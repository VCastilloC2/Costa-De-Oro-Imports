package com.application.presentation.controller.ia;

import com.application.presentation.dto.chat.request.ChatRequest;
import com.application.service.http.AIHttp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AIController {

    private final AIHttp aiHttp;

    @PostMapping("/chat")
    public String ask(
            @RequestBody ChatRequest request
    ) {
        return aiHttp.preguntar(
                request.message()
        );
    }

}