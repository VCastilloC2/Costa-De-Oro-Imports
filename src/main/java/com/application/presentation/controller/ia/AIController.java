package com.application.presentation.controller.ia;

import com.application.service.http.AIHttp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
public class AIController {

    private final AIHttp aiHttp;

    @GetMapping("/ask")
    public ResponseEntity<String> ask(
            @RequestParam String prompt,
            @RequestParam(required = false, defaultValue = "default") String chatId) {
        return ResponseEntity.ok(aiHttp.preguntar(prompt, chatId));
    }

}