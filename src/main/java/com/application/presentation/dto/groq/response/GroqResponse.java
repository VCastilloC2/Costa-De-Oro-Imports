package com.application.presentation.dto.groq.response;

import com.application.presentation.dto.Choice;

import java.util.List;

public record GroqResponse (
        List<Choice> choices
) {
}