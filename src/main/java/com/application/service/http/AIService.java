package com.application.service.http;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatClient chatClient;

    public AIService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String preguntar(String mensaje) {
        return chatClient
                .prompt(mensaje)
                .call()
                .content();
    }

}