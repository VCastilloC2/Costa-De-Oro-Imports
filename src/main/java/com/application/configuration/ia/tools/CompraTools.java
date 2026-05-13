package com.application.configuration.ia.tools;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.service.interfaces.CompraService;
import com.application.persistence.entity.compra.Compra;
import com.application.persistence.entity.compra.enums.EEstado;
import com.application.presentation.dto.compra.request.CompraCreateRequest;
import com.application.presentation.dto.compra.response.CompraDashboardResponse;
import com.application.presentation.dto.compra.response.CompraResponse;
import com.application.presentation.dto.compra.response.DetalleCompraResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompraTools {

    private final CompraService compraService;

    /*

    @Tool(
            name = "obtener_compra_por_id",
            description = "Obtiene una compra usando su ID"
    )
    public String getCompraById(
            @ToolParam(description = "ID de la compra a buscar") Long compraId) {
        try {
            Compra c = compraService.getCompraById(compraId);
            return """
                    Compra encontrada:
                    ID: %d
                    Usuario ID: %d
                    Fecha: %s
                    Total: %.2f
                    Estado: %s
                    """.formatted(
                    c.getId(),
                    c.getUsuarioId(),
                    c.getFecha() != null ? c.getFecha().toString() : "No especificado",
                    c.getTotal(),
                    c.getEstado() != null ? c.getEstado().toString() : "No especificado"
            );
        } catch (Exception e) {
            return "Compra no encontrada con ID: " + compraId;
        }
    }

    @Tool(
            name = "obtener_ingreso_anual",
            description = "Obtiene el ingreso anual"
    )
    public String getIngresoAnual() {
        try {
            Double ingreso = compraService.getIngresoAnual();
            return "Ingreso anual: $" + String.format("%.2f", ingreso);
        } catch (Exception e) {
            return "Error al obtener ingreso anual: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_total_compras_anuales",
            description = "Obtiene el total de compras anuales"
    )
    public String getTotalCompasAnuales() {
        try {
            Long total = compraService.getTotalCompasAnuales();
            return "Total de compras anuales: " + total;
        } catch (Exception e) {
            return "Error al obtener total de compras anuales: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_compras_orden_por_fecha_desc",
            description = "Obtiene las compras ordenadas por fecha descendente"
    )
    public String getCompraByOrderAndFechaDesc() {
        try {
            List<CompraDashboardResponse> compras = compraService.getCompraByOrderAndFechaDesc();
            if (compras == null || compras.isEmpty()) {
                return "No hay compras registradas.";
            }
            StringBuilder result = new StringBuilder("Compras ordenadas por fecha descendente:\n\n");
            for (CompraDashboardResponse c : compras) {
                result.append(String.format("ID: %d - Usuario ID: %d - Fecha: %s - Total: %.2f - Estado: %s\n",
                        c.getCompraId(),
                        c.getUsuarioId(),
                        c.getFecha() != null ? c.getFecha().toString() : "No especificado",
                        c.getTotal(),
                        c.getEstado() != null ? c.getEstado().toString() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener compras ordenadas por fecha descendente: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_detalle_compra",
            description = "Obtiene el detalle de una compra usando su ID"
    )
    public String getDetalleCompra(
            @ToolParam(description = "ID de la compra") Long compraId) {
        try {
            DetalleCompraResponse dcr = compraService.getDetalleCompra(compraId);
            return """
                    Detalle de compra:
                    Compra ID: %d
                    Producto ID: %d
                    Producto nombre: %s
                    Cantidad: %d
                    Precio unitario: %.2f
                    Subtotal: %.2f
                    """.formatted(
                    dcr.getCompraId(),
                    dcr.getProductoId(),
                    dcr.getProductoNombre() != null ? dcr.getProductoNombre() : "No especificado",
                    dcr.getCantidad(),
                    dcr.getPrecioUnitario(),
                    dcr.getSubtotal()
            );
        } catch (Exception e) {
            return "Detalle de compra no encontrado con ID: " + compraId;
        }
    }

    @Tool(
            name = "crear_compra",
            description = "Crea una nueva compra"
    )
    public String createCompra(
            @ToolParam(description = "Usuario principal autenticado") CustomUserPrincipal principal,
            @ToolParam(description = "Solicitud de creación de compra") CompraCreateRequest compraRequest) {
        try {
            CompraResponse response = compraService.createCompra(principal, compraRequest);
            return """
                    Compra creada:
                    ID: %d
                    Usuario ID: %d
                    Fecha: %s
                    Total: %.2f
                    Estado: %s
                    """.formatted(
                    response.getId(),
                    response.getUsuarioId(),
                    response.getFecha() != null ? response.getFecha().toString() : "No especificado",
                    response.getTotal(),
                    response.getEstado() != null ? response.getEstado().toString() : "No especificado"
            );
        } catch (Exception e) {
            return "❌ Error al crear compra: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_estado_compra",
            description = "Actualiza el estado de una compra"
    )
    public String updateEstadoCompra(
            @ToolParam(description = "ID de la compra") Long compraId,
            @ToolParam(description = "Nuevo estado") EEstado estado) {
        try {
            compraService.updateEstadoCompra(compraId, estado);
            return "✅ Estado de la compra con ID " + compraId + " actualizado a " + estado + " exitosamente.";
        } catch (Exception e) {
            return "❌ Error al actualizar estado de la compra: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_stock_producto_por_compra_id_y_estado",
            description = "Actualiza el stock de un producto por compra ID y estado de compra"
    )
    public String updateStockProductoByCompraIdAndEstadoCompra(
            @ToolParam(description = "ID de la compra") Long compraId,
            @ToolParam(description = "Estado de la compra") EEstado estado) {
        try {
            compraService.updateStockProductoByCompraIdAndEstadoCompra(compraId, estado);
            return "✅ Stock de producto actualizado exitosamente para la compra ID " + compraId + " con estado " + estado + ".";
        } catch (Exception e) {
            return "❌ Error al actualizar stock de producto: " + e.getMessage();
        }
    }

    @Tool(
            name = "limpiar_compras_pendientes",
            description = "Limpia las compras con estado pendiente"
    )
    public String limpiarComprasConEstadoPendiente() {
        try {
            compraService.limpiarComprasConEstadoPendiente();
            return "✅ Compras pendientes limpiadas exitosamente.";
        } catch (Exception e) {
            return "❌ Error al limpiar compras pendientes: " + e.getMessage();
        }
    }

    @Tool(
            name = "cambiar_estado_compra_simple",
            description = "Cambia el estado de una compra usando un string"
    )
    public String changeEstadoCompra(
            @ToolParam(description = "ID de la compra") Long compraId,
            @ToolParam(description = "Estado como string") String estado) {
        try {
            BaseResponse response = compraService.changeEstadoCompra(compraId, estado);
            if (response.isSuccess()) {
                return "✅ Estado de la compra con ID " + compraId + " cambiado a " + estado + " exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al cambiar estado de la compra: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al cambiar estado de la compra con ID " + compraId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "convertir_compra_a_response",
            description = "Convierte una entidad Compra a su DTO de respuesta"
    )
    public String toResponse(
            @ToolParam(description = "Entidad Compra") Compra compra) {
        try {
            CompraResponse response = compraService.toResponse(compra);
            return """
                    Compra Response:
                    ID: %d
                    Usuario ID: %d
                    Fecha: %s
                    Total: %.2f
                    Estado: %s
                    """.formatted(
                    response.getId(),
                    response.getUsuarioId(),
                    response.getFecha() != null ? response.getFecha().toString() : "No especificado",
                    response.getTotal(),
                    response.getEstado() != null ? response.getEstado().toString() : "No especificado"
            );
        } catch (Exception e) {
            return "Error al convertir compra a response: " + e.getMessage();
        }
    }

     */
}