package com.application.configuration.ia.tools;

import com.application.service.interfaces.GraficaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GraficaTools {

    private final GraficaService graficaService;

    /*

    @Tool(
            name = "obtener_datos_grafica_ingresos_gastos",
            description = "Obtiene los datos para la gráfica de ingresos y gastos"
    )
    public String obtenerDatosGraficaIngresosGastos() {
        try {
            GraficaIngresosGastosResponse response = graficaService.obtenerDatosGraficaIngresosGastos();
            return """
                    Gráfica de Ingresos y Gastos:
                    Ingresos totales: %.2f
                    Gastos totales: %.2f
                    Utilidad: %.2f
                    """.formatted(
                    response.getIngresosTotales(),
                    response.getGastosTotales(),
                    response.getUtilidad()
            );
        } catch (Exception e) {
            return "Error al obtener datos para gráfica de ingresos y gastos: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_ventas_totales_ultimos_12_meses",
            description = "Obtiene las ventas totales de los últimos 12 meses"
    )
    public String obtenerVentasTotalesUltimos12Meses() {
        try {
            VentasTotalesResponse response = graficaService.obtenerVentasTotalesUltimos12Meses();
            return """
                    Ventas totales de los últimos 12 meses:
                    Total: %.2f
                    """.formatted(
                    response.getTotal()
            );
        } catch (Exception e) {
            return "Error al obtener ventas totales de los últimos 12 meses: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_datos_stock_compras",
            description = "Obtiene los datos para la gráfica de stock y compras"
    )
    public String obtenerDatosStockCompras() {
        try {
            StockComprasResponse response = graficaService.obtenerDatosStockCompras();
            return """
                    Gráfica de Stock y Compras:
                    Stock total: %d
                    Compras totales: %d
                    """.formatted(
                    response.getStockTotal(),
                    response.getComprasTotales()
            );
        } catch (Exception e) {
            return "Error al obtener datos para gráfica de stock y compras: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_ventas_para_mapa",
            description = "Obtiene los datos de ventas para el mapa"
    )
    public String obtenerVentasParaMapa() {
        try {
            MapaVentasResponse response = graficaService.obtenerVentasParaMapa();
            return """
                    Ventas para mapa:
                    """ + (response != null ? response.toString() : "No data") + """
                    """;
        } catch (Exception e) {
            return "Error al obtener ventas para mapa: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_historico_ventas",
            description = "Obtiene el histórico de ventas"
    )
    public String obtenerHistoricoVentas() {
        try {
            HistoricoVentasResponse response = graficaService.obtenerHistoricoVentas();
            return """
                    Histórico de ventas:
                    """ + (response != null ? response.toString() : "No data") + """
                    """;
        } catch (Exception e) {
            return "Error al obtener histórico de ventas: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_estadisticas_generales",
            description = "Obtiene las estadísticas generales"
    )
    public String obtenerEstadisticasGenerales() {
        try {
            EstadisticasGeneralesDTO response = graficaService.obtenerEstadisticasGenerales();
            return """
                    Estadísticas generales:
                    Total usuarios: %d
                    Total clientes: %d
                    Total proveedores: %d
                    Total productos: %d
                    Total compras: %d
                    Total ventas: %.2f
                    """.formatted(
                    response.getTotalUsuarios(),
                    response.getTotalClientes(),
                    response.getTotalProveedores(),
                    response.getTotalProductos(),
                    response.getTotalCompras(),
                    response.getTotalVentas()
            );
        } catch (Exception e) {
            return "Error al obtener estadísticas generales: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_top_productos_mas_vendidos",
            description = "Obtiene los top productos más vendidos"
    )
    public String getTopProductosMasVendidos() {
        try {
            List<ProductoMasVendidoResponse> productos = graficaService.getTopProductosMasVendidos();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos más vendidos.";
            }
            StringBuilder result = new StringBuilder("Top productos más vendidos:\n\n");
            for (ProductoMasVendidoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Total vendido: %d\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getTotalVendido()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener top productos más vendidos: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_compras_recientes",
            description = "Obtiene las compras recientes"
    )
    public String getComprasRecientes() {
        try {
            List<CompraResumenResponse> compras = graficaService.getComprasRecientes();
            if (compras == null || compras.isEmpty()) {
                return "No hay compras recientes.";
            }
            StringBuilder result = new StringBuilder("Compras recientes:\n\n");
            for (CompraResumenResponse c : compras) {
                result.append(String.format("ID: %d - Fecha: %s - Total: %.2f\n",
                        c.getCompraId(),
                        c.getFecha() != null ? c.getFecha().toString() : "No especificado",
                        c.getTotal()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener compras recientes: " + e.getMessage();
        }
    }

     */
}