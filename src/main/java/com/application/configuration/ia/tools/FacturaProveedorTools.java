package com.application.configuration.ia.tools;

import com.application.persistence.entity.factura.FacturaProveedor;
import com.application.persistence.repository.FacturaProveedorRepository;
import com.application.service.interfaces.FacturaProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FacturaProveedorTools {

    private final FacturaProveedorRepository facturaRepository;
    private final FacturaProveedorService facturaService;

    private static final int MAX_RESULTADOS = 5;

    @Tool(
            name = "listar_facturas_recientes",
            description = """
                    Lista las facturas de proveedores más recientes registradas en el sistema.
                    """
    )
    public String listarFacturasRecientes() {
        List<FacturaProveedor> facturas = facturaRepository.findAll()
                .stream()
                .sorted((f1, f2) -> f2.getFechaRegistro().compareTo(f1.getFechaRegistro()))
                .limit(MAX_RESULTADOS)
                .toList();

        if (facturas.isEmpty()) {
            return "No se encontraron facturas de proveedores.";
        }

        return facturas.stream()
                .map(this::formatearFactura)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    @Tool(
            name = "obtener_detalle_factura",
            description = """
                    Obtiene el detalle de una factura de proveedor mediante su ID.
                    """
    )
    public String obtenerDetalleFactura(Long facturaId) {
        FacturaProveedor factura = facturaRepository.findById(facturaId).orElse(null);
        if (factura == null) {
            return "No se encontró la factura con ID: " + facturaId;
        }

        return formatearFactura(factura);
    }

    private String formatearFactura(FacturaProveedor factura) {
        return """
                ID Factura: %d
                Número: %s
                Fecha Emisión: %s
                Total: $%.2f
                Estado: %s
                Proveedor/Usuario: %s %s
                """.formatted(
                factura.getFacturaId(),
                factura.getNumeroFactura(),
                factura.getFechaEmision(),
                factura.getTotal(),
                factura.isActivo() ? "ACTIVO" : "INACTIVO",
                factura.getUsuario() != null ? factura.getUsuario().getNombres() : "N/A",
                factura.getUsuario() != null ? factura.getUsuario().getApellidos() : "N/A"
        );
    }
}
