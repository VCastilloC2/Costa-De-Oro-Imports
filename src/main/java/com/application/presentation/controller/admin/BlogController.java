package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.configuration.fastapi.FastApiAdmin;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.historia.request.HistoriaCreateRequest;
import com.application.presentation.dto.historia.response.HistoriaResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.historia.HistoriaService;
import com.application.service.interfaces.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
public class BlogController {

    private final UsuarioService usuarioService;
    private final HistoriaService historiaService;
    private final CloudinaryService cloudinaryService;
    private final FastApiAdmin fastApiAdmin;

    @GetMapping({"", "/"})
    public String DashboardBlog(@AuthenticationPrincipal CustomUserPrincipal principal,
                                @RequestParam(value = "mensaje", required = false) String mensaje,
                                @RequestParam(value = "success", required = false) Boolean success,
                                Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        List<HistoriaResponse> historiaList = fastApiAdmin.leer("blog.listar", "/admin/blog",
                new ParameterizedTypeReference<List<HistoriaResponse>>() {
                }, historiaService::getHistorias);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("historiaList", historiaList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);

        return "DashboardBlog";
    }

    @GetMapping("/create-blog")
    public String agregarBlog(@AuthenticationPrincipal CustomUserPrincipal principal,
                              Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        return "AgregarBlog";
    }

    @PostMapping("/create-blog")
    public String addBlog(@ModelAttribute @Valid HistoriaCreateRequest historiaRequest) {
        BaseResponse response = fastApiAdmin.escribir("blog.crear", HttpMethod.POST, "/admin/blog",
                () -> cuerpo(historiaRequest), () -> historiaService.createHistoria(historiaRequest));
        return AdminVistas.redirect("/admin/blog/", response);
    }

    @GetMapping("/update-blog/{id}")
    public String updateBlog(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserPrincipal principal,
                                 Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());
        HistoriaResponse historiaResponse = fastApiAdmin.leer("blog.obtener", "/admin/blog/{id}",
                HistoriaResponse.class, () -> historiaService.getHistoriaResponseById(id), id);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("historiaResponse", historiaResponse);
        model.addAttribute("historiaId", id);
        return "EditarBlog";
    }

    @PostMapping("/update-blog/{id}")
    public String updateBlog(@PathVariable Long id, @ModelAttribute @Valid HistoriaCreateRequest historiaRequest) {
        BaseResponse response = fastApiAdmin.escribir("blog.actualizar", HttpMethod.PUT, "/admin/blog/{id}",
                () -> cuerpo(historiaRequest), () -> historiaService.updateHistoria(historiaRequest, id), id);
        return AdminVistas.redirect("/admin/blog/", response);
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String disableBlog(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("blog.estado", HttpMethod.PATCH, "/admin/blog/{id}/disable",
                null, () -> historiaService.changeEstadoHistoria(id), id);
        return AdminVistas.redirect("/admin/blog/", response);
    }

    @GetMapping("delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("blog.eliminar", HttpMethod.DELETE, "/admin/blog/{id}",
                null, () -> historiaService.deleteHistoria(id), id);
        return AdminVistas.redirect("/admin/blog/", response);
    }

    private Object cuerpo(HistoriaCreateRequest r) {
        return AdminVistas.cuerpo(
                "imagen", AdminVistas.subirImagen(cloudinaryService, r.imagen(), "imagen-blog"),
                "titulo", r.titulo(), "descripcion", r.descripcion(),
                "historiaCompleta", r.historiaCompleta(), "activo", r.activo());
    }
}
