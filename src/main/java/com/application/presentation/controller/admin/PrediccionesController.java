package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.DatosGraficaResponse;
import com.application.presentation.dto.PrediccionResponse;
import com.application.presentation.dto.venta.request.VentaRequest;
import com.application.service.implementation.PrediccionServiceImpl;
import com.application.service.implementation.usuario.UsuarioServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/prediccion")
@RequiredArgsConstructor
public class PrediccionesController {

    private final UsuarioServiceImpl usuarioServiceImpl;
    private final PrediccionServiceImpl prediccionServiceImpl;

    @GetMapping({ "", "/" })
    public String DashboardPrediccion(@AuthenticationPrincipal CustomUserPrincipal principal,
                                      @RequestParam(value = "mensaje", required = false) String mensaje,
                                      Model model) {

        // Usuario Actual
        Usuario usuario = usuarioServiceImpl.getUsuarioByCorreo(principal.getUsername());

        // Obtener datos para la gráfica
        DatosGraficaResponse datosGrafica = prediccionServiceImpl.obtenerDatosParaGrafica();

        model.addAttribute("usuario", usuario);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("datosGrafica", datosGrafica);
        model.addAttribute("añoActual", Year.now().getValue());

        return "DashboardPredicciones";
    }

    @PostMapping("/generar")
    @ResponseBody
    public ResponseEntity<PrediccionResponse> generarPrediccion(@Valid @RequestBody VentaRequest request) {
        try {
            double resultado = prediccionServiceImpl.predecir(request);
            PrediccionResponse response = new PrediccionResponse(
                    resultado,
                    "Predicción generada exitosamente",
                    true
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            PrediccionResponse
                    response = new PrediccionResponse(
                    null,
                    "Error generando predicción: " + e.getMessage(),
                    false
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/datos-grafica")
    @ResponseBody
    public ResponseEntity<DatosGraficaResponse> obtenerDatosGrafica() {
        try {
            DatosGraficaResponse datos = prediccionServiceImpl.obtenerDatosParaGrafica();
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/prediccion-2026")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generarPrediccion2026() {
        try {
            Map<String, Object> resultado = prediccionServiceImpl.generarPrediccion2026();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error generando predicción: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

}