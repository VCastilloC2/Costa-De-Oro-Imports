package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.application.presentation.dto.factura.request.FacturaProveedorRequest;
import com.application.presentation.dto.factura.response.FacturaProductoProveedorResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.usuario.request.CreateClienteRequest;
import com.application.presentation.dto.usuario.request.CreateProveedorRequest;
import com.application.presentation.dto.usuario.response.ProveedorEstadisticasResponse;
import com.application.presentation.dto.usuario.response.ProveedorResponse;
import com.application.service.implementation.usuario.UsuarioServiceImpl;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.FacturaProveedorService;
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
@RequestMapping("/admin/provedores")
@RequiredArgsConstructor
public class ProvedoresController {

    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;
    private final FacturaProveedorService facturaProveedorService;

    @GetMapping({"", "/"})
    public String DashboardProveedores(@AuthenticationPrincipal CustomUserPrincipal principal,
                                       @RequestParam(value = "mensaje", required = false) String mensaje,
                                       @RequestParam(value = "success", required = false) Boolean success,
                                       Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        List<ProveedorEstadisticasResponse> proveedorList = usuarioService.getProveedorConEstadisticas();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
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
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        // Obtener productos del proveedor para el select
        List<FacturaProductoProveedorResponse> productos = facturaProveedorService.getProductosByProveedorId(id);

        // Obtener información del proveedor
        ProveedorResponse proveedor = usuarioService.getProveedorById(id);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("productos", productos);
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("proveedorId", id);

        return "NuevaCompra";
    }

    @GetMapping("/productos/{proveedorId}")
    @ResponseBody
    public List<FacturaProductoProveedorResponse> getProductosByProveedor(@PathVariable Long proveedorId) {
        return facturaProveedorService.getProductosByProveedorId(proveedorId);
    }

    @PostMapping("/create-compra")
    public String crearCompra(@ModelAttribute @Valid FacturaProveedorRequest facturaRequest,
                              Model model) {
        BaseResponse response = facturaProveedorService.crearFacturaCompra(facturaRequest);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/provedores/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("/create-proveedor")
    public String agregarProveedores(@AuthenticationPrincipal CustomUserPrincipal principal,
                                     Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        return "AgregarProveedores";
    }

    @PostMapping("/create-proveedor")
    public String addProveedor(@ModelAttribute @Valid CreateProveedorRequest proveedorRequest) {
        BaseResponse response = usuarioService.createProveedor(proveedorRequest);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/provedores/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("/update-proveedor/{id}")
    public String updateProveedores(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserPrincipal principal,
                                    Model model) {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());
        ProveedorResponse proveedorResponse = usuarioService.getProveedorById(id);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        model.addAttribute("proveedorResponse", proveedorResponse);
        model.addAttribute("proveedorId", id);
        return "EditarProveedores";
    }

    @PostMapping("/update-proveedor/{id}")
    public String updateProveedor(@PathVariable Long id, @ModelAttribute @Valid CreateProveedorRequest proveedorRequest) {
        BaseResponse response = usuarioService.updateProveedor(id, proveedorRequest);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/provedores/?mensaje=" + mensaje + "&success=" + success;
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String disableProveedor(@PathVariable Long id) {
        BaseResponse response = usuarioService.changeEstadoUsuario(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/provedores/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("delete/{id}")
    public String deleteProveedor(@PathVariable Long id) {
        BaseResponse response = usuarioService.deleteUsuario(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/provedores/?mensaje=" + mensaje + "&success=" + success;
    }

}