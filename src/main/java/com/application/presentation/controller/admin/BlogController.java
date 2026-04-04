package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.historia.request.HistoriaCreateRequest;
import com.application.presentation.dto.historia.response.HistoriaResponse;
import com.application.presentation.dto.usuario.request.CreateClienteRequest;
import com.application.presentation.dto.usuario.response.ClienteResponse;
import com.application.presentation.dto.usuario.response.UsuarioGastoResponse;
import com.application.service.implementation.usuario.UsuarioServiceImpl;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.historia.HistoriaService;
import com.application.service.interfaces.usuario.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
public class BlogController {

    private final UsuarioService usuarioService;
    private final HistoriaService historiaService;
    private final CloudinaryService cloudinaryService;

    @GetMapping({"", "/"})
    public String DashboardBlog(@AuthenticationPrincipal CustomUserPrincipal principal,
                                @RequestParam(value = "mensaje", required = false) String mensaje,
                                @RequestParam(value = "success", required = false) Boolean success,
                                Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        List<HistoriaResponse> historiaList = historiaService.getHistorias();

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
        BaseResponse response = historiaService.createHistoria(historiaRequest);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/blog/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("/update-blog/{id}")
    public String updateBlog(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserPrincipal principal,
                                 Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());
        HistoriaResponse historiaResponse = historiaService.getHistoriaResponseById(id);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("historiaResponse", historiaResponse);
        model.addAttribute("historiaId", id);
        return "EditarBlog";
    }

    @PostMapping("/update-blog/{id}")
    public String updateBlog(@PathVariable Long id, @ModelAttribute @Valid HistoriaCreateRequest historiaRequest) {
        BaseResponse response = historiaService.updateHistoria(historiaRequest, id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/blog/?mensaje=" + mensaje + "&success=" + success;
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String disableBlog(@PathVariable Long id) {
        BaseResponse response = historiaService.changeEstadoHistoria(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/blog/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        BaseResponse response = historiaService.deleteHistoria(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/blog/?mensaje=" + mensaje + "&success=" + success;
    }
}