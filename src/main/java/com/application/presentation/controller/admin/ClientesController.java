package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/clientes")
@RequiredArgsConstructor
public class ClientesController {

    private final UsuarioService usuarioService;
    private final CloudinaryService cloudinaryService;

    @GetMapping({"/", ""})
    public String DashboardClientes(@AuthenticationPrincipal CustomUserPrincipal principal,
                                    @RequestParam(value = "mensaje", required = false) String mensaje,
                                    @RequestParam(value = "success", required = false) Boolean success,
                                    Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        List<UsuarioGastoResponse> usuarioList = usuarioService.getUsuarioGastoUltimoAnio();

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("usuarioList", usuarioList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);
        return "DashboardClientes";
    }

    @GetMapping("/create-cliente")
    public String agregarClientes(@AuthenticationPrincipal CustomUserPrincipal principal,
                                  Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        return "AgregarClientes";
    }

    @PostMapping("/create-cliente")
    public String addClientes(@ModelAttribute @Valid CreateClienteRequest clienteRequest) {
        BaseResponse response = usuarioService.createCliente(clienteRequest);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/clientes/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("/update-cliente/{id}")
    public String updateClientes(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserPrincipal principal,
                                 Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());
        ClienteResponse clienteResponse = usuarioService.getClienteById(id);

        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        model.addAttribute("clienteResponse", clienteResponse);
        model.addAttribute("clienteId", id);
        return "EditarClientes";
    }

    @PostMapping("/update-cliente/{id}")
    public String updateCliente(@PathVariable Long id, @ModelAttribute @Valid CreateClienteRequest clienteRequest) {
        BaseResponse response = usuarioService.updateCliente(id, clienteRequest);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/clientes/?mensaje=" + mensaje + "&success=" + success;
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("disable/{id}")
    public String disableCliente(@PathVariable Long id) {
        BaseResponse response = usuarioService.changeEstadoUsuario(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/clientes/?mensaje=" + mensaje + "&success=" + success;
    }

    @GetMapping("delete/{id}")
    public String deleteCliente(@PathVariable Long id) {
        BaseResponse response = usuarioService.deleteUsuario(id);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        boolean success = response.success();
        return "redirect:/admin/clientes/?mensaje=" + mensaje + "&success=" + success;
    }

}