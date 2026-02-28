package com.application.presentation.controller.principal;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.empresa.Empresa;
import com.application.persistence.entity.empresa.enums.ESector;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.application.presentation.dto.empresa.request.SetEmpresaPhotoRequest;
import com.application.presentation.dto.empresa.request.UpdateEmpresaRequest;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.general.response.GeneralResponse;
import com.application.presentation.dto.usuario.request.SetUsuarioPhotoRequest;
import com.application.presentation.dto.usuario.request.UpdatePasswordRequest;
import com.application.presentation.dto.usuario.request.UpdateUsuarioRequest;
import com.application.service.implementation.empresa.EmpresaServiceImpl;
import com.application.service.implementation.usuario.UsuarioServiceImpl;
import com.application.service.interfaces.CloudinaryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioServiceImpl usuarioService;
    private final EmpresaServiceImpl empresaService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/")
    public String perfil(@AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(value = "mensaje", required = false) String mensaje,
            Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getCorreo());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        String urlImagenEmpresa = "";
        if (usuario.getEmpresa() != null) {
            urlImagenEmpresa = cloudinaryService.getImagenUrl(usuario.getEmpresa().getImagen());
        }

        model.addAttribute("usuario", usuario); // datos del usuario
        model.addAttribute("mensaje", mensaje); // mensaje de los distintos formularios
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("urlImagenEmpresa", urlImagenEmpresa);
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        model.addAttribute("sectoresEmpresa", ESector.values());
        return "Perfil";
    }

    @PostMapping("/actualizar-usuario")
    public String updateUsuario(@AuthenticationPrincipal CustomUserPrincipal principal,
            @ModelAttribute @Valid UpdateUsuarioRequest usuarioRequest) {
        GeneralResponse response = usuarioService.updateUser(principal, usuarioRequest);
        String mensaje = response.mensaje();
        return "redirect:/perfil/?mensaje=" + UriUtils.encode(mensaje, StandardCharsets.UTF_8);
    }

    @PostMapping("/actualizar-empresa")
    public String updateEmpresa(@AuthenticationPrincipal CustomUserPrincipal principal,
            @ModelAttribute @Valid UpdateEmpresaRequest empresaRequest) {
        GeneralResponse response = empresaService.updateEmpresa(principal, empresaRequest);
        String mensaje = response.mensaje();
        return "redirect:/perfil/?mensaje=" + UriUtils.encode(mensaje, StandardCharsets.UTF_8);
    }

    @PostMapping("/colocar-imagen-usuario")
    public String setUsuarioPhoto(@AuthenticationPrincipal CustomUserPrincipal principal,
            @ModelAttribute @Valid SetUsuarioPhotoRequest usuarioPhotoRequest) {
        GeneralResponse response = usuarioService.setUserPhoto(principal, usuarioPhotoRequest);
        String mensaje = response.mensaje();
        return "redirect:/perfil/?mensaje=" + UriUtils.encode(mensaje, StandardCharsets.UTF_8);
    }

    @PostMapping("/colocar-imagen-empresa")
    public String setEmpresaPhoto(@AuthenticationPrincipal CustomUserPrincipal principal,
            @ModelAttribute @Valid SetEmpresaPhotoRequest empresaPhotoRequest) {
        GeneralResponse response = empresaService.setEmpresaPhoto(principal, empresaPhotoRequest);
        String mensaje = response.mensaje();
        return "redirect:/perfil/?mensaje=" + UriUtils.encode(mensaje, StandardCharsets.UTF_8);
    }

    @GetMapping("/completar-registro")
    public String completarRegistro() {
        return "CompletarRegistro";
    }

    @GetMapping("/configuration")
    public String configuracion(@AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(value = "mensaje", required = false) String mensaje,
            @RequestParam(value = "success", required = false) Boolean success,
            Model model) {
        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getCorreo());
        model.addAttribute("usuario", usuario); // datos del usuario
        model.addAttribute("mensaje", mensaje); // mensaje de los distintos formularios
        model.addAttribute("success", success); // boolean que determina si se obtuvo un mensaje de éxito o error
        return "Configuraciones";
    }

    @PostMapping("/actualizar-contraseña")
    public String updatePassword(@AuthenticationPrincipal CustomUserPrincipal principal,
            @ModelAttribute @Valid UpdatePasswordRequest passwordRequest) {
        BaseResponse response = usuarioService.updatePassword(principal, passwordRequest);
        String mensajeEncode = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        return "redirect:/perfil/configuration?mensaje=" + mensajeEncode + "&success=" + response.success();
    }

    @GetMapping("/notificaciones")
    public String notificaciones() {
        return "Notificaciones";
    }

    @GetMapping("/pedidos")
    public String Pedidos() {
        return "Pedidos";
    }

}