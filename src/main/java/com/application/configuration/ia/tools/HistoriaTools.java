package com.application.configuration.ia.tools;

import com.application.service.interfaces.historia.HistoriaService;
import com.application.persistence.entity.historia.Historia;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.historia.request.HistoriaCreateRequest;
import com.application.presentation.dto.historia.response.HistoriaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoriaTools {

    private final HistoriaService historiaService;

    /*

    @Tool(
            name = "obtener_historia_por_id",
            description = "Obtiene una historia usando su ID"
    )
    public String getHistoriaById(
            @ToolParam(description = "ID de la historia a buscar") Long id) {
        try {
            Historia h = historiaService.getHistoriaById(id);
            return """
                    Historia encontrada:
                    ID: %d
                    Título: %s
                    Descripción: %s
                    Imagen URL: %s
                    Estado: %s
                    """.formatted(
                    h.getId(),
                    h.getTitulo() != null ? h.getTitulo() : "No especificado",
                    h.getDescripcion() != null ? h.getDescripcion() : "No especificado",
                    h.getImagenUrl() != null ? h.getImagenUrl() : "No especificado",
                    h.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Historia no encontrada con ID: " + id;
        }
    }

    @Tool(
            name = "obtener_historia_response_por_id",
            description = "Obtiene una historia response usando su ID"
    )
    public String getHistoriaResponseById(
            @ToolParam(description = "ID de la historia a buscar") Long id) {
        try {
            HistoriaResponse hr = historiaService.getHistoriaResponseById(id);
            return """
                    Historia Response encontrada:
                    ID: %d
                    Título: %s
                    Descripción: %s
                    Imagen URL: %s
                    Estado: %s
                    """.formatted(
                    hr.getId(),
                    hr.getTitulo() != null ? hr.getTitulo() : "No especificado",
                    hr.getDescripcion() != null ? hr.getDescripcion() : "No especificado",
                    hr.getImagenUrl() != null ? hr.getImagenUrl() : "No especificado",
                    hr.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Historia Response no encontrada con ID: " + id;
        }
    }

    @Tool(
            name = "obtener_historias",
            description = "Obtiene la lista de todas las historias"
    )
    public String getHistorias() {
        try {
            List<HistoriaResponse> historias = historiaService.getHistorias();
            if (historias == null || historias.isEmpty()) {
                return "No hay historias registradas.";
            }
            StringBuilder result = new StringBuilder("Lista de historias:\n\n");
            for (HistoriaResponse h : historias) {
                result.append(String.format("ID: %d - Título: %s\n",
                        h.getId(),
                        h.getTitulo() != null ? h.getTitulo() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener historias: " + e.getMessage();
        }
    }

    @Tool(
            name = "obtener_historias_activas",
            description = "Obtiene la lista de historias activas"
    )
    public String getHistoriasActivas() {
        try {
            List<HistoriaResponse> historias = historiaService.getHistoriasActivas();
            if (historias == null || historias.isEmpty()) {
                return "No hay historias activas.";
            }
            StringBuilder result = new StringBuilder("Historias activas:\n\n");
            for (HistoriaResponse h : historias) {
                result.append(String.format("ID: %d - Título: %s\n",
                        h.getId(),
                        h.getTitulo() != null ? h.getTitulo() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener historias activas: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_historia",
            description = "Crea una nueva historia"
    )
    public String createHistoria(
            @ToolParam(description = "Solicitud de creación de historia") HistoriaCreateRequest historiaRequest) {
        try {
            BaseResponse response = historiaService.createHistoria(historiaRequest);
            if (response.isSuccess()) {
                return "✅ Historia creada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al crear historia: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al crear historia: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_historia",
            description = "Actualiza una historia existente"
    )
    public String updateHistoria(
            @ToolParam(description = "Solicitud de actualización de historia") HistoriaCreateRequest historiaRequest,
            @ToolParam(description = "ID de la historia a actualizar") Long id) {
        try {
            BaseResponse response = historiaService.updateHistoria(historiaRequest, id);
            if (response.isSuccess()) {
                return "✅ Historia con ID " + id + " actualizada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar historia: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar historia con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "cambiar_estado_historia",
            description = "Cambia el estado de una historia"
    )
    public String changeEstadoHistoria(
            @ToolParam(description = "ID de la historia") Long id) {
        try {
            BaseResponse response = historiaService.changeEstadoHistoria(id);
            if (response.isSuccess()) {
                return "✅ Estado de la historia con ID " + id + " cambiado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al cambiar estado de la historia: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al cambiar estado de la historia con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "eliminar_historia",
            description = "Elimina una historia usando su ID"
    )
    public String deleteHistoria(
            @ToolParam(description = "ID de la historia a eliminar") Long id) {
        try {
            BaseResponse response = historiaService.deleteHistoria(id);
            if (response.isSuccess()) {
                return "✅ Historia con ID " + id + " eliminada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al eliminar historia: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al eliminar historia con ID " + id + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "convertir_historia_a_response",
            description = "Convierte una entidad Historia a su DTO de respuesta"
    )
    public String toResponse(
            @ToolParam(description = "Entidad Historia") Historia historia) {
        try {
            HistoriaResponse response = historiaService.toResponse(historia);
            return """
                    Historia Response:
                    ID: %d
                    Título: %s
                    Descripción: %s
                    Imagen URL: %s
                    Estado: %s
                    """.formatted(
                    response.getId(),
                    response.getTitulo() != null ? response.getTitulo() : "No especificado",
                    response.getDescripcion() != null ? response.getDescripcion() : "No especificado",
                    response.getImagenUrl() != null ? response.getImagenUrl() : "No especificado",
                    response.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Error al convertir historia a response: " + e.getMessage();
        }
    }

     */
}