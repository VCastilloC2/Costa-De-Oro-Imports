package com.application.configuration.IA;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "costa")
public class CostaDeOroProperties {

    private String baseUrl;
    private String chatEndpoint;
    private String apiKey;
    private String model;

}