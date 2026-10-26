package com.costadeoro.admin.prediccion; // AJUSTA al paquete de tu proyecto

// Imports de tu proyecto que ya usas: Usuario, UsuarioServiceImpl, CustomUserPrincipal,
// VentaRequest, PrediccionResponse. Añade: lombok.extern.slf4j.Slf4j, lombok.RequiredArgsConstructor,
// jakarta.validation.Valid, org.springframework.http.*, org.springframework.security.core.annotation.AuthenticationPrincipal,
// org.springframework.stereotype.Controller, org.springframework.ui.Model, org.springframework.web.bind.annotation.*

import java.time.Year;

@Slf4j
@Controller
@RequestMapping("/admin/prediccion")
@RequiredArgsConstructor
public class PrediccionesController {

    /** Años de historia que muestra la gráfica: meses exactos = ANIOS_GRAFICA * 12. */
    private static final int ANIOS_GRAFICA = 6;

    /** Meses que agrega el botón "Generar Predicción" (el año siguiente). */
    private static final int MESES_PROYECCION = 12;

    private final UsuarioServiceImpl usuarioServiceImpl;
    private final PrediccionApiClient predicciones;

    @GetMapping({"", "/"})
    public String dashboardPrediccion(@AuthenticationPrincipal CustomUserPrincipal principal,
                                      @RequestParam(value = "mensaje", required = false) String mensaje,
                                      Model model) {

        Usuario usuario = usuarioServiceImpl.getUsuarioByCorreo(principal.getUsername());

        model.addAttribute("usuario", usuario);
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("añoActual", Year.now().getValue());
        model.addAttribute("aniosGrafica", ANIOS_GRAFICA);
        model.addAttribute("mesesProyeccion", MESES_PROYECCION);

        // La gráfica se pinta en el navegador con datos de /datos-grafica.
        return "DashboardPredicciones";
    }

    /** Serie mensual para ApexCharts. proyeccion_meses = 0 (historia) o 12 (año siguiente). */
    @GetMapping("/datos-grafica")
    @ResponseBody
    public ResponseEntity<GraficaMensualResponse> obtenerDatosGrafica(
            @RequestParam(value = "proyeccion_meses", defaultValue = "0") int proyeccionMeses) {
        try {
            int meses = Math.max(0, Math.min(proyeccionMeses, MESES_PROYECCION));
            return ResponseEntity.ok(predicciones.obtenerGrafica(ANIOS_GRAFICA, meses));
        } catch (Exception e) {
            log.error("No se pudo obtener la gráfica desde la API de predicciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/generar")
    @ResponseBody
    public ResponseEntity<PrediccionResponse> generarPrediccion(@Valid @RequestBody VentaRequest request) {
        try {
            double resultado = predicciones.predecirMes(request.anno(), request.mes());
            return ResponseEntity.ok(new PrediccionResponse(resultado, "Predicción generada exitosamente", true));
        } catch (Exception e) {
            log.error("Error generando predicción mensual: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    new PrediccionResponse(null, "Error generando predicción: " + e.getMessage(), false));
        }
    }
}
