package com.application.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Respuesta de GET /api/prediccion/datos-grafica (FastAPI). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GraficaMensualResponse(
        int anios,
        @JsonProperty("proyeccion_meses") int proyeccionMeses,
        List<PuntoMensual> serie,
        Map<String, Object> metricas,
        @JsonProperty("historial_entrenamiento") HistorialEntrenamiento historialEntrenamiento,
        @JsonProperty("variables_utilizadas") List<Map<String, String>> variablesUtilizadas,
        @JsonProperty("variables_prediccion") List<Map<String, String>> variablesPrediccion,
        List<String> herramientas,
        @JsonProperty("historial_datos") Map<String, Object> historialDatos,
        @JsonProperty("advertencia_modelo") String advertenciaModelo) {

    /**
     * tipo: real | parcial (mes en curso) | prediccion | sin_datos
     * real: venta real (null en meses futuros); prediccion: valor del modelo (null en sin_datos).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PuntoMensual(String fecha, String tipo, Double real, Double prediccion) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HistorialEntrenamiento(List<Integer> epocas, List<Double> loss) {
    }
}
