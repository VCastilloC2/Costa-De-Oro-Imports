package com.application.presentation.controller.ia;

import com.application.service.http.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @GetMapping("/ask")
    public String ask(@RequestParam String prompt) {
        return aiService.preguntar(prompt);
    }

}