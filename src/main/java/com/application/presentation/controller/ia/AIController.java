package com.application.presentation.controller.ia;

import com.application.service.http.AIHttp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AIController {

    private final AIHttp aiHttp;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> ask(
            @RequestBody Map<String, String> body) {

        String mensaje = body.get("message");
        String chatId = body.getOrDefault("chatId", "default");

        String respuesta = aiHttp.preguntar(mensaje, chatId);

        return ResponseEntity.ok(Map.of("response", respuesta));
    }

}