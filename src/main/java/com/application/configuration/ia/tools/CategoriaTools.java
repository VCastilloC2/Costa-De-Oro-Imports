package com.application.configuration.ia.tools;

import com.application.service.interfaces.categoria.CategoriaService;
import com.application.persistence.entity.categoria.Categoria;
import com.application.presentation.dto.categoria.request.CategoriaCreateRequest;
import com.application.presentation.dto.categoria.response.CategoriaProductoResponse;
import com.application.presentation.dto.categoria.response.CategoriaResponse;
import com.application.presentation.dto.categoria.response.SubCategoriaProductoResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoriaTools {

    private final CategoriaService categoriaService;

    /*

    @Tool(
            name = "obtener_categoria_por_id",
            description = "Obtiene una categoría usando su ID"
    )
    public String getCategoriaById(
            @ToolParam(description = "ID de la categoría a buscar") Long id) {
        try {
            Categoria c = categoriaService.getCategoriaById(id);
            return """
                    Categoría encontrada:
                    ID: %d
                    Nombre: %s
                    Descripción: %s
                    Estado: %s
                    """.formatted(
                    c.getId(),
                    c.getNombre() != null ? c.getNombre() : "No especificado",
                    c.getDescripcion() != null ? c.getDescripcion() : "No especificado",
                    c.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Categoría no encontrada con ID: " + id;
        }
    }

    @Tool(
            name = "obtener_categoria_response_por_id",
            description = "Obtiene una categoría response usando su ID"
    )
    public String getCategoriaResponseById(
            @ToolParam(description = "ID de la categoría a buscar") Long id) {
        try {
            CategoriaResponse cr = categoriaService.getCategoriaResponseById(id);
            return """
                    Categoría Response encontrada:
                    ID: %d
                    Nombre: %s
                    Descripción: %s
                    Estado: %s
                    """.formatted(
                    cr.getId(),
                    cr.getNombre() != null ? cr.getNombre() : "No especificado",
                    cr.getDescripcion() != null ? cr.getDescripcion() : "No especificado",
                    cr.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Categoria Response no encontrada con ID: " + id;
        }
    }

    @Tool(
            name = "obtener_categorias",
            description = "Obtiene la lista de todas las categorías"
    )
    public String getCategorias() {
        try {
            List<CategoriaResponse> categorias = categoriaService.getCategorias();
            if (categorias == null || categorias.isEmpty()) {
                return "No hay categorías registradas.";
            }
            StringBuilder result = new StringBuilder("Lista de categorías:\n\n");
            for (CategoriaResponse c : categorias) {
                result.append(String.format("ID: %d - Nombre: %s\n",
                        c.getId(),
                        c.getNombre() != null ? c.getNombre() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener categorías: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_categorias_activas",
            description = "Obtiene la lista de categorías activas"
    )
    public String getCategoriasActivas() {
        try {
            List<CategoriaResponse> categorias = categoriaService.getCategoriasActivas();
            if (categorias == null || categorias.isEmpty()) {
                return "No hay categorías activas.";
            }
            StringBuilder result = new StringBuilder("Categorías activas:\n\n");
            for (CategoriaResponse c : categorias) {
                result.append(String.format("ID: %d - Nombre: %s\n",
                        c.getId(),
                        c.getNombre() != null ? c.getNombre() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener categorías activas: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_categorias_producto",
            description = "Obtiene la lista de categorías con productos"
    )
    public String getCategoriasProducto() {
        try {
            List<CategoriaProductoResponse> categorias = categoriaService.getCategoriasProducto();
            if (categorias == null || categorias.isEmpty()) {
                return "No hay categorías con productos.";
            }
            StringBuilder result = new StringBuilder("Categorías con productos:\n\n");
            for (CategoriaProductoResponse cp : categorias) {
                result.append(String.format("ID: %d - Nombre: %s - Productos count: %d\n",
                        cp.getId(),
                        cp.getNombre() != null ? cp.getNombre() : "No especificado",
                        cp.getProductosCount()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener categorías producto: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_subcategorias_activas_por_categoria_id",
            description = "Obtiene las subcategorías activas por categoría ID"
    )
    public String getSubCategoriasActivasByCategoriaId(
            @ToolParam(description = "ID de la categoría") Long categoriaId) {
        try {
            List<SubCategoriaProductoResponse> subcategorias = categoriaService.getSubCategoriasActivasByCategoriaId(categoriaId);
            if (subcategorias == null || subcategorias.isEmpty()) {
                return "No hay subcategorías activas para la categoría ID: " + categoriaId;
            }
            StringBuilder result = new StringBuilder(String.format("Subcategorías activas para la categoría ID %d:\n\n", categoriaId));
            for (SubCategoriaProductoResponse sc : subcategorias) {
                result.append(String.format("ID: %d - Nombre: %s\n",
                        sc.getId(),
                        sc.getNombre() != null ? sc.getNombre() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener subcategorías activas por categoría ID: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_categoria",
            description = "Crea una nueva categoría"
    )
    public String createCategoria(
            @ToolParam(description = "Solicitud de creación de categoría") CategoriaCreateRequest categoriaRequest) {
        try {
            BaseResponse response = categoriaService.createCategoria(categoriaRequest);
            if (response.isSuccess()) {
                return "✅ Categoría creada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al crear categoría: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al crear categoría: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_categoria",
            description = "Actualiza una categoría existente"
    )
    public String updateCategoria(
            @ToolParam(description = "Solicitud de actualización de categoría") CategoriaCreateRequest categoriaRequest,
            @ToolParam(description = "ID de la categoría a actualizar") Long id) {
        try {
            BaseResponse response = categoriaService.updateCategoria(categoriaRequest, id);
            if (response.isSuccess()) {
                return "✅ Categoría con ID " + id + " actualizada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar categoría: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar categoría con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "cambiar_estado_categoria",
            description = "Cambia el estado de una categoría"
    )
    public String changeEstadoCategoria(
            @ToolParam(description = "ID de la categoría") Long id) {
        try {
            BaseResponse response = categoriaService.changeEstadoCategoria(id);
            if (response.isSuccess()) {
                return "✅ Estado de la categoría con ID " + id + " cambiado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al cambiar estado de la categoría: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al cambiar estado de la categoría con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "eliminar_categoria",
            description = "Elimina una categoría usando su ID"
    )
    public String deleteCategoria(
            @ToolParam(description = "ID de la categoría a eliminar") Long id) {
        try {
            BaseResponse response = categoriaService.deleteCategoria(id);
            if (response.isSuccess()) {
                return "✅ Categoría con ID " + id + " eliminada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al eliminar categoría: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al eliminar categoría con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "convertir_categoria_a_response",
            description = "Convierte una entidad Categoría a su DTO de respuesta"
    )
    public String toResponse(
            @ToolParam(description = "Entidad Categoría") Categoria categoria) {
        try {
            CategoriaResponse response = categoriaService.toResponse(categoria);
            return """
                    Categoría Response:
                    ID: %d
                    Nombre: %s
                    Descripción: %s
                    Estado: %s
                    """.formatted(
                    response.getId(),
                    response.getNombre() != null ? response.getNombre() : "No especificado",
                    response.getDescripcion() != null ? response.getDescripcion() : "No especificado",
                    response.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Error al convertir categoría a response: " + e.getMessage();
        }
    }

     */
}