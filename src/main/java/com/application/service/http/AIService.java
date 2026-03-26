package com.application.service.http;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatClient chatClient;

    @Value("${app.ai.negacion-respuesta}")
    private String mensajeNegacion;

    private static final String SYSTEM_PROMPT = """
            Eres un asistente virtual de Costa de Oro Imports, una tienda de e-commerce especializada en la venta de cervezas artesanales y comerciales.

            REGLAS ESTRICTAS:
            1. Solo debes responder preguntas relacionadas con:
               - Costa de Oro Imports y sus productos (cervezas, packs, combos)
               - Información sobre cervezas en general (tipos, estilos, maridaje)
               - Procesos de compra, envíos y pagos de la tienda
               - Horarios de atención y contacto de la empresa

            2. Si la pregunta NO está relacionada con Costa de Oro Imports o cervezas:
               - Responde educadamente que solo puedes ayudar con temas de la tienda
               - No proporciones información sobre otros temas bajo ninguna circunstancia

            3. Información de la tienda:
               - Somos una tienda online de cervezas artesanales y comerciales
               - Ofrecemos productos individuales, packs y combos
               - Aceptamos Mercado Pago como método de pago
               - Realizamos envíos a domicilio

            4. Estilo de respuestas:
               - Sé amable, profesional y conciso
               - Responde en español
               - Si no conoces la respuesta, indícalo honestamente

            Ejemplos de preguntas VÁLIDAS:
            - "¿Qué cervezas tienen disponibles?"
            - "¿Cuánto cuesta tal cerveza?"
            - "¿Hacen envíos?"
            - "¿Qué métodos de pago aceptan?"
            - "¿Qué es una cerveza IPA?"

            Ejemplos de preguntas INVÁLIDAS (rechazar):
            - "¿Cuál es la capital de Francia?"
            - "¿Cómo programar en Java?"
            - "¿Qué películas recomiendas?"
            - Cualquier tema político, religioso, deportivo o no relacionado con la tienda
            """;

    public AIService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public String preguntar(String mensaje) {
        String respuesta = chatClient
                .prompt(mensaje)
                .call()
                .content();

        if (respuesta == null || respuesta.isBlank()) {
            return mensajeNegacion;
        }

        return respuesta;
    }

}