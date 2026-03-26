package com.application.presentation.controller.ia;

import com.application.service.http.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @GetMapping("/ask")
    public ResponseEntity<String> ask(@RequestParam String prompt) {
        String respuesta = aiService.preguntar(prompt);
        return ResponseEntity.ok(respuesta);
    }

}