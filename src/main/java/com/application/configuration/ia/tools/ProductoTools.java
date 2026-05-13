package com.application.configuration.ia.tools;

import com.application.service.interfaces.producto.ProductoService;
import com.application.persistence.entity.producto.Producto;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.general.response.GeneralResponse;
import com.application.presentation.dto.producto.request.FiltroRequest;
import com.application.presentation.dto.producto.request.ProductoCreateRequest;
import com.application.presentation.dto.producto.response.ProductoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductoTools {

    private final ProductoService productoService;

    /*

    @Tool(
            name = "obtener_producto_por_id",
            description = "Obtiene un producto usando su ID"
    )
    public String getProductoById(
            @ToolParam(description = "ID del producto a buscar") Long id) {
        try {
            Producto p = productoService.getProductoById(id);
            return """
                    Producto encontrado:
                    ID: %d
                    Nombre: %s
                    Descripción: %s
                    Precio: %.2f
                    Estado: %s
                    """.formatted(
                    p.getId(),
                    p.getNombre() != null ? p.getNombre() : "No especificado",
                    p.getDescripcion() != null ? p.getDescripcion() : "No especificado",
                    p.getPrecio(),
                    p.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Producto no encontrado con ID: " + id;
        }
    }

    @Tool(
            name = "obtener_producto_response_por_id",
            description = "Obtiene un producto response usando su ID"
    )
    public String getProductoResponseById(
            @ToolParam(description = "ID del producto a buscar") Long id) {
        try {
            ProductoResponse pr = productoService.getProductoResponseById(id);
            return """
                    Producto Response encontrado:
                    ID: %d
                    Nombre: %s
                    Descripción: %s
                    Precio: %.2f
                    Stock: %d
                    Categoría: %s
                    Subcategoría: %s
                    Marca: %s
                    País: %s
                    Estado: %s
                    """.formatted(
                    pr.getId(),
                    pr.getNombre() != null ? pr.getNombre() : "No especificado",
                    pr.getDescripcion() != null ? pr.getDescripcion() : "No especificado",
                    pr.getPrecio(),
                    pr.getStock(),
                    pr.getCategoriaNombre() != null ? pr.getCategoriaNombre() : "No especificado",
                    pr.getSubcategoriaNombre() != null ? pr.getSubcategoriaNombre() : "No especificado",
                    pr.getMarca() != null ? pr.getMarca() : "No especificado",
                    pr.getPaisOrigen() != null ? pr.getPaisOrigen() : "No especificado",
                    pr.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Producto Response no encontrado con ID: " + id;
        }
    }

    @Tool(
            name = "obtener_productos",
            description = "Obtiene la lista de todos los productos"
    )
    public String getProductos() {
        try {
            List<ProductoResponse> productos = productoService.getProductos();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos registrados.";
            }
            StringBuilder result = new StringBuilder("Lista de productos:\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Precio: %.2f - Stock: %d\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getPrecio(),
                        p.getStock()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_productos_activos",
            description = "Obtiene la lista de productos activos"
    )
    public String getProductosActivos() {
        try {
            List<ProductoResponse> productos = productoService.getProductosActivos();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos activos.";
            }
            StringBuilder result = new StringBuilder("Productos activos:\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Precio: %.2f\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getPrecio()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos activos: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_packs_activos",
            description = "Obtiene la lista de packs activos"
    )
    public String getPacksActivos() {
        try {
            List<ProductoResponse> packs = productoService.getPacksActivos();
            if (packs == null || packs.isEmpty()) {
                return "No hay packs activos.";
            }
            StringBuilder result = new StringBuilder("Packs activos:\n\n");
            for (ProductoResponse p : packs) {
                result.append(String.format("ID: %d - Nombre: %s - Precio: %.2f\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getPrecio()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener packs activos: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_productos_mas_vendidos_activos",
            description = "Obtiene los productos más vendidos activos"
    )
    public String getProductosMasVendidosActivos() {
        try {
            List<ProductoResponse> productos = productoService.getProductosMasVendidosActivos();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos más vendidos activos.";
            }
            StringBuilder result = new StringBuilder("Productos más vendidos activos:\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Total vendido: %d\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getTotalVendido()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos más vendidos activos: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_productos_mas_vendidos_por_categoria_id_activos",
            description = "Obtiene los productos más vendidos por categoría ID activos"
    )
    public String getProductosMasVendidosByCategoriaIdActivos(
            @ToolParam(description = "ID de la categoría") Long categoriaId) {
        try {
            List<ProductoResponse> productos = productoService.getProductosMasVendidosByCategoriaIdActivos(categoriaId);
            if (productos == null || productos.isEmpty()) {
                return "No hay productos más vendidos activos para la categoría ID: " + categoriaId;
            }
            StringBuilder result = new StringBuilder(String.format("Productos más vendidos activos para la categoría ID %d:\n\n", categoriaId));
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Total vendido: %d\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getTotalVendido()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos más vendidos por categoría ID: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_productos_mas_vendidos_por_categoria_ids_activos",
            description = "Obtiene los productos más vendidos por lista de categoría IDs activos"
    )
    public String getProductosMasVendidosByCategoriaIdsActivos(
            @ToolParam(description = "Lista de IDs de categorías") List<Long> categoriaIds) {
        try {
            List<ProductoResponse> productos = productoService.getProductosMasVendidosByCategoriaIdsActivos(categoriaIds);
            if (productos == null || productos.isEmpty()) {
                return "No hay productos más vendidos activos para las categorías IDs: " + categoriaIds;
            }
            StringBuilder result = new StringBuilder("Productos más vendidos activos para las categorías IDs: " + categoriaIds + "\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Total vendido: %d\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getTotalVendido()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos más vendidos por categoría IDs: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_paises_producto",
            description = "Obtiene la lista de países de los productos"
    )
    public String getPaisesProducto() {
        try {
            List<String> paises = productoService.getPaisesProducto();
            if (paises == null || paises.isEmpty()) {
                return "No hay países de productos registrados.";
            }
            StringBuilder result = new StringBuilder("Países de los productos:\n\n");
            for (String pais : paises) {
                result.append("- ").append(pais).append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener países de productos: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_marcas_productos",
            description = "Obtiene la lista de marcas de los productos"
    )
    public String getMarcasProductos() {
        try {
            List<String> marcas = productoService.getMarcasProductos();
            if (marcas == null || marcas.isEmpty()) {
                return "No hay marcas de productos registradas.";
            }
            StringBuilder result = new StringBuilder("Marcas de los productos:\n\n");
            for (String marca : marcas) {
                result.append("- ").append(marca).append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener marcas de productos: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_productos_por_precio_asc",
            description = "Obtiene los productos ordenados por precio ascendente"
    )
    public String getProductosPorPrecioAsc() {
        try {
            List<ProductoResponse> productos = productoService.getProductosPorPrecioAsc();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos para ordenar por precio ascendente.";
            }
            StringBuilder result = new StringBuilder("Productos ordenados por precio ascendente:\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Precio: %.2f\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getPrecio()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos por precio ascendente: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_productos_por_precio_desc",
            description = "Obtiene los productos ordenados por precio descendente"
    )
    public String getProductosPorPrecioDesc() {
        try {
            List<ProductoResponse> productos = productoService.getProductosPorPrecioDesc();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos para ordenar por precio descendente.";
            }
            StringBuilder result = new StringBuilder("Productos ordenados por precio descendente:\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Precio: %.2f\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getPrecio()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos por precio descendente: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_productos_por_nombre_asc",
            description = "Obtiene los productos ordenados por nombre ascendente"
    )
    public String getProductosPorNombreAsc() {
        try {
            List<ProductoResponse> productos = productoService.getProductosPorNombreAsc();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos para ordenar por nombre ascendente.";
            }
            StringBuilder result = new StringBuilder("Productos ordenados por nombre ascendente:\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos por nombre ascendente: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_productos_por_nombre_desc",
            description = "Obtiene los productos ordenados por nombre descendente"
    )
    public String getProductosPorNombreDesc() {
        try {
            List<ProductoResponse> productos = productoService.getProductosPorNombreDesc();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos para ordenar por nombre descendente.";
            }
            StringBuilder result = new StringBuilder("Productos ordenados por nombre descendente:\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos por nombre descendente: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_categorias_activas_con_subcategorias",
            description = "Obtiene las categorías activas con sus subcategorías"
    )
    public String getCategoriasActivasConSubcategorias() {
        try {
            Map<String, List<String>> categorias = productoService.getCategoriasActivasConSubcategorias();
            if (categorias == null || categorias.isEmpty()) {
                return "No hay categorías activas con subcategorías.";
            }
            StringBuilder result = new StringBuilder("Categorías activas con sus subcategorías:\n\n");
            for (Map.Entry<String, List<String>> entry : categorias.entrySet()) {
                result.append("Categoría: ").append(entry.getKey()).append("\n");
                for (String subcategoria : entry.getValue()) {
                    result.append("  - ").append(subcategoria).append("\n");
                }
                result.append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener categorías activas con subcategorías: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_productos_mas_vendidos",
            description = "Obtiene los productos más vendidos"
    )
    public String getProductosMasVendidos() {
        try {
            List<ProductoResponse> productos = productoService.getProductosMasVendidos();
            if (productos == null || productos.isEmpty()) {
                return "No hay productos más vendidos.";
            }
            StringBuilder result = new StringBuilder("Productos más vendidos:\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Total vendido: %d\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getTotalVendido()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener productos más vendidos: " + e.getMessage();
        }
    }

    @Tool(
            name = "filtrar_productos",
            description = "Filtra productos según los criterios proporcionados"
    )
    public String filtrarProductos(
            @ToolParam(description = "Solicitud de filtros") FiltroRequest filtros) {
        try {
            List<ProductoResponse> productos = productoService.filtrarProductos(filtros);
            if (productos == null || productos.isEmpty()) {
                return "No se encontraron productos con los filtros proporcionados.";
            }
            StringBuilder result = new StringBuilder("Productos filtrados:\n\n");
            for (ProductoResponse p : productos) {
                result.append(String.format("ID: %d - Nombre: %s - Precio: %.2f - Stock: %d\n",
                        p.getId(),
                        p.getNombre() != null ? p.getNombre() : "No especificado",
                        p.getPrecio(),
                        p.getStock()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al filtrar productos: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_producto",
            description = "Crea un nuevo producto"
    )
    public String createProducto(
            @ToolParam(description = "Solicitud de creación de producto") ProductoCreateRequest productoRequest) {
        try {
            BaseResponse response = productoService.createProducto(productoRequest);
            if (response.isSuccess()) {
                return "✅ Producto creado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al crear producto: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al crear producto: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_producto",
            description = "Actualiza un producto existente"
    )
    public String updateProducto(
            @ToolParam(description = "Solicitud de actualización de producto") ProductoCreateRequest productoRequest,
            @ToolParam(description = "ID del producto a actualizar") Long id) {
        try {
            BaseResponse response = productoService.updateProducto(productoRequest, id);
            if (response.isSuccess()) {
                return "✅ Producto con ID " + id + " actualizado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar producto: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar producto con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "cambiar_estado_producto",
            description = "Cambia el estado de un producto"
    )
    public String changeEstadoProducto(
            @ToolParam(description = "ID del producto") Long id) {
        try {
            BaseResponse response = productoService.changeEstadoProducto(id);
            if (response.isSuccess()) {
                return "✅ Estado del producto con ID " + id + " cambiado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al cambiar estado del producto: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al cambiar estado del producto con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "eliminar_producto",
            description = "Elimina un producto usando su ID"
    )
    public String deleteProducto(
            @ToolParam(description = "ID del producto a eliminar") Long id) {
        try {
            BaseResponse response = productoService.deleteProducto(id);
            if (response.isSuccess()) {
                return "✅ Producto con ID " + id + " eliminado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al eliminar producto: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al eliminar producto con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "convertir_a_respuesta",
            description = "Convierte una entidad Producto a su DTO de respuesta"
    )
    public String toResponse(
            @ToolParam(description = "Entidad Producto") Producto producto) {
        try {
            ProductoResponse response = productoService.toResponse(producto);
            return """
                    Producto Response:
                    ID: %d
                    Nombre: %s
                    Descripción: %s
                    Precio: %.2f
                    Stock: %d
                    Categoría: %s
                    Subcategoría: %s
                    Marca: %s
                    País: %s
                    Estado: %s
                    """.formatted(
                    response.getId(),
                    response.getNombre() != null ? response.getNombre() : "No especificado",
                    response.getDescripcion() != null ? response.getDescripcion() : "No especificado",
                    response.getPrecio(),
                    response.getStock(),
                    response.getCategoriaNombre() != null ? response.getCategoriaNombre() : "No especificado",
                    response.getSubcategoriaNombre() != null ? response.getSubcategoriaNombre() : "No especificado",
                    response.getMarca() != null ? response.getMarca() : "No especificado",
                    response.getPaisOrigen() != null ? response.getPaisOrigen() : "No especificado",
                    response.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Error al convertir producto a response: " + e.getMessage();
        }
    }
     */
}