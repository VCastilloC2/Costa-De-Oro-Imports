package com.application.presentation.controller;

import com.application.configuration.fastapi.FastApiException;
import com.application.presentation.dto.GraficaMensualResponse;
import com.application.service.implementation.PrediccionApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Proxy público de Spring para la app web y clientes móviles autenticados. */
@RestController
@RequiredArgsConstructor
public class PrediccionesVentasApiController {
    private final PrediccionApiClient predicciones;

    @GetMapping("/api/v1/predicciones/ventas")
    public ResponseEntity<GraficaMensualResponse> ventas(
            @RequestParam(defaultValue = "12") int meses) {
        try {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .body(predicciones.obtenerGrafica(6, Math.max(0, Math.min(meses, 24))));
        } catch (FastApiException e) {
            HttpStatus status = e instanceof FastApiException.Unavailable
                    ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.BAD_GATEWAY;
            return ResponseEntity.status(status).build();
        }
    }

    @GetMapping("/api/v1/predicciones/ventas/evaluacion")
    public ResponseEntity<PrediccionApiClient.EvaluacionReciente> evaluarUltimosMeses(
            @RequestParam(defaultValue = "5") int meses) {
        if (meses < 1 || meses > 12) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noStore())
                    .body(predicciones.evaluarUltimosMeses(meses));
        } catch (FastApiException e) {
            HttpStatus status = e instanceof FastApiException.Unavailable
                    ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.BAD_GATEWAY;
            return ResponseEntity.status(status).build();
        }
    }
}
