package com.application.presentation.dto.usuario.request;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;

@JsonPropertyOrder({"username", "password"})
public record AuthLoginRequest(@NotBlank String username,
                               @NotBlank String password) {
}
