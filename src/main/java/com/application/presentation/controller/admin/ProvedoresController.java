package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.configuration.fastapi.FastApiAdmin;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.application.presentation.dto.factura.request.FacturaProveedorRequest;
import com.application.presentation.dto.factura.response.FacturaProductoProveedorResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.usuario.request.CreateProveedorRequest;
import com.application.presentation.dto.usuario.response.ProveedorEstadisticasResponse;
import com.application.presentation.dto.usuario.response.ProveedorResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.FacturaProveedorService;
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
 * Proveedores del panel admin y sus facturas de compra: datos y operaciones en FastAPI
 * ({@code /admin/provedores}). Registrar una factura suma el stock de cada producto en FastAPI.
 */
@Controller
@RequestMapping("/admin/provedores")
@RequiredArgsConstructor
public class ProvedoresController {

    private static final String RUTA = "/admin/provedores/";

    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;
    private final FacturaProveedorService facturaProveedorService;
    private final FastApiAdmin fastApiAdmin;

    @GetMapping({"", "/"})
    public String DashboardProveedores(@AuthenticationPrincipal CustomUserPrincipal principal,
                                       @RequestParam(value = "mensaje", required = false) String mensaje,
                                       @RequestParam(value = "success", required = false) Boolean success,
                                       Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        List<ProveedorEstadisticasResponse> proveedorList = fastApiAdmin.leer("provedores.listar",
                "/admin/provedores", new ParameterizedTypeReference<List<ProveedorEstadisticasResponse>>() {
                }, usuarioService::getProveedorConEstadisticas);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("proveedorList", proveedorList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);
        return "DashboardProvedores";
    }

    @GetMapping("/create-compra/{id}")
    public String nuevaCompraProveedor(@PathVariable Long id,
                                       @AuthenticationPrincipal CustomUserPrincipal principal,
                                       Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("productos", productos(id));
        model.addAttribute("proveedor", proveedor(id));
        model.addAttribute("proveedorId", id);
        return "NuevaCompra";
    }

    @GetMapping("/productos/{proveedorId}")
    @ResponseBody
    public List<FacturaProductoProveedorResponse> getProductosByProveedor(@PathVariable Long proveedorId) {
        return productos(proveedorId);
    }

    @PostMapping("/create-compra")
    public String crearCompra(@ModelAttribute @Valid FacturaProveedorRequest facturaRequest, Model model) {
        BaseResponse response = fastApiAdmin.escribir("provedores.factura", HttpMethod.POST,
                "/admin/provedores/compras", () -> cuerpoFactura(facturaRequest),
                () -> facturaProveedorService.crearFacturaCompra(facturaRequest));
        return AdminVistas.redirect(RUTA, response);
    }

    @GetMapping("/create-proveedor")
    public String agregarProveedores(@AuthenticationPrincipal CustomUserPrincipal principal, Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        return "AgregarProveedores";
    }

    @PostMapping("/create-proveedor")
    public String addProveedor(@ModelAttribute @Valid CreateProveedorRequest proveedorRequest) {
        BaseResponse response = fastApiAdmin.escribir("provedores.crear", HttpMethod.POST, "/admin/provedores",
                () -> cuerpoProveedor(proveedorRequest), () -> usuarioService.createProveedor(proveedorRequest));
        return AdminVistas.redirect(RUTA, response);
    }

    @GetMapping("/update-proveedor/{id}")
    public String updateProveedores(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserPrincipal principal,
                                    Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        model.addAttribute("proveedorResponse", proveedor(id));
        model.addAttribute("proveedorId", id);
        return "EditarProveedores";
    }

    @PostMapping("/update-proveedor/{id}")
    public String updateProveedor(@PathVariable Long id, @ModelAttribute @Valid CreateProveedorRequest proveedorRequest) {
        BaseResponse response = fastApiAdmin.escribir("provedores.actualizar", HttpMethod.PUT,
                "/admin/provedores/{id}", () -> cuerpoProveedor(proveedorRequest),
                () -> usuarioService.updateProveedor(id, proveedorRequest), id);
        return AdminVistas.redirect(RUTA, response);
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String disableProveedor(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("provedores.estado", HttpMethod.PATCH,
                "/admin/provedores/{id}/disable", null, () -> usuarioService.changeEstadoUsuario(id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    @GetMapping("delete/{id}")
    public String deleteProveedor(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("provedores.eliminar", HttpMethod.DELETE,
                "/admin/provedores/{id}", null, () -> usuarioService.deleteUsuario(id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    private List<FacturaProductoProveedorResponse> productos(Long proveedorId) {
        return fastApiAdmin.leer("provedores.productos", "/admin/provedores/productos/{id}",
                new ParameterizedTypeReference<List<FacturaProductoProveedorResponse>>() {
                }, () -> facturaProveedorService.getProductosByProveedorId(proveedorId), proveedorId);
    }

    private ProveedorResponse proveedor(Long id) {
        return fastApiAdmin.leer("provedores.obtener", "/admin/provedores/{id}", ProveedorResponse.class,
                () -> usuarioService.getProveedorById(id), id);
    }

    /** Contraseña vacía en edición = conservar la actual. */
    private Object cuerpoProveedor(CreateProveedorRequest r) {
        return AdminVistas.cuerpo(
                "imagen", AdminVistas.subirImagen(cloudinaryService, r.imagen(), "perfil-usuario"),
                "tipoIdentificacion", r.tipoIdentificacion(),
                "numeroIdentificacion", r.numeroIdentificacion(),
                "nombres", r.nombres(), "apellidos", r.apellidos(), "telefono", r.telefono(),
                "correo", r.correo(), "direccion", r.direccion(),
                "password", r.password() == null || r.password().isBlank() ? null : r.password(),
                "imagenEmpresa", AdminVistas.subirImagen(cloudinaryService, r.imagenEmpresa(), "perfil-empresa"),
                "nit", r.nit(), "razonSocial", r.razonSocial(), "ciudad", r.ciudad(),
                "direccionEmpresa", r.direccionEmpresa(), "telefonoEmpresa", r.telefonoEmpresa(),
                "correoEmpresa", r.correoEmpresa());
    }

    private Object cuerpoFactura(FacturaProveedorRequest r) {
        List<Object> detalles = r.detalles() == null ? List.of() : r.detalles().stream()
                .map(d -> (Object) AdminVistas.cuerpo("productoId", d.productoId(), "cantidad", d.cantidad(),
                        "precioCompra", d.precioCompra(), "subtotal", d.subtotal()))
                .toList();
        return AdminVistas.cuerpo(
                "numeroFactura", r.numeroFactura(), "fechaEmision", r.fechaEmision(),
                "detalles", detalles, "totalCompra", r.totalCompra(), "proveedorId", r.proveedorId());
    }
}
