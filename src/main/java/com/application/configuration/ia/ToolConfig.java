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
    private final CategoriaTools categoriaTools;
    private final CompraTools compraTools;
    private final ComentarioTools comentarioTools;
    private final EmpresaTools empresaTools;
    private final FacturaProveedorTools facturaProveedorTools;
    private final EmailTools emailTools;
    private final GraficaTools graficaTools;
    private final HistoriaTools historiaTools;
    private final PrediccionTools prediccionTools;

    @Bean
    public ToolCallbackProvider tools() {
        return MethodToolCallbackProvider.builder()
                .toolObjects(
                        usuarioTools,
                        categoriaTools,
                        compraTools,
                        comentarioTools,
                        empresaTools,
                        facturaProveedorTools,
                        emailTools,
                        graficaTools,
                        historiaTools,
                        prediccionTools
                )
                .build();
    }

}