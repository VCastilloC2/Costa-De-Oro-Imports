package com.application.service.http;

import com.application.configuration.ia.GroqClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroqService {

    private final GroqClient groqClient;

    public String generateAnswer(String question, String context) {
        String systemPrompt = """
                Eres un asistente académico basado en RAG.
                Responde en español.
                Usa exclusivamente el contexto proporcionado.
                Si la respuesta no está en el contexto, indica que no hay información suficiente.
                No inventes datos.
                Sé claro, breve y preciso.
                """;

        String userPrompt = """
                Contexto recuperado:
                %s

                Pregunta del usuario:
                %s

                Respuesta:
                """.formatted(context, question);

        return groqClient.chat(systemPrompt, userPrompt);
    }

}