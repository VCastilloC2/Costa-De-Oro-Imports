package com.application.configuration.ia.tools;

import com.application.persistence.entity.compra.Compra;
import com.application.persistence.repository.CompraRepository;
import com.application.service.interfaces.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompraTools {

    private final CompraRepository compraRepository;
    private final CompraService compraService;

    private static final int MAX_RESULTADOS = 5;

    @Tool(
            name = "buscar_compras_recientes",
            description = """
                    Obtiene una lista de las compras más recientes del sistema.
                    Usa esta herramienta para analizar tendencias de ventas actuales o revisar pedidos nuevos.
                    """
    )
    public String buscarComprasRecientes() {
        List<Compra> compras = compraRepository.findAll()
                .stream()
                .sorted((c1, c2) -> c2.getFecha().compareTo(c1.getFecha()))
                .limit(MAX_RESULTADOS)
                .toList();

        if (compras.isEmpty()) {
            return "No se encontraron compras recientes.";
        }

        return compras.stream()
                .map(this::formatearCompra)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    @Tool(
            name = "obtener_detalle_compra",
            description = """
                    Obtiene la información detallada de una compra mediante su ID.
                    Muestra total, fecha, estado y método de pago.
                    """
    )
    public String obtenerDetalleCompra(Long compraId) {
        Compra compra = compraRepository.findById(compraId).orElse(null);
        if (compra == null) {
            return "No se encontró la compra con ID: " + compraId;
        }

        return formatearCompra(compra);
    }

    @Tool(
            name = "listar_compras_por_usuario",
            description = """
                    Lista las compras realizadas por un usuario específico usando su ID.
                    """
    )
    public String listarComprasPorUsuario(Long usuarioId) {
        List<Compra> compras = compraRepository.findAll().stream()
                .filter(c -> c.getUsuario() != null && c.getUsuario().getUsuarioId().equals(usuarioId))
                .limit(MAX_RESULTADOS)
                .toList();

        if (compras.isEmpty()) {
            return "No se encontraron compras para el usuario ID: " + usuarioId;
        }

        return compras.stream()
                .map(this::formatearCompra)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    private String formatearCompra(Compra compra) {
        return """
                ID Compra: %d
                Total: $%.2f
                Fecha: %s
                Estado: %s
                Método Pago: %s
                Cliente: %s %s
                """.formatted(
                compra.getCompraId(),
                compra.getTotal(),
                compra.getFecha(),
                compra.getEstado(),
                compra.getEMetodoPago(),
                compra.getUsuario() != null ? compra.getUsuario().getNombres() : "N/A",
                compra.getUsuario() != null ? compra.getUsuario().getApellidos() : "N/A"
        );
    }
}
