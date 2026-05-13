package com.application.configuration.ia.tools;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.service.interfaces.PeticionService;
import com.application.persistence.entity.pqrs.enums.EEstadoPeticion;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.peticion.request.PeticionCreateRequest;
import com.application.presentation.dto.peticion.response.PeticionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PeticionTools {

    private final PeticionService peticionService;

    /*

    @Tool(
            name = "obtener_peticiones_activas",
            description = "Obtiene la lista de peticiones activas"
    )
    public String getPeticionesActivas() {
        try {
            List<PeticionResponse> peticiones = peticionService.getPeticionesActivas();
            if (peticiones == null || peticiones.isEmpty()) {
                return "No hay peticiones activas.";
            }
            StringBuilder result = new StringBuilder("Peticiones activas:\n\n");
            for (PeticionResponse p : peticiones) {
                result.append(String.format("ID: %d - Título: %s - Descripción: %s - Estado: %s\n",
                        p.getId(),
                        p.getTitulo() != null ? p.getTitulo() : "No especificado",
                        p.getDescripcion() != null ? p.getDescripcion() : "No especificado",
                        p.getEstado() != null ? p.getEstado().toString() : "No especificado"));
            }
            return result.toString();
        } catch (Exception e) {
            return "Error al obtener peticiones activas: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_peticion",
            description = "Crea una nueva petición"
    )
    public String createPeticion(
            @ToolParam(description = "Usuario principal autenticado") CustomUserPrincipal principal,
            @ToolParam(description = "Solicitud de creación de petición") PeticionCreateRequest peticionRequest) {
        try {
            BaseResponse response = peticionService.createPeticion(principal, peticionRequest);
            if (response.isSuccess()) {
                return "✅ Petición creada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al crear petición: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al crear petición: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_peticion",
            description = "Actualiza una petición existente"
    )
    public String updatePeticion(
            @ToolParam(description = "ID de la petición") Long peticionId,
            @ToolParam(description = "Usuario principal autenticado") CustomUserPrincipal principal,
            @ToolParam(description = "Solicitud de actualización de petición") PeticionCreateRequest peticionRequest) {
        try {
            BaseResponse response = peticionService.updatePeticion(peticionId, principal, peticionRequest);
            if (response.isSuccess()) {
                return "✅ Petición con ID " + peticionId + " actualizada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar petición: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar petición con ID " + peticionId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "cambiar_estado_peticion",
            description = "Cambia el estado de una petición"
    )
    public String changeEstadoPeticion(
            @ToolParam(description = "ID de la petición") Long peticionId,
            @ToolParam(description = "Nuevo estado") String estadoPeticion) {
        try {
            BaseResponse response = peticionService.changeEstadoPeticion(peticionId, estadoPeticion);
            if (response.isSuccess()) {
                return "✅ Estado de la petición con ID " + peticionId + " cambiado a " + estadoPeticion + " exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al cambiar estado de la petición: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al cambiar estado de la petición con ID " + peticionId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "eliminar_peticion",
            description = "Elimina una petición usando su ID"
    )
    public String deletePeticion(
            @ToolParam(description = "ID de la petición a eliminar") Long peticionId) {
        try {
            BaseResponse response = peticionService.deletePeticion(peticionId);
            if (response.isSuccess()) {
                return "✅ Petición con ID " + peticionId + " eliminada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al eliminar petición: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al eliminar petición con ID " + peticionId + ": " + e.getMessage();
        }
    }

     */
}