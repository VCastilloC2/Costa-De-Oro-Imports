package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.configuration.fastapi.FastApiAdmin;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.comentario.response.ComentarioResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.usuario.response.UsuarioGastoResponse;
import com.application.service.implementation.usuario.UsuarioServiceImpl;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.comentario.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/resenna")
@RequiredArgsConstructor
public class ResennaController {

    private final UsuarioServiceImpl usuarioService;
    private final ComentarioService comentarioService;
    private final CloudinaryService cloudinaryService;
    private final FastApiAdmin fastApiAdmin;

    @GetMapping({"", "/"})
    public String DashboardResenna(@AuthenticationPrincipal CustomUserPrincipal principal,
                                   @RequestParam(value = "mensaje", required = false) String mensaje,
                                   @RequestParam(value = "success", required = false) Boolean success,
                                   Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        List<ComentarioResponse> comentarioList = fastApiAdmin.leer("resenna.listar", "/admin/resenna",
                new ParameterizedTypeReference<List<ComentarioResponse>>() {
                }, comentarioService::getComentarios);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("comentarioList", comentarioList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);
        return "DashboardReseñas";
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String disableResenna(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("resenna.estado", HttpMethod.PATCH,
                "/admin/resenna/{id}/disable", null, () -> comentarioService.changeEstadoComentario(id), id);
        return AdminVistas.redirect("/admin/resenna/", response);
    }

    @GetMapping("delete/{id}")
    public String deleteResenna(@PathVariable Long id, @AuthenticationPrincipal CustomUserPrincipal principal) {
        BaseResponse response = fastApiAdmin.escribir("resenna.eliminar", HttpMethod.DELETE,
                "/admin/resenna/{id}", null, () -> comentarioService.deleteComentario(id, principal), id);
        return AdminVistas.redirect("/admin/resenna/", response);
    }

}