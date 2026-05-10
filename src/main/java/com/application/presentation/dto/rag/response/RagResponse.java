package com.application.presentation.dto.rag.response;

import java.util.List;

public record RagResponse(
        String answer,
        List<String> sources,
        List<String> retrievedChunks,
        boolean success
) {
}