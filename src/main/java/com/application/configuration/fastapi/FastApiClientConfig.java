package com.application.configuration.fastapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Cliente HTTP hacia FastAPI.
 *
 * <p>Toda la configuración (URL, API key, timeouts, reintentos) sale de {@link FastApiProperties},
 * que a su vez se alimenta de {@code .env}. Cambiar de entorno local a cloud es solo cuestión de
 * variables de entorno / perfil activo: el código no cambia.</p>
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FastApiProperties.class)
public class FastApiClientConfig {

    static final String API_KEY_HEADER = "X-API-Key";
    private static final int MAX_ERROR_BODY_CHARS = 500;

    /*
     * Se usa RestClient.builder() propio: en Spring Boot 4 el bean RestClient.Builder solo existe
     * si se añade spring-boot-starter-restclient, y este cliente no necesita su personalización global.
     * Los conversores JSON (Jackson) se registran igualmente por defecto.
     */
    @Bean
    RestClient fastApiRestClient(FastApiProperties props) {
        log.info("FastAPI configurado: baseUrl={}, connectTimeout={}, readTimeout={}, predictionReadTimeout={}, retry={}",
                props.baseUrl(), props.connectTimeout(), props.readTimeout(), props.predictionReadTimeout(),
                props.retry());
        if (props.apiKey().isEmpty()) {
            log.warn("FASTAPI_API_KEY está vacía: FastAPI rechazará las peticiones con 401");
        }
        return buildClient(props, props.readTimeout());
    }

    /** Mismo cliente pero con timeout largo: las predicciones entrenan el modelo en FastAPI. */
    @Bean
    RestClient fastApiPredictionRestClient(FastApiProperties props) {
        return buildClient(props, props.predictionReadTimeout());
    }

    /** Gateway por defecto (dashboard y llamadas rápidas). */
    @Bean
    @Primary
    FastApiGateway fastApiGateway(RestClient fastApiRestClient, RetryTemplate fastApiRetryTemplate) {
        return new FastApiGateway(fastApiRestClient, fastApiRetryTemplate);
    }

    /** Gateway para predicciones (timeout de lectura largo). */
    @Bean
    FastApiGateway fastApiPredictionGateway(RestClient fastApiPredictionRestClient,
                                            RetryTemplate fastApiRetryTemplate) {
        return new FastApiGateway(fastApiPredictionRestClient, fastApiRetryTemplate);
    }

    private static RestClient buildClient(FastApiProperties props, Duration readTimeout) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(props.connectTimeout())
                .followRedirects(HttpClient.Redirect.NEVER) // nunca reenviar la API key a otro host
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(readTimeout);
        return configure(RestClient.builder().requestFactory(requestFactory), props).build();
    }

    /** Cabeceras, logging y traducción de errores (separado del transporte para poder testearlo). */
    static RestClient.Builder configure(RestClient.Builder builder, FastApiProperties props) {
        return builder
                .baseUrl(props.baseUrl().toString())
                .defaultHeader(API_KEY_HEADER, props.apiKey())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "costa-de-oro-spring/1.0")
                .requestInterceptor(new FastApiLoggingInterceptor())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    int status = response.getStatusCode().value();
                    String detail = readBodySnippet(response.getBody());
                    String where = request.getMethod() + " " + request.getURI().getPath();
                    // 429 y 5xx son transitorios -> se reintentan; el resto de 4xx no.
                    if (status == 429 || status >= 500) {
                        throw new FastApiException.Unavailable(
                                "FastAPI respondió " + status + " en " + where + ": " + detail, status, null);
                    }
                    throw new FastApiException.Rejected(
                            "FastAPI rechazó " + where + " con " + status + ": " + detail, status);
                });
    }

    @Bean
    RetryTemplate fastApiRetryTemplate(FastApiProperties props) {
        FastApiProperties.Retry retry = props.retry();
        RetryPolicy policy = RetryPolicy.builder()
                .maxRetries(retry.maxRetries())
                .delay(retry.delay())
                .multiplier(retry.multiplier())
                .maxDelay(retry.maxDelay())
                .predicate(FastApiClientConfig::isTransient)
                .build();
        return new RetryTemplate(policy);
    }

    /**
     * Solo se reintenta lo que puede salir bien al repetirlo: red caída, timeout de CONEXIÓN, 5xx y 429.
     * Un timeout de LECTURA no se reintenta: FastAPI sigue procesando la petición anterior y repetirla
     * solo duplica el trabajo (p. ej. reentrenar el modelo) y multiplica la espera del usuario.
     */
    static boolean isTransient(Throwable ex) {
        if (ex instanceof FastApiException.Unavailable) {
            return true;
        }
        return ex instanceof ResourceAccessException && !isReadTimeout(ex);
    }

    static boolean isReadTimeout(Throwable ex) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof HttpConnectTimeoutException) {
                return false;
            }
            if (t instanceof HttpTimeoutException || t instanceof SocketTimeoutException) {
                return true;
            }
        }
        return false;
    }

    private static String readBodySnippet(InputStream body) {
        try (body) {
            String text = new String(body.readNBytes(MAX_ERROR_BODY_CHARS), StandardCharsets.UTF_8);
            return text.isBlank() ? "<sin cuerpo>" : text.replaceAll("[\\r\\n]+", " ").strip();
        } catch (Exception e) {
            return "<cuerpo ilegible>";
        }
    }
}
