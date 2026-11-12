package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.grafica.DatosPanelResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.GraficaService;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin/principal")
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;
    private final GraficaService graficaService;

    @GetMapping({"/", ""})
    public String Dashboard(@AuthenticationPrincipal CustomUserPrincipal principal,
                            Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        // Tarjetas, progreso, top productos y compras recientes: FastAPI (/api/graficas/dashboard)
        DatosPanelResponse panel = graficaService.obtenerDatosPanel();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("ingresoAnual", panel.ingresoAnual());
        model.addAttribute("comprasAnuales", panel.comprasAnuales());
        model.addAttribute("totalClientes", panel.totalClientes());
        model.addAttribute("estadisticas", panel.estadisticas());
        model.addAttribute("productosMasVendidos", panel.productosMasVendidos());
        model.addAttribute("comprasRecientes", panel.comprasRecientes());

        return "Dashboard";
    }

}