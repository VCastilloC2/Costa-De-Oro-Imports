package com.application.configuration.ia;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MCPPriorityConfig {

    @Bean
    public List<String> mcpPriority() {
        return List.of(
                "dbhub"
        );
    }

}