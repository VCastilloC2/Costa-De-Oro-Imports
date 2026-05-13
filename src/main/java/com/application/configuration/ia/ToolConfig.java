package com.application.configuration.ia;

import com.application.configuration.ia.tools.EmailTools;
import com.application.configuration.ia.tools.EmpresaTools;
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
    private final EmpresaTools empresaTools;
    private final EmailTools emailTools;

    @Bean
    public ToolCallbackProvider customTools() {

        return MethodToolCallbackProvider.builder()
                .toolObjects(
                        usuarioTools, // Usuario Tools
                        empresaTools, // Empresa Tools
                        emailTools    // Email Tools
                )
                .build();
    }
}