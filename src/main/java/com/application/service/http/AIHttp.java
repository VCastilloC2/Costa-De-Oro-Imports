package com.application.service.http;

import com.application.configuration.ia.CostaBotPromptTemplate;
import com.application.service.interfaces.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIHttp implements AIService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final CostaBotPromptTemplate template;

    @Value("${app.ai.negacion-respuesta}")
    private String mensajeNegacion;

    @Override
    public String preguntar(String mensaje, String chatId) {
        try {

            String mensajeLimpio = mensaje.strip()
                    .replaceAll("\\s+", " ");

            String prompt = template.buildPrompt(mensajeLimpio, chatId);

            String respuesta = chatClient
                    .prompt()
                    .user(prompt)
                    .advisors(
                            PromptChatMemoryAdvisor.builder(chatMemory)
                                    .conversationId(chatId)
                                    .build()
                    )
                    .call()
                    .content();

            if (respuesta == null || respuesta.isBlank()) {
                return mensajeNegacion;
            }

            return respuesta;

        } catch (Exception e) {
            return "Error al conectar con la IA. Intenta nuevamente.";
        }

    }

}