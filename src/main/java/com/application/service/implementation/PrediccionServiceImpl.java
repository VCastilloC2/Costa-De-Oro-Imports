package com.application.service.implementation;

import com.application.presentation.dto.DatosGraficaResponse;
import com.application.presentation.dto.GraficaMensualResponse;
import com.application.presentation.dto.ProyeccionMensualRequest;
import com.application.presentation.dto.venta.request.VentaRequest;
import com.application.service.interfaces.PrediccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Adaptador de compatibilidad: todo el cálculo predictivo se ejecuta en FastAPI. */
@Service
@RequiredArgsConstructor
public class PrediccionServiceImpl implements PrediccionService {
    private static final String[] MESES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    private final PrediccionApiClient predicciones;

    @Override
    public double predecir(VentaRequest request) {
        return predicciones.predecirMes(request.anno(), request.mes());
    }

    @Override
    public DatosGraficaResponse obtenerDatosParaGrafica() {
        GraficaMensualResponse response = predicciones.obtenerGrafica(6, 0);
        Map<String, double[]> ventas = new HashMap<>();
        for (GraficaMensualResponse.PuntoMensual point : response.serie()) {
            if (point.real() == null || point.fecha() == null || point.fecha().length() < 7) continue;
            String year = point.fecha().substring(0, 4);
            int month = Integer.parseInt(point.fecha().substring(5, 7));
            ventas.computeIfAbsent(year, ignored -> new double[12])[month - 1] = point.real();
        }
        return new DatosGraficaResponse(ventas, Map.of("modelo", "FastAPI"), List.of());
    }

    @Override
    public Map<String, Object> generarPrediccion2026() {
        GraficaMensualResponse response = predicciones.obtenerGrafica(6, 12);
        List<ProyeccionMensualRequest> forecast = new ArrayList<>();
        for (GraficaMensualResponse.PuntoMensual point : response.serie()) {
            if (point.prediccion() == null || point.fecha() == null || !point.fecha().startsWith("2026-")) continue;
            int month = Integer.parseInt(point.fecha().substring(5, 7));
            forecast.add(new ProyeccionMensualRequest(month, MESES[month - 1], point.prediccion()));
        }
        return Map.of("proyeccion", forecast, "success", true, "mensaje", "Predicción generada por FastAPI");
    }
}
