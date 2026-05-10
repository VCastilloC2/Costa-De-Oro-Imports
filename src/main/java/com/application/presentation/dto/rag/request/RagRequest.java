package com.application.presentation.dto.rag.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RagRequest(
        @NotBlank(message = "La pregunta no puede estar vacía")
        @Size(max = 1000, message = "La pregunta no puede superar los 1000 caracteres")
        String question
) {
}