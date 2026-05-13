package com.application.configuration.ia.tools;

import com.application.persistence.entity.factura.FacturaProveedor;
import com.application.service.interfaces.FacturaProveedorService;
import com.application.presentation.dto.factura.request.FacturaProveedorRequest;
import com.application.presentation.dto.factura.response.FacturaProductoProveedorResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FacturaProveedorTools {

    private final FacturaProveedorService facturaProveedorService;

    /*

    @Tool(
            name = "obtener_productos_por_proveedor_id",
            description = "Obtiene los productos por proveedor ID"
    )
    public String getProductosByProveedorId(
            @ToolParam(description = "ID del proveedor") Long proveedorId) {
        try {
            List<FacturaProductoProveedorResponse> productos = facturaProveedorService.getProductosByProveedorId(proveedorId);
            if (productos == null || productos.isEmpty()) {
                return "No hay productos para el proveedor ID: " + proveedorId;
            }
            StringBuilder result = new StringBuilder("Productos del proveedor ID " + proveedorId + ":\n\n");
            for (FacturaProductoProveedorResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Precio: %.2f - Stock: %d\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getPrecio(),
                        p.getStock()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos por proveedor ID: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_factura_compra",
            description = "Crea una factura de compra"
    )
    public String crearFacturaCompra(
            @ToolParam(description = "Solicitud de factura de compra") FacturaProveedorRequest request) {
        try {
            BaseResponse response = facturaProveedorService.crearFacturaCompra(request);
            if (response.isSuccess()) {
                return "✅ Factura de compra creada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al crear factura de compra: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al crear factura de compra: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_facturas_por_proveedor_id",
            description = "Obtiene las facturas por proveedor ID"
    )
    public String getFacturasByProveedorId(
            @ToolParam(description = "ID del proveedor") Long proveedorId) {
        try {
            List<FacturaProveedor> facturas = facturaProveedorService.getFacturasByProveedorId(proveedorId);
            if (facturas == null || facturas.isEmpty()) {
                return "No hay facturas para el proveedor ID: " + proveedorId;
            }
            StringBuilder result = new StringBuilder("Facturas del proveedor ID " + proveedorId + ":\n\n");
            for (FacturaProveedor f : facturas) {
                result.append(String.format("ID: %d - Fecha: %s - Total: %.2f - Estado: %s\n",
                        f.getId(),
                        f.getFecha() != null ? f.getFecha().toString() : "No especificado",
                        f.getTotal(),
                        f.getEstado() != null ? f.getEstado().toString() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener facturas por proveedor ID: " + e.getMessage();
        }
    }

     */
}