package com.application.configuration.fastapi;

import com.application.presentation.dto.GraficaMensualResponse;
import com.application.service.implementation.PrediccionApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FastApiGatewayTest {

    private static final String BASE = "http://fastapi.test";
    private static final String GRAFICA = BASE + "/api/prediccion/datos-grafica?anios=6&proyeccion_meses=0";
    private static final String GRAFICA_JSON = """
            {"anios":6,"proyeccion_meses":0,
             "serie":[{"fecha":"2026-01","tipo":"real","real":10.0,"prediccion":11.0}],
             "metricas":{}}""";

    private MockRestServiceServer server;
    private PrediccionApiClient client;

    @BeforeEach
    void setUp() {
        FastApiProperties props = new FastApiProperties(URI.create(BASE), "cdo_pk_test", false,
                Duration.ofSeconds(1), Duration.ofSeconds(1),
                new FastApiProperties.Retry(2, Duration.ofMillis(1), 1.0, Duration.ofMillis(5)));
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = FastApiClientConfig.configure(builder, props).build();
        FastApiGateway gateway = new FastApiGateway(restClient, new FastApiClientConfig().fastApiRetryTemplate(props));
        client = new PrediccionApiClient(gateway);
    }

    @Test
    void enviaApiKeyYRequestId() {
        server.expect(requestTo(GRAFICA))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-API-Key", "cdo_pk_test"))
                .andExpect(request -> assertThat(request.getHeaders().getFirst("X-Request-Id")).isNotBlank())
                .andRespond(withSuccess(GRAFICA_JSON, MediaType.APPLICATION_JSON));

        GraficaMensualResponse grafica = client.obtenerGrafica(6, 0);

        assertThat(grafica.serie()).hasSize(1);
        server.verify();
    }

    @Test
    void reintentaErroresTransitoriosYLuegoTieneExito() {
        server.expect(requestTo(GRAFICA)).andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        server.expect(requestTo(GRAFICA)).andRespond(withException(new IOException("connection refused")));
        server.expect(requestTo(GRAFICA)).andRespond(withSuccess(GRAFICA_JSON, MediaType.APPLICATION_JSON));

        assertThat(client.obtenerGrafica(6, 0).anios()).isEqualTo(6);
        server.verify();
    }

    @Test
    void agotaReintentosYLanzaUnavailable() {
        server.expect(ExpectedCount.times(3), requestTo(GRAFICA)).andRespond(withStatus(HttpStatus.BAD_GATEWAY));

        assertThatThrownBy(() -> client.obtenerGrafica(6, 0))
                .isInstanceOf(FastApiException.Unavailable.class)
                .extracting(e -> ((FastApiException) e).getStatus()).isEqualTo(502);
        server.verify();
    }

    @Test
    void noReintentaErroresDeCliente() {
        server.expect(ExpectedCount.once(), requestTo(GRAFICA))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"detail\":\"API Key inválida o revocada\"}"));

        assertThatThrownBy(() -> client.obtenerGrafica(6, 0))
                .isInstanceOf(FastApiException.Rejected.class)
                .hasMessageContaining("401");
        server.verify();
    }

    @Test
    void propiedadesCloudExigenHttpsYApiKey() {
        var retry = new FastApiProperties.Retry(0, Duration.ZERO, 1.0, Duration.ofSeconds(1));
        assertThatThrownBy(() -> new FastApiProperties(URI.create("http://api.example.com"), "k", true,
                Duration.ofSeconds(1), Duration.ofSeconds(1), retry))
                .hasMessageContaining("https");
        assertThatThrownBy(() -> new FastApiProperties(URI.create("https://api.example.com"), " ", true,
                Duration.ofSeconds(1), Duration.ofSeconds(1), retry))
                .hasMessageContaining("FASTAPI_API_KEY");
        assertThat(new FastApiProperties(URI.create("https://api.example.com"), "k", true,
                Duration.ofSeconds(1), Duration.ofSeconds(1), retry).toString()).doesNotContain("=k");
    }
}
