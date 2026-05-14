package com.application.presentation.dto.usuario.request;

import com.application.persistence.entity.usuario.enums.EIdentificacion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateClienteRequest(
        MultipartFile imagen,
        EIdentificacion tipoIdentificacion,
        @NotBlank String numeroIdentificacion,
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank String telefono,
        @Email String correo,
        @NotBlank String direccion,
        @NotBlank String password
) {
}