package com.application.configuration.ia;

import com.application.configuration.ia.tools.UsuarioTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ToolConfig {

    private final UsuarioTools usuarioTools;

    @Bean
    public ToolCallbackProvider localTools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(usuarioTools)
                .build();
    }

}