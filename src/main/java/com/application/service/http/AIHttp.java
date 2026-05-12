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

                // 🔥 FILTRAR instrucciones y markdown
                .filter(token -> {
                    // Eliminar tokens que contienen instrucciones
                    if (token == null || token.trim().isEmpty()) return false;

                    String lowerToken = token.toLowerCase();

                    // Patrones de instrucciones a filtrar
                    return !lowerToken.contains("instrucción") &&
                            !lowerToken.contains("instruction") &&
                            !lowerToken.contains("restriccion") &&
                            !lowerToken.contains("restriction") &&
                            !lowerToken.contains("## instrucción") &&
                            !lowerToken.contains("## instruction") &&
                            !lowerToken.contains("**instrucción") &&
                            !lowerToken.contains("markdown") &&
                            !lowerToken.contains("formato") &&
                            !token.startsWith("##") &&
                            !token.startsWith("#") &&
                            !token.startsWith("**Instrucción") &&
                            !token.matches(".*\\d+\\..*"); // Elimina "1.", "2.", etc.
                })

                // Buffer para unir tokens correctamente
                .bufferUntil(token ->
                        token.endsWith(" ")
                                || token.endsWith("\n")
                                || token.matches(".*[.!?]+$")
                )

                .map(tokens -> {

                    String resultado =
                            String.join("", tokens);

                    return resultado
                            .replaceAll("\\s+", " ");
                })

                // 🔥 FIN DEL STREAM
                .concatWith(Flux.just("[DONE]"))

                .switchIfEmpty(
                        Flux.just(mensajeNegacion)
                )

                .onErrorResume(e ->
                        Flux.just(
                                "Lo siento, estoy teniendo problemas técnicos con la IA. Por favor, intenta de nuevo..."
                        )
                );
    }
}