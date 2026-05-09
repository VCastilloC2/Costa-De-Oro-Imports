package com.application.presentation.controller.ia;

import com.application.service.http.AIHttp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AIController {

    private final AIHttp aiHttp;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> askStream(@RequestParam String message) {
        return aiHttp.preguntar(message)
                .map(token ->
                        ServerSentEvent.builder(token).build()
                );
    }

}