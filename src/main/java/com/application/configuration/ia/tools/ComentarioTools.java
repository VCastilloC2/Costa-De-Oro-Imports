package com.application.configuration.ia.tools;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.service.interfaces.comentario.ComentarioService;
import com.application.persistence.entity.comentario.Comentario;
import com.application.presentation.dto.comentario.request.ComentarioCreateRequest;
import com.application.presentation.dto.comentario.response.ComentarioResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ComentarioTools {

    private final ComentarioService comentarioService;

    /*

    @Tool(
            name = "obtener_comentario_por_id",
            description = "Obtiene un comentario usando su ID"
    )
    public String getComentarioById(
            @ToolParam(description = "ID del comentario a buscar") Long id) {
        try {
            Comentario c = comentarioService.getComentarioById(id);
            return """
                    Comentario encontrado:
                    ID: %d
                    Contenido: %s
                    Historia ID: %d
                    Usuario ID: %d
                    Fecha: %s
                    Estado: %s
                    """.formatted(
                    c.getId(),
                    c.getContenido() != null ? c.getContenido() : "No especificado",
                    c.getHistoriaId(),
                    c.getUsuarioId(),
                    c.getFechaCreacion() != null ? c.getFechaCreacion().toString() : "No especificado",
                    c.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Comentario no encontrado con ID: " + id;
        }
    }

    @Tool(
            name = "obtener_comentarios",
            description = "Obtiene la lista de todos los comentarios"
    )
    public String getComentarios() {
        try {
            List<ComentarioResponse> comentarios = comentarioService.getComentarios();
            if (comentarios == null || comentarios.isEmpty()) {
                return "No hay comentarios registrados.";
            }
            StringBuilder result = new StringBuilder("Lista de comentarios:\n\n");
            for (ComentarioResponse c : comentarios) {
                result.append(String.format("ID: %d - Contenido: %s - Historia ID: %d\n",
                        c.getId(),
                        c.getContenido() != null ? c.getContenido() : "No especificado",
                        c.getHistoriaId()));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener comentarios: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_comentarios_activos_por_historia_id",
            description = "Obtiene los comentarios activos por historia ID"
    )
    public String getComentariosActivosByHistoriaId(
            @ToolParam(description = "ID de la historia") Long historiaId) {
        try {
            List<ComentarioResponse> comentarios = comentarioService.getComentariosActivosByHistoriaId(historiaId);
            if (comentarios == null || comentarios.isEmpty()) {
                return "No hay comentarios activos para la historia ID: " + historiaId;
            }
            StringBuilder result = new StringBuilder(String.format("Comentarios activos para la historia ID %d:\n\n", historiaId));
            for (ComentarioResponse c : comentarios) {
                result.append(String.format("ID: %d - Contenido: %s\n",
                        c.getId(),
                        c.getContenido() != null ? c.getContenido() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener comentarios activos por historia ID: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_comentario",
            description = "Crea un nuevo comentario para una historia"
    )
    public String createComentario(
            @ToolParam(description = "ID de la historia") Long historiaId,
            @ToolParam(description = "Usuario principal autenticado") CustomUserPrincipal principal,
            @ToolParam(description = "Solicitud de creación de comentario") ComentarioCreateRequest comentarioRequest) {
        try {
            BaseResponse response = comentarioService.createComentario(historiaId, principal, comentarioRequest);
            if (response.isSuccess()) {
                return "✅ Comentario creado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al crear comentario: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al crear comentario: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_comentario",
            description = "Actualiza un comentario existente"
    )
    public String updateComentario(
            @ToolParam(description = "ID de la historia") Long historiaId,
            @ToolParam(description = "ID del comentario") Long comentarioId,
            @ToolParam(description = "Usuario principal autenticado") CustomUserPrincipal principal,
            @ToolParam(description = "Solicitud de actualización de comentario") ComentarioCreateRequest comentarioRequest) {
        try {
            BaseResponse response = comentarioService.updateComentario(historiaId, comentarioId, principal, comentarioRequest);
            if (response.isSuccess()) {
                return "✅ Comentario con ID " + comentarioId + " actualizado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar comentario: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar comentario con ID " + comentarioId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "cambiar_estado_comentario",
            description = "Cambia el estado de un comentario"
    )
    public String changeEstadoComentario(
            @ToolParam(description = "ID del comentario") Long id) {
        try {
            BaseResponse response = comentarioService.changeEstadoComentario(id);
            if (response.isSuccess()) {
                return "✅ Estado del comentario con ID " + id + " cambiado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al cambiar estado del comentario: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al cambiar estado del comentario con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "eliminar_comentario",
            description = "Elimina un comentario usando su ID"
    )
    public String deleteComentario(
            @ToolParam(description = "ID del comentario") Long comentarioId,
            @ToolParam(description = "Usuario principal autenticado") CustomUserPrincipal principal) {
        try {
            BaseResponse response = comentarioService.deleteComentario(comentarioId, principal);
            if (response.isSuccess()) {
                return "✅ Comentario con ID " + comentarioId + " eliminado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al eliminar comentario: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al eliminar comentario con ID " + comentarioId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "convertir_comentario_a_response",
            description = "Convierte una entidad Comentario a su DTO de respuesta"
    )
    public String toResponse(
            @ToolParam(description = "Entidad Comentario") Comentario comentario) {
        try {
            ComentarioResponse response = comentarioService.toResponse(comentario);
            return """
                    Comentario Response:
                    ID: %d
                    Contenido: %s
                    Historia ID: %d
                    Usuario ID: %d
                    Fecha: %s
                    Estado: %s
                    """.formatted(
                    response.getId(),
                    response.getContenido() != null ? response.getContenido() : "No especificado",
                    response.getHistoriaId(),
                    response.getUsuarioId(),
                    response.getFechaCreacion() != null ? response.getFechaCreacion().toString() : "No especificado",
                    response.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Error al convertir comentario a response: " + e.getMessage();
        }
    }

     */
}