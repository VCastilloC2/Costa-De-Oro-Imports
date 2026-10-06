package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.DashboardResponse;
import com.application.presentation.dto.grafica.comprasRecientes.CompraResumenResponse;
import com.application.presentation.dto.grafica.productosMasVendidos.ProductoMasVendidoResponse;
import com.application.presentation.dto.grafica.progresoActual.EstadisticasGeneralesDTO;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.CompraService;
import com.application.service.interfaces.GraficaService;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/principal")
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;
    private final CompraService compraService;
    private final GraficaService graficaService;

    @GetMapping({"/", ""})
    public String Dashboard(@AuthenticationPrincipal CustomUserPrincipal principal,
                            Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        // Ingresos Anuales
        Double ingresoAnual = compraService.getIngresoAnual();

        // Compras Anuales
        Long comprasAnuales = compraService.getTotalCompasAnuales();

        // Total Cliente
        Long totalClientes = usuarioService.getTotalClientes();

        // Barra de Progreso Actual
        EstadisticasGeneralesDTO estadisticas = graficaService.obtenerEstadisticasGenerales();

        // Productos más Vendidos
        List<ProductoMasVendidoResponse> productoMasVendidos = graficaService.getTopProductosMasVendidos();

        // Compras recientes
        List<CompraResumenResponse> comprasRecientes = graficaService.getComprasRecientes();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("ingresoAnual", ingresoAnual);
        model.addAttribute("comprasAnuales", comprasAnuales);
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("productosMasVendidos", productoMasVendidos);
        model.addAttribute("comprasRecientes", comprasRecientes);

        return "Dashboard";
    }

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

}