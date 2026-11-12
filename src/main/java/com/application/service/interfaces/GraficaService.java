package com.application.service.interfaces;

import com.application.presentation.dto.grafica.DatosPanelResponse;
import com.application.presentation.dto.grafica.GraficaIngresosGastosResponse;
import com.application.presentation.dto.grafica.columnasApiladas.StockComprasResponse;
import com.application.presentation.dto.grafica.comprasRecientes.CompraResumenResponse;
import com.application.presentation.dto.grafica.historicoVentas.HistoricoVentasResponse;
import com.application.presentation.dto.grafica.productosMasVendidos.ProductoMasVendidoResponse;
import com.application.presentation.dto.grafica.progresoActual.EstadisticasGeneralesDTO;
import com.application.presentation.dto.grafica.ventasCiudades.MapaVentasResponse;
import com.application.presentation.dto.grafica.ventasTotales.VentasTotalesResponse;

import java.util.List;

public interface GraficaService {

    DatosPanelResponse obtenerDatosPanel();

    GraficaIngresosGastosResponse obtenerDatosGraficaIngresosGastos();
    VentasTotalesResponse obtenerVentasTotalesUltimos12Meses();
    StockComprasResponse obtenerDatosStockCompras();
    MapaVentasResponse obtenerVentasParaMapa();
    HistoricoVentasResponse obtenerHistoricoVentas();
    EstadisticasGeneralesDTO obtenerEstadisticasGenerales();
    List<ProductoMasVendidoResponse> getTopProductosMasVendidos();
    List<CompraResumenResponse> getComprasRecientes();
}
