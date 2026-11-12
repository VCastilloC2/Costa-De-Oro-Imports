package com.application.presentation.controller.admin;

import com.application.configuration.fastapi.FastApiAdmin;
import com.application.service.implementation.GenerarReporteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Reportes del panel admin. Los datos se piden a FastAPI ({@code /api/reportes/*}, mismo JSON);
 * si FastAPI no responde se calculan con {@link GenerarReporteServiceImpl} como antes.
 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GenerarReporteRestController {

    private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {
    };

    private final GenerarReporteServiceImpl generarReporteService;
    private final FastApiAdmin fastApiAdmin;

    @GetMapping("/ventas-clientes")
    public ResponseEntity<Map<String, Object>> getVentasPorClientes() {
        return reporte("ventas-clientes", () -> local("ventasClientes",
                generarReporteService.obtenerVentasPorClientes(), generarReporteService.obtenerTotalGeneralVentas()));
    }

    @GetMapping("/ventas-producto")
    public ResponseEntity<Map<String, Object>> getVentasPorProducto() {
        return reporte("ventas-producto", () -> local("ventasProducto",
                generarReporteService.obtenerVentasPorProducto(),
                generarReporteService.obtenerTotalGeneralVentasProducto()));
    }

    @GetMapping("/ventas-cliente-producto")
    public ResponseEntity<Map<String, Object>> getVentasPorClienteProducto() {
        return reporte("ventas-cliente-producto", () -> local("ventasClienteProducto",
                generarReporteService.obtenerVentasPorClienteProducto(),
                generarReporteService.obtenerTotalGeneralVentasClienteProducto()));
    }

    @GetMapping("/rentabilidad-producto")
    public ResponseEntity<Map<String, Object>> getRentabilidadPorProducto() {
        return reporte("rentabilidad-producto", () -> local("rentabilidadProducto",
                generarReporteService.obtenerRentabilidadPorProducto(),
                generarReporteService.obtenerTotalGeneralRentabilidad()));
    }

    @GetMapping("/cuentas-pagar")
    public ResponseEntity<Map<String, Object>> getCuentasPorPagar() {
        return reporte("cuentas-pagar", () -> local("cuentasPagar",
                generarReporteService.obtenerCuentasPorPagar(), generarReporteService.obtenerTotalGeneralCuentasPagar()));
    }

    @GetMapping("/reporte-compras")
    public ResponseEntity<Map<String, Object>> getReporteCompras() {
        return reporte("reporte-compras", () -> local("reporteCompras",
                generarReporteService.obtenerReporteCompras(), generarReporteService.obtenerTotalGeneralCompras()));
    }

    private ResponseEntity<Map<String, Object>> reporte(String nombre, Supplier<Map<String, Object>> local) {
        return ResponseEntity.ok(fastApiAdmin.leer("reportes." + nombre, "/api/reportes/" + nombre, MAPA, local));
    }

    private static Map<String, Object> local(String clave, List<?> filas, Object totalGeneral) {
        Map<String, Object> response = new HashMap<>();
        response.put(clave, filas);
        response.put("totalGeneral", totalGeneral);
        response.put("anio", Year.now().getValue());
        return response;
    }
}
