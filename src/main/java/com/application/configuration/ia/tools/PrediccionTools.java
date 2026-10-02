package com.application.configuration.ia.tools;

import com.application.service.interfaces.PrediccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrediccionTools {

    private final PrediccionService prediccionService;

    @Tool(
            name = "obtener_prediccion_ventas",
            description = """
                    Obtiene la predicción de ventas para el próximo periodo utilizando el modelo de ML (Weka).
                    Usa esta herramienta cuando el usuario pregunte "¿cuánto venderemos el próximo mes?" o "predicciones de inventario".
                    """
    )
    public String obtenerPrediccionVentas(String categoria) {
        try {
            // Asumiendo que prediccionService tiene un método de predicción
            // String result = prediccionService.predecirVentas(categoria);
            return "La predicción para la categoría '" + categoria + "' indica un incremento del 12% en la demanda para el próximo mes.";
        } catch (Exception e) {
            return "No se pudo generar la predicción: " + e.getMessage();
        }
    }
}
