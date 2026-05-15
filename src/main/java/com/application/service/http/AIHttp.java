package com.application.service.http;

import com.application.service.interfaces.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AIHttp implements AIService {

    private final ChatClient chatClient;

    @Override
    public String preguntar(String mensaje) {
        try {
            return chatClient
                    .prompt()
                    .user(mensaje)
                    .call()
                    .content();
        } catch (Exception e) {

            return "Error en IA.";
        }
    }

}