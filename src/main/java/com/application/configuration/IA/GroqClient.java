package com.application.configuration.ia;

import com.application.presentation.dto.Choice;
import com.application.presentation.dto.Message;
import com.application.presentation.dto.groq.request.GroqRequest;
import com.application.presentation.dto.groq.response.GroqResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class GroqClient {

    private final GroqProperties groqProperties;
    private final RestTemplate restTemplate;

    public GroqClient(GroqProperties groqProperties) {
        this.groqProperties = groqProperties;
        this.restTemplate = new RestTemplate();
    }

    public String chat(String systemPrompt, String userPrompt) {
        if (groqProperties.getApiKey() == null || groqProperties.getApiKey().isBlank()) {
            throw new IllegalStateException("La variable GROQ_API_KEY no está configurada.");
        }

        String url = groqProperties.getBaseUrl() + groqProperties.getChatEndpoint();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqProperties.getApiKey());

        GroqRequest request = new GroqRequest(
                groqProperties.getModel(),
                List.of(
                        new Message("system", systemPrompt),
                        new Message("user", userPrompt)
                ),
                0.1
        );

        HttpEntity<GroqRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GroqResponse> response = restTemplate.postForEntity(
                url,
                entity,
                GroqResponse.class
        );

        GroqResponse body = response.getBody();

        if (body == null || body.choices() == null || body.choices().isEmpty()) {
            throw new IllegalStateException("Groq no retornó contenido.");
        }

        Choice choice = body.choices().get(0);

        if (choice.message() == null || choice.message().content() == null) {
            throw new IllegalStateException("Groq no retornó mensaje válido.");
        }

        return choice.message().content().trim();
    }
}