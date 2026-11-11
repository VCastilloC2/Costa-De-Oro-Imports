package com.application.presentation.controller.admin;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.configuration.fastapi.FastApiException;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.GraficaMensualResponse;
import com.application.presentation.dto.PrediccionResponse;
import com.application.presentation.dto.venta.request.VentaRequest;
import com.application.service.implementation.PrediccionApiClient;
import com.application.service.implementation.usuario.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        } catch (FastApiException e) {
            // El detalle ya quedó registrado en FastApiGateway.
            return ResponseEntity.status(estadoPara(e)).build();
        }
    }

    @PostMapping("/generar")
    @ResponseBody
    public ResponseEntity<PrediccionResponse> generarPrediccion(@Valid @RequestBody VentaRequest request) {
        try {
            double resultado = predicciones.predecirMes(request.anno(), request.mes());
            return ResponseEntity.ok(new PrediccionResponse(resultado, "Predicción generada exitosamente", true));
        } catch (FastApiException e) {
            // No se reenvía e.getMessage() al navegador: puede contener detalles internos de FastAPI.
            String mensaje = e instanceof FastApiException.Unavailable
                    ? "El servicio de predicciones no está disponible. Inténtalo de nuevo en unos minutos."
                    : "No se pudo generar la predicción para el periodo solicitado.";
            return ResponseEntity.status(estadoPara(e)).body(new PrediccionResponse(null, mensaje, false));
        }
    }

    /** Unavailable -> 503; 401/403 (API key mal configurada) -> 502; otros 4xx -> 400. */
    private static HttpStatus estadoPara(FastApiException e) {
        if (e instanceof FastApiException.Unavailable) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        Integer status = e.getStatus();
        if (status != null && (status == 401 || status == 403)) {
            log.error("FastAPI rechazó la credencial de servicio: revisa FASTAPI_API_KEY y sus permisos");
            return HttpStatus.BAD_GATEWAY;
        }
        return HttpStatus.BAD_REQUEST;
    }
}