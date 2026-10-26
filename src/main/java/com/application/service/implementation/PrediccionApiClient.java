package com.costadeoro.admin.prediccion; // AJUSTA al paquete de tu proyecto

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Cliente HTTP hacia la API Python (FastAPI).
 * Reemplaza PrediccionServiceImpl: Spring ya no calcula predicciones, solo las consulta.
 */
@Component
public class PrediccionApiClient {

    private final RestClient restClient;

    public PrediccionApiClient(@Value("${prediccion.api.url}") String baseUrl,
                               @Value("${prediccion.api.key:}") String apiKey) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3_000);   // ms
        factory.setReadTimeout(15_000);     // ms: el primer cálculo del día puede tardar

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader("X-API-Key", apiKey)
                .build();
    }

    /** Serie mensual para la gráfica: anios * 12 meses (+ proyeccionMeses). */
    public GraficaMensualResponse obtenerGrafica(int anios, int proyeccionMeses) {
        return restClient.get()
                .uri(uri -> uri.path("/api/prediccion/datos-grafica")
                        .queryParam("anios", anios)
                        .queryParam("proyeccion_meses", proyeccionMeses)
                        .build())
                .retrieve()
                .body(GraficaMensualResponse.class);
    }

    /** Venta total prevista para un mes concreto. */
    public double predecirMes(int anio, int mes) {
        RespuestaPrediccion respuesta = restClient.post()
                .uri("/api/prediccion/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("anno", anio, "mes", mes))
                .retrieve()
                .body(RespuestaPrediccion.class);

        if (respuesta == null || respuesta.resultado() == null) {
            throw new IllegalStateException("La API de predicciones no devolvió un resultado");
        }
        return respuesta.resultado();
    }

    /** Respuesta de POST /api/prediccion/generar (mismos nombres que el JSON de Python). */
    public record RespuestaPrediccion(Double resultado, String mensaje, boolean success) {
    }
}
