package com.application.configuration.fastapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
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
public class FastApiGateway {

    private final RestClient restClient;
    private final RetryTemplate retryTemplate;

    /** Se crean en {@link FastApiClientConfig}: {@code fastApiGateway} y {@code fastApiPredictionGateway}. */
    public FastApiGateway(RestClient restClient, RetryTemplate retryTemplate) {
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
        return execute(operation, call, true);
    }

    /**
     * Igual que {@link #execute(String, Function)}; con {@code idempotente = false} (crear, alternar estado,
     * registrar una factura...) solo se reintenta si la petición no llegó a FastAPI (fallo de conexión):
     * si FastAPI ya la recibió, repetirla podría aplicarla dos veces.
     */
    public <T> T execute(String operation, Function<RestClient, T> call, boolean idempotente) {
        AtomicInteger attempt = new AtomicInteger();
        try {
            return retryTemplate.execute(() -> {
                int n = attempt.incrementAndGet();
                try {
                    return call.apply(restClient);
                } catch (RuntimeException e) {
                    RuntimeException toThrow = idempotente || FastApiClientConfig.isConnectFailure(e)
                            ? e : sinReintentos(e);
                    if (FastApiClientConfig.isTransient(toThrow)) {
                        log.warn("FastAPI '{}' intento {} fallido: {}", operation, n, e.getMessage());
                    }
                    throw toThrow;
                }
            });
        } catch (RetryException e) {
            throw translate(operation, attempt.get(), e.getCause() != null ? e.getCause() : e);
        }
    }

    private static RuntimeException sinReintentos(RuntimeException e) {
        if (e instanceof FastApiException.Unavailable unavailable) {
            return unavailable.sinReintentos();
        }
        if (e instanceof ResourceAccessException && !FastApiClientConfig.isReadTimeout(e)) {
            return new FastApiException.Unavailable("Fallo de red tras enviar la petición: " + e.getMessage(), null, e)
                    .sinReintentos();
        }
        return e;
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
            case ResourceAccessException rae when FastApiClientConfig.isReadTimeout(rae) -> {
                log.error("FastAPI '{}' no respondió a tiempo (timeout de lectura, sin reintentar): {}",
                        operation, rae.getMessage());
                yield new FastApiException.Unavailable(
                        "El servicio FastAPI tardó demasiado en responder (" + operation + ")", null, rae);
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
