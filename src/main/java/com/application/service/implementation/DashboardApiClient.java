package com.application.service.implementation;

import com.application.presentation.exception.FastApiException;
import com.application.configuration.fastapi.FastApiGateway;
import com.application.presentation.dto.grafica.GraficaIngresosGastosResponse;
import com.application.presentation.dto.grafica.ventasTotales.VentasTotalesResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
        if (r == null || r.items() == null) {
            throw new FastApiException.Unavailable("FastAPI devolvió ventas-totales vacío", 200, null);
        }

        // FastAPI devuelve {items:[{estado,nombre,monto,...}], totalMonto}; convertirlo
        // al DTO que ya consume Dashboard.js (series, labels, totalVentas).
        double cobradas = 0;
        double noCobradas = 0;
        double pendientes = 0;
        for (VentaTotalItem item : r.items()) {
            if (item == null || item.monto() == null) continue;
            switch (item.estado() == null ? "" : item.estado()) {
                case "PAGADO" -> cobradas += item.monto();
                case "CANCELADO_RECHAZADO" -> noCobradas += item.monto();
                case "PENDIENTE" -> pendientes += item.monto();
                default -> { }
            }
        }
        List<Double> series = List.of(cobradas, noCobradas, pendientes);
        double total = r.totalMonto() != null ? r.totalMonto() : series.stream().mapToDouble(Double::doubleValue).sum();
        List<String> labels = new ArrayList<>(3);
        labels.add(formatLabel("Envíos", cobradas, total));
        labels.add(formatLabel("Reembolsos", noCobradas, total));
        labels.add(formatLabel("Pedidos", pendientes, total));
        return new VentasTotalesResponse(series, labels, total);
    }

    private static String formatLabel(String tipo, double valor, double total) {
        double porcentaje = total > 0 ? valor / total * 100 : 0;
        return String.format("%s $%s (%s%%)", tipo,
                new DecimalFormat("#,##0.00").format(valor),
                new DecimalFormat("0").format(porcentaje));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record IngresosGastos(List<String> categorias, List<Double> ingresos, List<Double> gastos,
                          Double totalIngresos, Double totalGastos) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record VentasTotales(List<VentaTotalItem> items, Double totalMonto) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record VentaTotalItem(String estado, String nombre, Double monto, Integer pedidos, Double montoAnterior) {
    }
}
