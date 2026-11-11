package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.configuration.fastapi.FastApiAdmin;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.usuario.request.CreateClienteRequest;
import com.application.presentation.dto.usuario.response.ClienteResponse;
import com.application.presentation.dto.usuario.response.UsuarioGastoResponse;
import com.application.service.interfaces.CloudinaryService;
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

/** Clientes del panel admin: datos y operaciones en FastAPI ({@code /admin/clientes}). */
@Controller
@RequestMapping("/admin/clientes")
@RequiredArgsConstructor
public class ClientesController {

    private static final String RUTA = "/admin/clientes/";

    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;
    private final FastApiAdmin fastApiAdmin;

    @GetMapping({"/", ""})
    public String DashboardClientes(@AuthenticationPrincipal CustomUserPrincipal principal,
                                    @RequestParam(value = "mensaje", required = false) String mensaje,
                                    @RequestParam(value = "success", required = false) Boolean success,
                                    Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        List<UsuarioGastoResponse> usuarioList = fastApiAdmin.leer("clientes.listar", "/admin/clientes",
                new ParameterizedTypeReference<List<UsuarioGastoResponse>>() {
                }, usuarioService::getUsuarioGastoUltimoAnio);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("usuarioList", usuarioList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);
        return "DashboardClientes";
    }

    @GetMapping("/create-cliente")
    public String agregarClientes(@AuthenticationPrincipal CustomUserPrincipal principal, Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        return "AgregarClientes";
    }

    @PostMapping("/create-cliente")
    public String addClientes(@ModelAttribute @Valid CreateClienteRequest clienteRequest) {
        BaseResponse response = fastApiAdmin.escribir("clientes.crear", HttpMethod.POST, "/admin/clientes",
                () -> cuerpo(clienteRequest), () -> usuarioService.createCliente(clienteRequest));
        return AdminVistas.redirect(RUTA, response);
    }

    @GetMapping("/update-cliente/{id}")
    public String updateClientes(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserPrincipal principal,
                                 Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        ClienteResponse clienteResponse = fastApiAdmin.leer("clientes.obtener", "/admin/clientes/{id}",
                ClienteResponse.class, () -> usuarioService.getClienteById(id), id);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", cloudinaryService.getImagenUrl(usuario.getImagen()));
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        model.addAttribute("clienteResponse", clienteResponse);
        model.addAttribute("clienteId", id);
        return "EditarClientes";
    }

    @PostMapping("/update-cliente/{id}")
    public String updateCliente(@PathVariable Long id, @ModelAttribute @Valid CreateClienteRequest clienteRequest) {
        BaseResponse response = fastApiAdmin.escribir("clientes.actualizar", HttpMethod.PUT, "/admin/clientes/{id}",
                () -> cuerpo(clienteRequest), () -> usuarioService.updateCliente(id, clienteRequest), id);
        return AdminVistas.redirect(RUTA, response);
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String disableCliente(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("clientes.estado", HttpMethod.PATCH,
                "/admin/clientes/{id}/disable", null, () -> usuarioService.changeEstadoUsuario(id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    @GetMapping("delete/{id}")
    public String deleteCliente(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("clientes.eliminar", HttpMethod.DELETE,
                "/admin/clientes/{id}", null, () -> usuarioService.deleteUsuario(id), id);
        return AdminVistas.redirect(RUTA, response);
    }

    /** Cuerpo para FastAPI; contraseña vacía en edición = conservar la actual. */
    private Object cuerpo(CreateClienteRequest r) {
        return AdminVistas.cuerpo(
                "imagen", AdminVistas.subirImagen(cloudinaryService, r.imagen(), "perfil-usuario"),
                "tipoIdentificacion", r.tipoIdentificacion(),
                "numeroIdentificacion", r.numeroIdentificacion(),
                "nombres", r.nombres(), "apellidos", r.apellidos(), "telefono", r.telefono(),
                "correo", r.correo(), "direccion", r.direccion(),
                "password", r.password() == null || r.password().isBlank() ? null : r.password());
    }
}
