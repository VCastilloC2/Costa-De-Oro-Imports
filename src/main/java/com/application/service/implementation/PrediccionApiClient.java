package com.application.service.implementation;

import com.application.configuration.fastapi.FastApiException;
import com.application.configuration.fastapi.FastApiGateway;
import com.application.presentation.dto.GraficaMensualResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Adaptador hacia la API de predicciones en FastAPI.
 *
 * <p>URL, credenciales, timeouts y reintentos se configuran en {@code FastApiClientConfig}
 * (variables {@code FASTAPI_*} del {@code .env}); aquí solo se describen las peticiones.
 * Autenticación: cabecera {@code X-API-Key} con una clave de servicio emitida por FastAPI
 * ({@code POST /api/v1/predictions/clients}) con permisos {@code predictions:read} y
 * {@code predictions:generate}.</p>
 */
@Component
@RequiredArgsConstructor
public class PrediccionApiClient {

    private final FastApiGateway fastApi;

    /** Serie mensual para la gráfica: anios * 12 meses (+ proyeccionMeses). */
    public GraficaMensualResponse obtenerGrafica(int anios, int proyeccionMeses) {
        GraficaMensualResponse respuesta = fastApi.execute("prediccion.grafica", client -> client.get()
                .uri(uri -> uri.path("/api/prediccion/datos-grafica")
                        .queryParam("anios", anios)
                        .queryParam("proyeccion_meses", proyeccionMeses)
                        .build())
                .retrieve()
                .body(GraficaMensualResponse.class));

        if (respuesta == null) {
            throw new FastApiException.Unavailable("FastAPI devolvió una gráfica vacía", 200, null);
        }
        return respuesta;
    }

    /** Venta total prevista para un mes concreto. */
    public double predecirMes(int anio, int mes) {
        RespuestaPrediccion respuesta = fastApi.execute("prediccion.mes", client -> client.post()
                .uri("/api/prediccion/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SolicitudPrediccion(anio, mes))
                .retrieve()
                .body(RespuestaPrediccion.class));

        if (respuesta == null || !respuesta.success() || respuesta.resultado() == null) {
            throw new FastApiException.Unavailable("La API de predicciones no devolvió un resultado", 200, null);
        }
        return respuesta.resultado();
    }

    /** Cuerpo de POST /api/prediccion/generar. */
    public record SolicitudPrediccion(int anno, int mes) {
    }

    /** Respuesta de POST /api/prediccion/generar (mismos nombres que el JSON de Python). */
    public record RespuestaPrediccion(Double resultado, String mensaje, boolean success) {
    }
}
