package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.DashboardResponse;
import com.application.presentation.dto.grafica.GraficaIngresosGastosResponse;
import com.application.presentation.dto.grafica.columnasApiladas.StockComprasResponse;
import com.application.presentation.dto.grafica.comprasRecientes.CompraResumenResponse;
import com.application.presentation.dto.grafica.historicoVentas.HistoricoVentasResponse;
import com.application.presentation.dto.grafica.productosMasVendidos.ProductoMasVendidoResponse;
import com.application.presentation.dto.grafica.progresoActual.EstadisticasGeneralesDTO;
import com.application.presentation.dto.grafica.ventasCiudades.MapaVentasResponse;
import com.application.presentation.dto.grafica.ventasTotales.VentasTotalesResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.CompraService;
import com.application.service.interfaces.GraficaService;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/graficas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DashboardRestController {

    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;
    private final CompraService compraService;
    private final GraficaService graficaService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboardController(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());

        String urlImagenUsuario =
                cloudinaryService.getImagenUrl(usuario.getImagen());

        // Ingresos Anuales
        Double ingresoAnual =
                compraService.getIngresoAnual();

        // Compras Anuales
        Long comprasAnuales =
                compraService.getTotalCompasAnuales();

        // Total Clientes
        Long totalClientes =
                usuarioService.getTotalClientes();

        // Estadísticas generales
        EstadisticasGeneralesDTO estadisticas =
                graficaService.obtenerEstadisticasGenerales();

        // Productos más vendidos
        List<ProductoMasVendidoResponse> productosMasVendidos =
                graficaService.getTopProductosMasVendidos();

        // Compras recientes
        List<CompraResumenResponse> comprasRecientes =
                graficaService.getComprasRecientes();

        DashboardResponse response = new DashboardResponse(
                usuario,
                urlImagenUsuario,
                ingresoAnual,
                comprasAnuales,
                totalClientes,
                estadisticas,
                productosMasVendidos,
                comprasRecientes
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ingresos-gastos")
    public ResponseEntity<GraficaIngresosGastosResponse> obtenerDatosGraficaIngresosGastos() {
        try {
            GraficaIngresosGastosResponse datos = graficaService.obtenerDatosGraficaIngresosGastos();
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ventas-totales")
    public ResponseEntity<VentasTotalesResponse> obtenerVentasTotales() {
        try {
            VentasTotalesResponse datos = graficaService.obtenerVentasTotalesUltimos12Meses();
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stock-compras")
    public ResponseEntity<StockComprasResponse> obtenerDatosStockCompras() {
        try {
            StockComprasResponse datos = graficaService.obtenerDatosStockCompras();
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/mapa-ventas")
    public ResponseEntity<MapaVentasResponse> obtenerDatosMapaVentas() {
        try {
            MapaVentasResponse datos = graficaService.obtenerVentasParaMapa();
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/historico-ventas")
    public ResponseEntity<HistoricoVentasResponse> obtenerHistoricoVentas() {
        try {
            HistoricoVentasResponse datos = graficaService.obtenerHistoricoVentas();
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
