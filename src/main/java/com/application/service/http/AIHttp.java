package com.application.service.http;

import com.application.service.interfaces.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class AIHttp implements AIService {

    private final ChatClient chatClient;

    @Value("${app.ai.negacion-respuesta}")
    private String mensajeNegacion;

    @Override
    public Flux<String> preguntar(String mensaje) {

        return chatClient
                .prompt()
                .user(mensaje)
                .stream()
                .content()
                .switchIfEmpty(Flux.just(mensajeNegacion))
                .onErrorResume(e ->
                        Flux.just("Error al conectar con la IA."));
    }

}