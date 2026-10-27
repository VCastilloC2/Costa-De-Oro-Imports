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
        Map<String, Object> metricas) {

    /**
     * tipo: real | parcial (mes en curso) | prediccion | sin_datos
     * real: venta real (null en meses futuros); prediccion: valor del modelo.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PuntoMensual(String fecha, String tipo, Double real, double prediccion) {
    }
}
