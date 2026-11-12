package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.configuration.fastapi.FastApiAdmin;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.categoria.request.CategoriaCreateRequest;
import com.application.presentation.dto.categoria.response.CategoriaResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.categoria.CategoriaService;
import com.application.service.interfaces.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Categorías del panel admin: datos y operaciones en FastAPI ({@code /admin/categoria}). */
@Controller
@RequestMapping("/admin/categoria")
@RequiredArgsConstructor
public class CategoriaController {

    private static final String RUTA = "/admin/categoria/";

    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;
    private final FastApiAdmin fastApiAdmin;

    @GetMapping({"/", ""})
    public String DashboardCategoria(@AuthenticationPrincipal CustomUserPrincipal principal,
                                     @RequestParam(value = "mensaje", required = false) String mensaje,
                                     @RequestParam(value = "success", required = false) Boolean success,
                                     Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        List<CategoriaResponse> categoriaList = fastApiAdmin.leer("categoria.listar", "/admin/categoria",
                new ParameterizedTypeReference<List<CategoriaResponse>>() {
                }, categoriaService::getCategorias);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("categoriaList", categoriaList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);
        return "DashboardCategoria";
    }

    @GetMapping("/create-categoria")
    public String AgregarCategoria(@AuthenticationPrincipal CustomUserPrincipal principal, Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getCorreo());
        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        return "AgregarCategoria";
    }

    @PostMapping("/create-categoria")
    public String addcategoria(@ModelAttribute @Valid CategoriaCreateRequest categoriaRequest) {
        BaseResponse response = fastApiAdmin.escribir("categoria.crear", HttpMethod.POST, "/admin/categoria",
                () -> cuerpo(categoriaRequest), () -> categoriaService.createCategoria(categoriaRequest));
        return AdminVistas.redirect(RUTA, response);
    }

    @GetMapping("/update-categoria/{id}")
    public String editarCategoria(@PathVariable Long id,
                                  @AuthenticationPrincipal CustomUserPrincipal principal,
                                  Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getCorreo());
        CategoriaResponse categoria = fastApiAdmin.leer("categoria.obtener", "/admin/categoria/{id}",
                CategoriaResponse.class, () -> categoriaService.getCategoriaResponseById(id), id);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("categoria", categoria);
        model.addAttribute("categoriaId", id);
        return "EditarCategoria";
    }

    @PostMapping("update-categoria/{id}")
    public String updateCategoria(@ModelAttribute @Valid CategoriaCreateRequest categoriaRequest, @PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("categoria.actualizar", HttpMethod.PUT, "/admin/categoria/{id}",
                () -> cuerpo(categoriaRequest), () -> categoriaService.updateCategoria(categoriaRequest, id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String disableCategoria(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("categoria.estado", HttpMethod.PATCH,
                "/admin/categoria/{id}/disable", null, () -> categoriaService.changeEstadoCategoria(id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    @GetMapping("delete/{id}")
    public String deleteCategoria(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("categoria.eliminar", HttpMethod.DELETE,
                "/admin/categoria/{id}", null, () -> categoriaService.deleteCategoria(id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    private Object cuerpo(CategoriaCreateRequest r) {
        return AdminVistas.cuerpo(
                "imagen", AdminVistas.subirImagen(cloudinaryService, r.imagen(), "imagen-categoria"),
                "nombre", r.nombre(), "descripcion", r.descripcion(), "activo", r.activo(),
                "subcategorias", r.subcategorias());
    }
}
