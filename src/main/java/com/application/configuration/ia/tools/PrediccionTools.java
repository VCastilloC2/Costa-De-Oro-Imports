package com.application.configuration.ia.tools;

import com.application.presentation.dto.DatosGraficaResponse;
import com.application.presentation.dto.venta.request.VentaRequest;
import com.application.service.interfaces.PrediccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PrediccionTools {

    private final PrediccionService prediccionService;


    @Tool(
            name = "predecir_venta",
            description = "Predice un valor basado en la solicitud de venta"
    )
    public double predecir(
            @ToolParam(description = "Solicitud de venta para predicción") VentaRequest request) throws Exception {
        return prediccionService.predecir(request);
    }

    @Tool(
            name = "obtener_datos_para_grafica",
            description = "Obtiene los datos para la gráfica de predicción"
    )
    public String obtenerDatosParaGrafica() {
        try {
            DatosGraficaResponse response = prediccionService.obtenerDatosParaGrafica();
            return """
                    Datos para gráfica:
                    """ + (response != null ? response.toString() : "No data") + """
                    """;
        } catch (Exception e) {
            return "Error al obtener datos para gráfica: " + e.getMessage();
        }
    }

    @Tool(
            name = "generar_prediccion_2026",
            description = "Genera la predicción para el año 2026"
    )
    public String generarPrediccion2026() {
        try {
            Map<String, Object> response = prediccionService.generarPrediccion2026();
            return """
                    Predicción 2026:
                    """ + (response != null ? response.toString() : "No data") + """
                    """;
        } catch (Exception e) {
            return "Error al generar predicción para 2026: " + e.getMessage();
        }
    }

}