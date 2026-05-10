package com.application.presentation.controller.ia;

import com.application.presentation.dto.chat.response.ChatResponse;
import com.application.presentation.dto.rag.request.RagRequest;
import com.application.presentation.dto.rag.response.RagResponse;
import com.application.service.http.RagService;
import com.application.service.interfaces.LlmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST — Nivel 1
 * Endpoints:
 *   POST /api/chat/directo  → envía una pregunta al LLM activo
 *   GET  /api/chat/info     → muestra qué proveedor está configurado
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class RagController {

    private static final Logger log = LoggerFactory.getLogger(RagController.class);

    private final LlmService llmService;
    private final RagService ragService;

    @PostMapping("/api/rag/ask")
    @ResponseBody
    public ResponseEntity<RagResponse> ask(@RequestBody RagRequest request) {
        if (request == null || request.question() == null || request.question().isBlank()) {
            return ResponseEntity.badRequest().body(
                    new RagResponse(
                            "La pregunta no puede estar vacía.",
                            List.of(),
                            List.of(),
                            false
                    )
            );
        }

        return ResponseEntity.ok(ragService.ask(request.question()));
    }

    @PostMapping("/directo")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody RagRequest request) {

        log.info("Pregunta recibida para proveedor: {}", llmService.getNombreProveedor());

        long inicio = System.currentTimeMillis();

        String respuesta = llmService.chat(request.question());

        long tiempoMs = System.currentTimeMillis() - inicio;

        log.info("Respuesta generada en {} ms", tiempoMs);

        return ResponseEntity.ok(
                new ChatResponse(
                        respuesta,
                        llmService.getNombreProveedor(),
                        llmService.getModelo(),
                        tiempoMs
                )
        );
    }

    @GetMapping("/info")
    public ResponseEntity<ChatResponse> info() {
        return ResponseEntity.ok(
                new ChatResponse(
                        "Proveedor activo y listo para recibir preguntas.",
                        llmService.getNombreProveedor(),
                        llmService.getModelo(),
                        0
                )
        );
    }

}