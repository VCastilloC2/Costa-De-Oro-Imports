package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.configuration.fastapi.FastApiAdmin;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.peticion.response.PeticionResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.PeticionService;
import com.application.service.interfaces.usuario.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequestMapping("/admin/pqrs")
@RequiredArgsConstructor
public class PQRS_Controller {

    private final UsuarioService usuarioService;
    private final PeticionService peticionService;
    private final CloudinaryService cloudinaryService;
    private final FastApiAdmin fastApiAdmin;

    @GetMapping({"", "/"})
    public String DashboardPQRS(@AuthenticationPrincipal CustomUserPrincipal principal,
                                @RequestParam(value = "mensaje", required = false) String mensaje,
                                @RequestParam(value = "success", required = false) Boolean success,
                                Model model) throws JsonProcessingException {

        Usuario usuario = usuarioService.getUsuarioByCorreo(principal.getUsername());
        String urlImagenUsuario = cloudinaryService.getImagenUrl(usuario.getImagen());

        List<PeticionResponse> peticionList = fastApiAdmin.leer("pqrs.listar", "/admin/pqrs",
                new ParameterizedTypeReference<List<PeticionResponse>>() {
                }, peticionService::getPeticionesActivas);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String peticionesJson = mapper.writeValueAsString(peticionList);

        model.addAttribute("peticionesJson", peticionesJson);
        model.addAttribute("usuario", usuario);
        model.addAttribute("urlImagenUsuario", urlImagenUsuario);
        model.addAttribute("peticionList", peticionList);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("success", success);

        return "DashboardPQRS";
    }

    // Para estos métodos se usa GetMapping porque la petición se hace por JS y estamos en un @Controller
    @GetMapping("change-estado/{id}")
    public String changeEstadoPeticion(@PathVariable Long id, @RequestParam String estadoPeticion) {
        BaseResponse response = fastApiAdmin.escribir("pqrs.estado", HttpMethod.PATCH, "/admin/pqrs/{id}/estado",
                () -> AdminVistas.cuerpo("estadoPeticion", estadoPeticion),
                () -> peticionService.changeEstadoPeticion(id, estadoPeticion), id);
        return AdminVistas.redirect("/admin/pqrs/", response);
    }

    @GetMapping("delete/{id}")
    public String deletePeticion(@PathVariable Long id) {
        BaseResponse response = fastApiAdmin.escribir("pqrs.eliminar", HttpMethod.DELETE, "/admin/pqrs/{id}",
                null, () -> peticionService.deletePeticion(id), id);
        return AdminVistas.redirect("/admin/pqrs/", response);
    }
}