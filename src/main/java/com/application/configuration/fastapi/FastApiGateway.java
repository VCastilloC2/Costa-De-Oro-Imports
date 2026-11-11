package com.application.configuration.fastapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Punto único de salida hacia FastAPI: aplica reintentos con backoff, registra cada intento fallido
 * y traduce cualquier error técnico a {@link FastApiException}. Los adaptadores concretos
 * (p. ej. {@code PrediccionApiClient}) solo describen la petición.
 */
@Slf4j
@Component
public class FastApiGateway {

    private final RestClient restClient;
    private final RetryTemplate retryTemplate;

    public FastApiGateway(@Qualifier("fastApiRestClient") RestClient restClient,
                          @Qualifier("fastApiRetryTemplate") RetryTemplate retryTemplate) {
        this.restClient = restClient;
        this.retryTemplate = retryTemplate;
    }

    /**
     * Ejecuta una llamada con reintentos.
     *
     * @param operation nombre legible para los logs (p. ej. "prediccion.grafica")
     * @param call      petición a realizar con el {@link RestClient} ya configurado
     * @throws FastApiException.Unavailable si el servicio no respondió tras agotar los reintentos
     * @throws FastApiException.Rejected    si el servicio devolvió un 4xx
     */
    public <T> T execute(String operation, Function<RestClient, T> call) {
        AtomicInteger attempt = new AtomicInteger();
        try {
            return retryTemplate.execute(() -> {
                int n = attempt.incrementAndGet();
                try {
                    return call.apply(restClient);
                } catch (RuntimeException e) {
                    if (FastApiClientConfig.isTransient(e)) {
                        log.warn("FastAPI '{}' intento {} fallido: {}", operation, n, e.getMessage());
                    }
                    throw e;
                }
            });
        } catch (RetryException e) {
            throw translate(operation, attempt.get(), e.getCause() != null ? e.getCause() : e);
        }
    }

    private static FastApiException translate(String operation, int attempts, Throwable cause) {
        return switch (cause) {
            case FastApiException.Rejected rejected -> {
                log.error("FastAPI '{}' rechazado (status={}): {}", operation, rejected.getStatus(), rejected.getMessage());
                yield rejected;
            }
            case FastApiException.Unavailable unavailable -> {
                log.error("FastAPI '{}' no disponible tras {} intento(s): {}", operation, attempts, unavailable.getMessage());
                yield unavailable;
            }
            case ResourceAccessException rae -> {
                log.error("FastAPI '{}' inaccesible tras {} intento(s) (caído o timeout): {}",
                        operation, attempts, rae.getMessage());
                yield new FastApiException.Unavailable(
                        "El servicio FastAPI no responde (" + operation + ")", null, rae);
            }
            default -> {
                log.error("FastAPI '{}' error inesperado tras {} intento(s)", operation, attempts, cause);
                yield new FastApiException.Unavailable(
                        "Error inesperado llamando a FastAPI (" + operation + ")", null, cause);
            }
        };
    }
}
