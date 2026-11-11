package com.application.presentation.dto.grafica;

import com.application.presentation.dto.grafica.comprasRecientes.CompraResumenResponse;
import com.application.presentation.dto.grafica.productosMasVendidos.ProductoMasVendidoResponse;
import com.application.presentation.dto.grafica.progresoActual.EstadisticasGeneralesDTO;

import java.util.List;

/** Datos del panel principal: los de {@code GET /api/graficas/dashboard} de FastAPI (sin el usuario). */
public record DatosPanelResponse(
        Double ingresoAnual,
        Long comprasAnuales,
        Long totalClientes,
        EstadisticasGeneralesDTO estadisticas,
        List<ProductoMasVendidoResponse> productosMasVendidos,
        List<CompraResumenResponse> comprasRecientes
) {
}
