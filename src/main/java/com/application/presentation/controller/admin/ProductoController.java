package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.configuration.fastapi.FastApiAdmin;
import com.application.persistence.entity.producto.enums.ETipo;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.categoria.response.CategoriaProductoResponse;
import com.application.presentation.dto.categoria.response.SubCategoriaProductoResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.producto.request.ProductoCreateRequest;
import com.application.presentation.dto.producto.response.ProductoResponse;
import com.application.presentation.dto.usuario.response.ProveedorProductoResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.categoria.CategoriaService;
import com.application.service.interfaces.producto.ProductoService;
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

/**
 * Productos del panel admin. Los datos y las operaciones van a FastAPI ({@code /admin/producto});
 * la imagen se sigue subiendo a Cloudinary desde aquí y se envía su public_id.
 * Si FastAPI no responde, los listados/formularios usan los servicios locales.
 */
@Controller
@RequestMapping("/admin/producto")
@RequiredArgsConstructor
public class ProductoController {

    private static final String RUTA = "/admin/producto/";

    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final CloudinaryService cloudinaryService;
    private final FastApiAdmin fastApiAdmin;

    /** Datos de los formularios de creación/edición ({@code GET /admin/producto/opciones}). */
    record OpcionesProducto(List<CategoriaProductoResponse> categoriaList,
                            List<ProveedorProductoResponse> proveedorList) {
    }

    @GetMapping({"/", ""})
    public String DashboardProducto(@AuthenticationPrincipal CustomUserPrincipal principal,
                                    @RequestParam(value = "mensaje", required = false) String mensaje,
                                    @RequestParam(value = "success", required = false) Boolean success,
                                    Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getCorreo());
        List<ProductoResponse> productoList = fastApiAdmin.leer("producto.listar", "/admin/producto",
                new ParameterizedTypeReference<List<ProductoResponse>>() {
                }, productoService::getProductos);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("productoList", productoList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);
        return "DashboardProducto";
    }

    @GetMapping("/create-producto")
    public String addProducto(@AuthenticationPrincipal CustomUserPrincipal principal, Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        OpcionesProducto opciones = opciones();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("tiposProducto", ETipo.values());
        model.addAttribute("categoriaList", opciones.categoriaList());
        model.addAttribute("proveedorList", opciones.proveedorList());
        return "AgregarProducto";
    }

    // Endpoint para obtener subcategorías por categoría (lo usa el JS del formulario)
    @GetMapping("/subcategorias/{categoriaId}")
    @ResponseBody
    public List<SubCategoriaProductoResponse> getSubCategoriasByCategoria(@PathVariable Long categoriaId) {
        return fastApiAdmin.leer("producto.subcategorias", "/admin/producto/subcategorias/{id}",
                new ParameterizedTypeReference<List<SubCategoriaProductoResponse>>() {
                }, () -> categoriaService.getSubCategoriasActivasByCategoriaId(categoriaId), categoriaId);
    }

    @PostMapping("/create-producto")
    public String addProducto(@ModelAttribute @Valid ProductoCreateRequest productoRequest) {
        BaseResponse response = fastApiAdmin.escribir("producto.crear", HttpMethod.POST, "/admin/producto",
                () -> cuerpo(productoRequest), () -> productoService.createProducto(productoRequest));
        return AdminVistas.redirect(RUTA, response);
    }

    @GetMapping("/update-producto/{id}")
    public String editarProducto(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserPrincipal principal,
                                 Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        ProductoResponse productoResponse = fastApiAdmin.leer("producto.obtener", "/admin/producto/{id}",
                ProductoResponse.class, () -> productoService.getProductoResponseById(id), id);
        OpcionesProducto opciones = opciones();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("productoResponse", productoResponse);
        model.addAttribute("tiposProducto", ETipo.values());
        model.addAttribute("categoriaList", opciones.categoriaList());
        model.addAttribute("proveedorList", opciones.proveedorList());
        return "EditarProducto";
    }

    @PostMapping("/update-producto/{id}")
    public String updateProducto(@ModelAttribute @Valid ProductoCreateRequest productoRequest, @PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("producto.actualizar", HttpMethod.PUT, "/admin/producto/{id}",
                () -> cuerpo(productoRequest), () -> productoService.updateProducto(productoRequest, id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String changeEstadoProducto(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("producto.estado", HttpMethod.PATCH,
                "/admin/producto/{id}/disable", null, () -> productoService.changeEstadoProducto(id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    @GetMapping("delete/{id}")
    public String deleteProducto(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("producto.eliminar", HttpMethod.DELETE, "/admin/producto/{id}",
                null, () -> productoService.deleteProducto(id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    private OpcionesProducto opciones() {
        return fastApiAdmin.leer("producto.opciones", "/admin/producto/opciones", OpcionesProducto.class,
                () -> new OpcionesProducto(categoriaService.getCategoriasProducto(),
                        usuarioService.getProveedoresActivos()));
    }

    private Object cuerpo(ProductoCreateRequest r) {
        return AdminVistas.cuerpo(
                "codigoProducto", r.codigoProducto(),
                "imagen", AdminVistas.subirImagen(cloudinaryService, r.imagen(), "imagen-producto"),
                "nombre", r.nombre(), "marca", r.marca(), "pais", r.pais(), "tipo", r.tipo(),
                "precioRegular", r.precioRegular(), "precio", r.precio(), "stock", r.stock(),
                "descripcion", r.descripcion(), "activo", r.activo(),
                "categoriaId", r.categoriaId(), "subCategoriaId", r.subCategoriaId(), "proveedorId", r.proveedorId());
    }
}
