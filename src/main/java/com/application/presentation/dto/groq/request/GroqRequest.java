package com.application.presentation.dto.groq.request;

import com.application.presentation.dto.Message;

import java.util.List;

public record GroqRequest(
        String model,
        List<Message> messages,
        Double temperature
) {
}