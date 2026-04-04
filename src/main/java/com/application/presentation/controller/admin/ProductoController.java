package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.producto.Producto;
import com.application.persistence.entity.producto.enums.ETipo;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.repository.CategoriaRepository;
import com.application.presentation.dto.categoria.response.CategoriaProductoResponse;
import com.application.presentation.dto.categoria.response.SubCategoriaProductoResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.general.response.GeneralResponse;
import com.application.presentation.dto.producto.request.ProductoCreateRequest;
import com.application.presentation.dto.producto.response.ProductoResponse;
import com.application.presentation.dto.usuario.response.ProveedorProductoResponse;
import com.application.service.implementation.producto.ProductoServiceImpl;
import com.application.service.implementation.usuario.UsuarioServiceImpl;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.categoria.CategoriaService;
import com.application.service.interfaces.producto.ProductoService;
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
@RequestMapping("/admin/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final CloudinaryService cloudinaryService;

    @GetMapping({"/", ""})
    public String DashboardProducto(@AuthenticationPrincipal CustomUserPrincipal principal,
                                    @RequestParam(value = "mensaje", required = false) String mensaje,
                                    @RequestParam(value = "success", required = false) Boolean success,
                                    Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getCorreo());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());
        List<ProductoResponse> productoList = productoService.getProductos();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("productoList", productoList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);

        return "DashboardProducto";
    }

    @GetMapping("/create-producto")
    public String addProducto(@AuthenticationPrincipal CustomUserPrincipal principal,
                              Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        // Obtener categorías y proveedores activos
        List<CategoriaProductoResponse> categoriaList = categoriaService.getCategoriasProducto();
        List<ProveedorProductoResponse> proveedorList = usuarioService.getProveedoresActivos();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("tiposProducto", ETipo.values());
        model.addAttribute("categoriaList", categoriaList);
        model.addAttribute("proveedorList", proveedorList);

        return "AgregarProducto";
    }

    // Endpoint para obtener subcategorías por categoría
    @GetMapping("/subcategorias/{categoriaId}")
    @ResponseBody
    public List<SubCategoriaProductoResponse> getSubCategoriasByCategoria(@PathVariable Long categoriaId) {
        return categoriaService.getSubCategoriasActivasByCategoriaId(categoriaId);
    }

    @PostMapping("/create-producto")
    public String addProducto(@ModelAttribute @Valid ProductoCreateRequest productoRequest) {
        BaseResponse response = productoService.createProducto(productoRequest);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/producto/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("/update-producto/{id}")
    public String editarProducto(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserPrincipal principal,
                                 Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        ProductoResponse productoResponse = productoService.getProductoResponseById(id);

        // Obtener categorías y proveedores activos
        List<CategoriaProductoResponse> categoriaList = categoriaService.getCategoriasProducto();
        List<ProveedorProductoResponse> proveedorList = usuarioService.getProveedoresActivos();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("productoResponse", productoResponse);
        model.addAttribute("tiposProducto", ETipo.values());
        model.addAttribute("categoriaList", categoriaList);
        model.addAttribute("proveedorList", proveedorList);

        return "EditarProducto";
    }

    @PostMapping("/update-producto/{id}")
    public String updateProducto(@ModelAttribute @Valid ProductoCreateRequest productoRequest, @PathVariable Long id) {
        BaseResponse response = productoService.updateProducto(productoRequest, id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/producto/?mensaje=" + mensaje + "&success=" + success;
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String changeEstadoProducto(@PathVariable Long id) {
        BaseResponse response = productoService.changeEstadoProducto(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/producto/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("delete/{id}")
    public String deleteProducto(@PathVariable Long id) {
        BaseResponse response = productoService.deleteProducto(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/producto/?mensaje=" + mensaje + "&success=" + success;
    }

}