package com.application.service.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

/** Sends MIME messages through Gmail API using an OAuth refresh token. */
@Component
public class GmailApiClient {
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String SEND_URL = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";
    private final RestClient restClient = RestClient.create();
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private volatile String accessToken;
    private volatile Instant accessTokenExpiresAt = Instant.EPOCH;

    public GmailApiClient(
            @Value("${gmail.api.client-id}") String clientId,
            @Value("${gmail.api.client-secret}") String clientSecret,
            @Value("${gmail.api.refresh-token}") String refreshToken) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
    }

    public void send(MimeMessage mimeMessage) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            mimeMessage.writeTo(output);
            String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(output.toByteArray());
            restClient.post()
                    .uri(SEND_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> headers.setBearerAuth(getAccessToken()))
                    .body(Map.of("raw", raw))
                    .retrieve()
                    .toBodilessEntity();
        } catch (MessagingException | IOException ex) {
            throw new IllegalStateException("No se pudo serializar el mensaje MIME", ex);
        }
    }

    private String getAccessToken() {
        Instant now = Instant.now();
        if (accessToken != null && now.isBefore(accessTokenExpiresAt.minusSeconds(60))) {
            return accessToken;
        }
        synchronized (this) {
            now = Instant.now();
            if (accessToken != null && now.isBefore(accessTokenExpiresAt.minusSeconds(60))) {
                return accessToken;
            }
            if (clientId.isBlank() || clientSecret.isBlank() || refreshToken.isBlank()) {
                throw new IllegalStateException(
                        "Configura GMAIL_API_CLIENT_ID, GMAIL_API_CLIENT_SECRET y GMAIL_API_REFRESH_TOKEN");
            }

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("client_id", clientId);
            form.add("client_secret", clientSecret);
            form.add("refresh_token", refreshToken);
            form.add("grant_type", "refresh_token");
            JsonNode tokenResponse = restClient.post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(JsonNode.class);
            if (tokenResponse == null || !tokenResponse.hasNonNull("access_token")) {
                throw new IllegalStateException("Google no devolvió un access token para Gmail API");
            }
            accessToken = tokenResponse.path("access_token").asText();
            accessTokenExpiresAt = now.plusSeconds(tokenResponse.path("expires_in").asLong(3600));
            return accessToken;
        }
    }
}
