package com.application.presentation.controller.ia;

import com.application.presentation.dto.rag.request.RagRequest;
import com.application.presentation.dto.rag.response.RagResponse;
import com.application.service.interfaces.LlmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST — Nivel 1
 * Endpoints:
 *   POST /api/chat/directo  → envía una pregunta al LLM activo
 *   GET  /api/chat/info     → muestra qué proveedor está configurado
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final LlmService llmService;

    @PostMapping("/directo")
    public ResponseEntity<RagResponse> chat(@Valid @RequestBody RagRequest request) {
        log.info("📥 Pregunta recibida para proveedor: {}", llmService.getNombreProveedor());

        long inicio = System.currentTimeMillis();
        String respuesta = llmService.chat(request.question());
        long tiempoMs = System.currentTimeMillis() - inicio;

        log.info("📤 Respuesta generada en {} ms", tiempoMs);

        return ResponseEntity.ok(new RagResponse(
                respuesta,
                llmService.getNombreProveedor(),
                llmService.getModelo(),
                tiempoMs
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<RagResponse> info() {
        return ResponseEntity.ok(new RagResponse(
                "Proveedor activo y listo para recibir preguntas.",
                llmService.getNombreProveedor(),
                llmService.getModelo(),
                0
        ));
    }

}