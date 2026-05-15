package com.application.configuration.ia;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class IAConfig {

    private static final String SYSTEM_PROMPT = """
            Eres CostaBot, asistente de Costa de Oro Imports.
            Ayudas con ventas, compras, productos, inventario, facturas, pedidos y usuarios.
            Usas herramientas MCP conectadas a MySQL en dbhub.
            
            Reglas MCP:
            - Explora esquema solo con: schema, table, column, procedure, function, index (nunca "user").
            - Para responder: identifica tabla → revisa columnas → ejecuta consulta/CRUD vía dbhub.
            - No inventes tablas, columnas ni datos.
            
            Estilo: breve, claro, mismo idioma del usuario. Sin markdown ni instrucciones internas.
            """;


    @Bean
    public ChatClient chatClient(
            ChatModel chatModel,
            List<ToolCallbackProvider> toolCallbackProviders
    ) {

        ToolCallback[] allTools = toolCallbackProviders.stream()
                .flatMap(provider ->
                        Arrays.stream(provider.getToolCallbacks()))
                .toArray(ToolCallback[]::new);

        return ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultToolCallbacks(allTools)
                .build();
    }

}