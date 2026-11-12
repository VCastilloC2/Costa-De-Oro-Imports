package com.application.service.implementation;

import com.application.configuration.fastapi.FastApiException;
import com.application.configuration.fastapi.FastApiGateway;
import com.application.presentation.dto.grafica.GraficaIngresosGastosResponse;
import com.application.presentation.dto.grafica.ventasTotales.VentasTotalesResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador hacia las gráficas del dashboard en FastAPI ({@code /api/v1/dashboard/*}).
 * Devuelve los mismos DTO que consume {@code Dashboard.js}. Requiere que FASTAPI_API_KEY
 * tenga el permiso {@code predictions:read}.
 */
@Component
@RequiredArgsConstructor
public class DashboardApiClient {

    private final FastApiGateway fastApi;

    /** Ingresos (compras pagadas) y gastos (facturas activas) de los últimos {@code meses} meses. */
    public GraficaIngresosGastosResponse ingresosGastos(int meses) {
        IngresosGastos r = fastApi.execute("dashboard.ingresos-gastos", client -> client.get()
                .uri(uri -> uri.path("/api/v1/dashboard/ingresos-gastos").queryParam("meses", meses).build())
                .retrieve()
                .body(IngresosGastos.class));
        if (r == null || r.categorias() == null) {
            throw new FastApiException.Unavailable("FastAPI devolvió ingresos-gastos vacío", 200, null);
        }
        return new GraficaIngresosGastosResponse(r.categorias(), r.ingresos(), r.gastos());
    }

    /** Ventas por estado (Envíos, Reembolsos, Pedidos) de los últimos {@code meses} meses. */
    public VentasTotalesResponse ventasTotales(int meses) {
        VentasTotales r = fastApi.execute("dashboard.ventas-totales", client -> client.get()
                .uri(uri -> uri.path("/api/v1/dashboard/ventas-totales").queryParam("meses", meses).build())
                .retrieve()
                .body(VentasTotales.class));
        if (r == null || r.series() == null) {
            throw new FastApiException.Unavailable("FastAPI devolvió ventas-totales vacío", 200, null);
        }
        return new VentasTotalesResponse(r.series(), r.labels(), r.totalVentas());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IngresosGastos(List<String> categorias, List<Double> ingresos, List<Double> gastos,
                          Double totalIngresos, Double totalGastos) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record VentasTotales(List<Double> series, List<String> labels, Double totalVentas) {
    }
}
