package com.application.configuration.ia.tools;

import com.application.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailTools {

    private final EmailService emailService;

    @Tool(
            name = "enviar_correo_notificacion",
            description = """
                    Envía un correo electrónico de notificación a un usuario.
                    Usa esta herramienta cuando el administrador quiera notificar a un cliente o proveedor sobre un cambio en su pedido o cuenta.
                    """
    )
    public String enviarCorreoNotificacion(String correo, String asunto, String mensaje) {
        if (correo == null || !correo.contains("@")) {
            return "Correo electrónico inválido.";
        }

        try {
            // Asumiendo que emailService.sendEmail(to, subject, body) existe
            emailService.sendEmail(correo, asunto, mensaje);
            return "Correo enviado exitosamente a " + correo;
        } catch (Exception e) {
            return "Error al enviar el correo: " + e.getMessage();
        }
    }
}
