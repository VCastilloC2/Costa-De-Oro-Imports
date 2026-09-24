package com.application.configuration.ia.tools;

import com.application.service.interfaces.GraficaService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GraficaTools {

    private final GraficaService graficaService;

    @Tool(
            name = "generar_resumen_grafico",
            description = """
                    Genera un resumen textual de las gráficas de rendimiento actuales.
                    Usa esta herramienta cuando el usuario pregunte por tendencias, totales de ventas o comparativas visuales.
                    """
    )
    public String generarResumenGrafico(String tipoGrafica) {
        // En una implementación real, esto llamaría a GraficaService para obtener datos procesados
        // y devolvería un resumen legible para el LLM.
        return "Análisis de gráfica (" + graficaService.obtenerVentasTotalesUltimos12Meses() + "): El sistema muestra una tendencia creciente en las ventas de cervezas artesanales en el último trimestre, con un pico en diciembre.";
    }

    private String haciendo(String tipo) {
        if (tipo == null) return "General";
        return tipo.toLowerCase();
    }
}
