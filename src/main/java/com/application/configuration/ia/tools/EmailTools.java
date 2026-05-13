package com.application.configuration.ia.tools;

import com.application.persistence.entity.compra.Compra;
import com.application.persistence.entity.compra.enums.EEstado;
import com.application.persistence.entity.usuario.Usuario;
import com.application.service.interfaces.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailTools {

    private final EmailService emailService;

    @Tool(
            name = "enviar_email",
            description = "Envía un email con plantilla y variables"
    )
    public void sendEmail(
            @ToolParam(description = "Dirección de correo del destinatario") String to,
            @ToolParam(description = "Asunto del email") String subject,
            @ToolParam(description = "Nombre de la plantilla") String plantilla,
            @ToolParam(description = "Variables para la plantilla") Map<String, Object> variables) {
        emailService.sendEmail(to, subject, plantilla, variables);
    }

    @Tool(
            name = "enviar_email_bienvenida",
            description = "Envía un email de bienvenida a un usuario"
    )
    public void sendWelcomeEmail(
            @ToolParam(description = "Usuario al que se enviará el email") Usuario usuario) {
        emailService.sendWelcomeEmail(usuario);
    }

    @Tool(
            name = "enviar_email_login_exitoso",
            description = "Envía un email de notificación de login exitoso"
    )
    public void sendEmailLoginSuccessful(
            @ToolParam(description = "Usuario que inició sesión") Usuario usuario,
            @ToolParam(description = "Solicitud HTTP") HttpServletRequest request) {
        emailService.sendEmailLoginSuccessful(usuario, request);
    }

    @Tool(
            name = "enviar_email_estado_pago",
            description = "Envía un email de notificación de estado de pago"
    )
    public void sendEmailEstadoPago(
            @ToolParam(description = "Usuario asociado a la compra") Usuario usuario,
            @ToolParam(description = "Compra relacionada") Compra compra,
            @ToolParam(description = "Estado de la compra") EEstado estado) {
        emailService.sendEmailEstadoPago(usuario, compra, estado);
    }

    @Tool(
            name = "obtener_info_dispositivo",
            description = "Obtiene información del dispositivo desde la solicitud HTTP"
    )
    public String getDeviceInfo(
            @ToolParam(description = "Solicitud HTTP") HttpServletRequest request) {
        return emailService.getDeviceInfo(request);
    }

    @Tool(
            name = "obtener_titulo_estado",
            description = "Obtiene el título correspondiente al estado de pago"
    )
    public String getTituloEstado(
            @ToolParam(description = "Estado de la compra") EEstado estado) {
        return emailService.getTituloEstado(estado);
    }

    @Tool(
            name = "obtener_mensaje_estado",
            description = "Obtiene el mensaje correspondiente al estado de pago"
    )
    public String getMensajeEstado(
            @ToolParam(description = "Estado de la compra") EEstado estado) {
        return emailService.getMensajeEstado(estado);
    }

    @Tool(
            name = "obtener_icono_estado",
            description = "Obtiene el icono correspondiente al estado de pago"
    )
    public String getIconoEstado(
            @ToolParam(description = "Estado de la compra") EEstado estado) {
        return emailService.getIconoEstado(estado);
    }
}