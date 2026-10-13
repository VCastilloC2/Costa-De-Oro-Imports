package com.application.presentation.dto;

import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.grafica.comprasRecientes.CompraResumenResponse;
import com.application.presentation.dto.grafica.productosMasVendidos.ProductoMasVendidoResponse;
import com.application.presentation.dto.grafica.progresoActual.EstadisticasGeneralesDTO;
import java.util.List;

public record DashboardResponse(
        Usuario usuario,
        String urlImagenUsuario,
        Double ingresoAnual,
        Long comprasAnuales,
        Long totalClientes,
        EstadisticasGeneralesDTO estadisticas,
        List<ProductoMasVendidoResponse> productosMasVendidos,
        List<CompraResumenResponse> comprasRecientes
) {}