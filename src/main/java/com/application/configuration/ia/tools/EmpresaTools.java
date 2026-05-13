package com.application.configuration.ia.tools;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.service.interfaces.empresa.EmpresaService;
import com.application.presentation.dto.empresa.request.CreateEmpresaRequest;
import com.application.presentation.dto.empresa.request.SetEmpresaPhotoRequest;
import com.application.presentation.dto.empresa.request.UpdateEmpresaRequest;
import com.application.presentation.dto.general.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmpresaTools {

    private final EmpresaService empresaService;

    @Tool(
            name = "crear_empresa",
            description = "Crea una nueva empresa"
    )
    public String createEmpresa(
            @ToolParam(description = "Usuario principal autenticado") CustomUserPrincipal principal,
            @ToolParam(description = "Solicitud de creación de empresa") CreateEmpresaRequest empresaRequest) {
        try {
            GeneralResponse response = empresaService.createEmpresa(principal, empresaRequest);
            if (!response.mensaje().equals("Empresa registrada exitosamente")) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al crear empresa: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_empresa",
            description = "Actualiza una empresa existente"
    )
    public String updateEmpresa(
            @ToolParam(description = "Usuario principal autenticado") CustomUserPrincipal principal,
            @ToolParam(description = "Solicitud de actualización de empresa") UpdateEmpresaRequest empresaRequest) {
        try {
            GeneralResponse response = empresaService.updateEmpresa(principal, empresaRequest);
            if (response.mensaje().equals("Empresa actualizada exitosamente")) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al actualizar empresa: " + e.getMessage();
        }
    }

    @Tool(
            name = "establecer_foto_empresa",
            description = "Establece la foto de una empresa"
    )
    public String setEmpresaPhoto(
            @ToolParam(description = "Usuario principal autenticado") CustomUserPrincipal principal,
            @ToolParam(description = "Solicitud para establecer foto de empresa") SetEmpresaPhotoRequest empresaPhotoRequest) {
        try {
            GeneralResponse response = empresaService.setEmpresaPhoto(principal, empresaPhotoRequest);
            if (response.mensaje().equals("Imagen asignada exitosamente")) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al establecer foto de empresa: " + e.getMessage();
        }
    }
}