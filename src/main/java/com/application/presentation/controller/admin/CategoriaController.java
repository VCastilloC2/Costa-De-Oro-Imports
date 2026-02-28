package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.categoria.request.CategoriaCreateRequest;
import com.application.presentation.dto.categoria.response.CategoriaResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.categoria.CategoriaService;
import com.application.service.interfaces.usuario.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;

    @GetMapping({"/", ""})
    public String DashboardCategoria(@AuthenticationPrincipal CustomUserPrincipal principal,
                                     @RequestParam(value = "mensaje", required = false) String mensaje,
                                     @RequestParam(value = "success", required = false) Boolean success,
                                     Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());
        List<CategoriaResponse> categoriaList = categoriaService.getCategorias();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("categoriaList", categoriaList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);

        return "DashboardCategoria";
    }

    @GetMapping("/create-categoria")
    public String AgregarCategoria(@AuthenticationPrincipal CustomUserPrincipal principal, Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getCorreo());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        return "AgregarCategoria";
    }

    @PostMapping("/create-categoria")
    public String addcategoria(@ModelAttribute @Valid CategoriaCreateRequest categoriaRequest) {
        BaseResponse response = categoriaService.createCategoria(categoriaRequest);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/categoria/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("/update-categoria/{id}")
    public String editarCategoria(@PathVariable Long id,
                                  @AuthenticationPrincipal CustomUserPrincipal principal,
                                  Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getCorreo());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());
        CategoriaResponse categoria = categoriaService.getCategoriaResponseById(id);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("categoria", categoria);
        model.addAttribute("categoriaId", id);
        return "EditarCategoria";
    }

    @PostMapping("update-categoria/{id}")
    public String updateCategoria(@ModelAttribute @Valid CategoriaCreateRequest categoriaRequest, @PathVariable Long id) {
        BaseResponse response = categoriaService.updateCategoria(categoriaRequest, id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/categoria/?mensaje=" + mensaje + "&success=" + success;
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String disableCategoria(@PathVariable Long id) {
        BaseResponse response = categoriaService.changeEstadoCategoria(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/categoria/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("delete/{id}")
    public String deleteCategoria(@PathVariable Long id) {
        BaseResponse response = categoriaService.deleteCategoria(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/categoria/?mensaje=" + mensaje + "&success=" + success;
    }

}