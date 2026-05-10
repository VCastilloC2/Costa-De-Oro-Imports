package com.application.service.http;

import com.application.persistence.entity.DocumentChunk;
import com.application.presentation.dto.rag.response.RagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RagService {

    private final RetrievalService retrievalService;
    private final GroqService groqService;

    public RagResponse ask(String question) {
        List<DocumentChunk> chunks = retrievalService.retrieve(question, 3);

        String context = buildContext(chunks);

        String answer;
        try {
            answer = groqService.generateAnswer(question, context);
        } catch (Exception e) {
            answer = "No fue posible consultar Groq. Contexto recuperado localmente:\n\n" + context;
        }

        List<String> sources = chunks.stream()
                .map(DocumentChunk::getSource)
                .distinct()
                .toList();

        List<String> retrievedChunks = chunks.stream()
                .map(chunk -> "[%s | score %.3f]\n%s".formatted(
                        chunk.getSource(),
                        chunk.getScore(),
                        chunk.getContent()
                ))
                .toList();

        return new RagResponse(answer, sources, retrievedChunks, true);
    }

    private String buildContext(List<DocumentChunk> chunks) {
        StringBuilder sb = new StringBuilder();

        for (DocumentChunk chunk : chunks) {
            sb.append("Fuente: ").append(chunk.getSource()).append("\n");
            sb.append("Fragmento: ").append(chunk.getContent()).append("\n\n");
        }

        return sb.toString();
    }
}