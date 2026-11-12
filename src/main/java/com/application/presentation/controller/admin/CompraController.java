package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.configuration.fastapi.FastApiAdmin;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.compra.response.CompraDashboardResponse;
import com.application.presentation.dto.compra.response.DetalleCompraResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.CompraService;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Compras del panel admin: listado, detalle y cambio de estado en FastAPI ({@code /admin/compra}). */
@Controller
@RequestMapping("/admin/compra")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;
    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;
    private final FastApiAdmin fastApiAdmin;

    @GetMapping({"", "/"})
    public String DashboardCompra(@AuthenticationPrincipal CustomUserPrincipal principal,
                                  @RequestParam(value = "mensaje", required = false) String mensaje,
                                  Model model) {
        List<CompraDashboardResponse> compraList = fastApiAdmin.leer("compra.listar", "/admin/compra",
                new ParameterizedTypeReference<List<CompraDashboardResponse>>() {
                }, compraService::getCompraByOrderAndFechaDesc);
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("compras", compraList);
        model.addAttribute("mensaje", mensaje);
        return "DashboardCompra";
    }

    @GetMapping("/detalle-compra/{id}")
    public String DetalleCompra(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserPrincipal principal,
                                @RequestParam(name = "mensaje", required = false) String mensaje,
                                @RequestParam(name = "success", required = false) Boolean success,
                                Model model) {
        DetalleCompraResponse detalleCompra = fastApiAdmin.leer("compra.detalle", "/admin/compra/{id}",
                DetalleCompraResponse.class, () -> compraService.getDetalleCompra(id), id);
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("detalleCompra", detalleCompra);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);
        return "DetalleCompra";
    }

    @GetMapping("detalle-compra/cambiar-estado/{id}")
    public String changeEstadoCompra(@PathVariable Long id, @RequestParam String estado) {
        BaseResponse response = fastApiAdmin.escribir("compra.estado", HttpMethod.PATCH, "/admin/compra/{id}/estado",
                () -> AdminVistas.cuerpo("estado", estado), () -> compraService.changeEstadoCompra(id, estado), id);
        return AdminVistas.redirect("/admin/compra/detalle-compra/" + id, response);
    }
}
