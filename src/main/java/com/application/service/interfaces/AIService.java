package com.application.service.interfaces;

import reactor.core.publisher.Flux;

public interface AIService {
    Flux<String> preguntar(String mensaje);
}