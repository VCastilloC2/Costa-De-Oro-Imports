package com.application.presentation.dto.chat.response;

public record ChatResponse(
        String answer,
        String provider,
        String model,
        long responseTimeMs
) {
}