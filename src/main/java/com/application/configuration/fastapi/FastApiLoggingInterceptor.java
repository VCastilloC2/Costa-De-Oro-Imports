package com.application.configuration.fastapi;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.UUID;

/**
 * Añade un {@code X-Request-Id} para correlacionar logs Spring <-> FastAPI y registra
 * método, ruta, estado y latencia de cada llamada. Nunca registra cabeceras (contienen la API key).
 */
@Slf4j
class FastApiLoggingInterceptor implements ClientHttpRequestInterceptor {

    static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String requestId = MDC.get("requestId");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        request.getHeaders().set(REQUEST_ID_HEADER, requestId);

        String target = request.getMethod() + " " + request.getURI().getPath();
        long start = System.nanoTime();
        try {
            ClientHttpResponse response = execution.execute(request, body);
            long ms = (System.nanoTime() - start) / 1_000_000;
            int status = response.getStatusCode().value();
            if (status >= 400) {
                log.warn("FastAPI {} -> {} en {} ms [requestId={}]", target, status, ms, requestId);
            } else {
                log.debug("FastAPI {} -> {} en {} ms [requestId={}]", target, status, ms, requestId);
            }
            return response;
        } catch (IOException e) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            log.warn("FastAPI {} sin respuesta tras {} ms [requestId={}]: {} - {}",
                    target, ms, requestId, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }
}
