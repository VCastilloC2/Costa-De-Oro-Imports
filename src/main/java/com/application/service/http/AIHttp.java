package com.application.service.http;

import com.application.service.interfaces.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
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
            log.error("❌ Error al comunicarse con la IA", e);
            throw e;
        }
    }
}