package com.application.presentation.controller.principal;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.application.presentation.dto.empresa.request.CreateEmpresaRequest;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.persistence.entity.empresa.enums.ESector;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.general.response.GeneralResponse;
import com.application.presentation.dto.usuario.request.CompleteUsuarioProfileRequest;
import com.application.presentation.dto.usuario.request.CreateUsuarioRequest;
import com.application.service.interfaces.empresa.EmpresaService;
import com.application.service.interfaces.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Controller for Authentication")
public class AuthenticationController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpresaService empresaService;

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "success", required = false) String success,
                        @RequestParam(value = "rol", required = false) String rol,
                        @RequestParam(value = "next", required = false) String next) {
        if (error != null) {
            model.addAttribute("mensajeError", error);
        }
        if (logout != null) {
            model.addAttribute("mensajeExitoso", "Sesión cerrada correctamente");
        }
        if (success != null) {

            String rolDecoded = null;
            String nextDecoded = null;

            if (rol != null) {
                rolDecoded = new String(Base64.getUrlDecoder().decode(rol), StandardCharsets.UTF_8);
            }

            if (next != null) {
                nextDecoded = new String(Base64.getUrlDecoder().decode(next), StandardCharsets.UTF_8);
            }

            model.addAttribute("loginSuccess", true);
            model.addAttribute("rol", rolDecoded);
            model.addAttribute("next", nextDecoded);
        }
        return "Login";
    }

    @GetMapping("/completar-registro")
    public String CompletarRegistro(@AuthenticationPrincipal CustomUserPrincipal principal,
                                    @RequestParam(value = "mensaje", required = false) String mensaje,
                                    @RequestParam(value = "redirectTo", required = false) String redirectTo,
                                    Model model) {
        Usuario usuario = this.usuarioService.getUsuarioByCorreo(principal.getCorreo());
        model.addAttribute("usuario", usuario);
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        model.addAttribute("mensaje", mensaje); // mensaje para el alert
        model.addAttribute("redirectTo", redirectTo); // redireccionados desde el JS mediante Swal
        return "CompletarRegistro";
    }

    @PostMapping("/completar-registro")
    public String postCompletarPerfil(@AuthenticationPrincipal CustomUserPrincipal principal,
                                      @ModelAttribute @Valid CompleteUsuarioProfileRequest completeProfileRequest,
                                      @RequestParam(name = "registrarEmpresa", defaultValue = "false") boolean registrarEmpresa) {
        GeneralResponse response = usuarioService.completeUserProfile(principal, completeProfileRequest);

        String destino = registrarEmpresa ? "registrarEmpresa" : "index";
        return "redirect:/auth/completar-registro?mensaje="
                + UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8)
                + "&redirectTo=" + destino;
    }

    @GetMapping("/registrar-empresa")
    public String RegistrarEmpresa(@RequestParam(value = "mensaje", required = false) String mensaje,
                                   Model model) {
        model.addAttribute("sectores", ESector.values());
        model.addAttribute("mensaje", mensaje); // mensaje para el alert
        return "RegistrarEmpresa";
    }

    @PostMapping("/registrar-empresa")
    public String postCreateEmpresa(@AuthenticationPrincipal CustomUserPrincipal principal,
                                    @ModelAttribute @Valid CreateEmpresaRequest createEmpresaRequest) {
        GeneralResponse response = empresaService.createEmpresa(principal, createEmpresaRequest);
        return "redirect:/auth/registrar-empresa?mensaje="
                + UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
    }

    @GetMapping("/registro")
    public String Registro(@RequestParam(value = "mensaje", required = false) String mensaje,
                           @RequestParam(value = "success", required = false) Boolean success,
                           Model model) {
        model.addAttribute("tiposIdentificacion", EIdentificacion.values());
        model.addAttribute("mensaje", mensaje); // mensaje a mostrar
        model.addAttribute("success", success); // boolean que determina si se obtuvo un mensaje de éxito o error
        return "Registro";
    }

    @PostMapping("/registro")
    public String postRegistro(@ModelAttribute @Valid CreateUsuarioRequest usuarioRequest, BindingResult result) {

        if (result.hasErrors()) {
            String mensaje = UriUtils.encode("error en los datos del formulario", StandardCharsets.UTF_8);
            return "redirect:/auth/registro?mensaje=" + mensaje + "&success=false";
        }
        BaseResponse response = usuarioService.createUser(usuarioRequest);
        String mensaje = UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8);
        return "redirect:/auth/registro?mensaje=" + mensaje + "&success=" + response.success();
    }

    @GetMapping("/recordar-contrasenna")
    public String recordarContrasenna() {
        return "RecordarContrasena";
    }

}