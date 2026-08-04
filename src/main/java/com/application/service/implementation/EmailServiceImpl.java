package com.application.service.implementation;

import com.application.persistence.entity.compra.Compra;
import com.application.persistence.entity.compra.enums.EEstado;
import com.application.persistence.entity.usuario.Usuario;
import com.application.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * Envía un correo electrónico con contenido HTML generado desde una plantilla Thymeleaf.
     *
     * @param to correo del destinatario principal
     * @param subject Asusto del correo
     * @param plantilla Nombre del archivo HTML (sin extensión) dentro de la carpeta "email/"
     * @param variables Variables dinámicas que se inyectan en la plantilla Thymeleaf
     */
    @Override
    public void sendEmail(String to,
                          String subject,
                          String plantilla,
                          Map<String, Object> variables) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(variables);

            String contenidoHtml =
                    templateEngine.process("email/" + plantilla, context);

            helper.setFrom(new InternetAddress(
                    from,"Costa De Oro Imports"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(contenidoHtml, true);

            System.out.println("FROM = " + from);
            System.out.println("TO = " + to);
            System.out.println("SUBJECT = " + subject);
            System.out.println("HTML SIZE = " + contenidoHtml.length());

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("ERROR: no se puedo enviar el email: " + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("ERROR: codificación no soportada: " + e.getMessage(), e);
        }
    }

    /**
     * Método para enviarle un correo al usuario cuando se registre en el sistema.
     * Se prepara el contexto con información personal y la URL de inicio de sesión.
     *
     * @param usuario Usuario al que se le enviara el correo
     */
    @Override
    public void sendWelcomeEmail(Usuario usuario) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("nombre", usuario.getNombres() + " " + usuario.getApellidos());
            variables.put("tipoUsuario", usuario.getRol().getName().getDescripcion());
            variables.put("urlLogin", "http://localhost:8080/");

            if (usuario.getEmpresa() != null) {
                variables.put("empresa", usuario.getEmpresa().getRazonSocial());
            }

            this.sendEmail(usuario.getCorreo(), "¡Bienvenido a Nuestro Sistema!", "email-bienvenida", variables);

        } catch (Exception e) {
            throw new RuntimeException("error al enviar el email de bienvenida" + e.getMessage(), e);
        }
    }

    /**
     * Método para enviarle un email al usuario cunado inicie sesión
     *
     * @param usuario Usuario que inicio sesión
     * @param request Petición HTTP para obtener información del dispositivo desde el User-Agent
     */
    @Override
    public void sendEmailLoginSuccessful(Usuario usuario, HttpServletRequest request) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("nombre", usuario.getNombres() + " " + usuario.getApellidos());
            variables.put("fechaLogin", LocalDateTime.now());
            variables.put("dispositivo", this.getDeviceInfo(request));

            this.sendEmail(usuario.getCorreo(), "Inicio de Sesión Exitoso - Seguridad", "email-login-exitoso", variables);
        } catch (Exception e) {
            throw new RuntimeException("error al enviar el email de inicio de sesión exitoso" + e.getMessage(), e);
        }
    }

    /**
     * Método para enviarle un email al usuario con el estado actualizado de su pago
     *
     * @param usuario Usuario que realizo la compra
     * @param compra Compra con los detalles del pedido
     * @param estado Estado del pedido
     */
    @Override
    public void sendEmailEstadoPago(Usuario usuario, Compra compra, EEstado estado) {
        try {
            Map<String, Object> variables = new HashMap<>();

            // Información del pedido y del usuario
            variables.put("nombreUsuario", usuario.getNombres() + " " + usuario.getApellidos());
            variables.put("numeroPedido", "ORD-" + String.format("%04d", compra.getCompraId()));
            variables.put("fechaPago", compra.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            variables.put("metodoPago", compra.getEMetodoPago().name());

            // Información del estado
            variables.put("estado", estado.name());
            variables.put("estadoTitulo", this.getTituloEstado(estado));
            variables.put("estadoMensaje", this.getMensajeEstado(estado));
            variables.put("estadoIcono", this.getIconoEstado(estado));

            // Detalles del producto
            List< Map<String, Object> > detalleCompra = compra.getDetalleVentas().stream()
                    .map(detalleVenta -> {

                        Map<String, Object> detalleMap = new HashMap<>();
                        detalleMap.put("productoNombre", detalleVenta.getProducto().getNombre());
                        detalleMap.put("cantidad", detalleVenta.getCantidad());
                        detalleMap.put("precioUnitario", detalleVenta.getPrecioUnitario());
                        detalleMap.put("subtotal", detalleVenta.getSubtotal());

                        return detalleMap;
                    })
                    .toList();

            variables.put("detalles", detalleCompra);
            variables.put("subtotal", compra.getSubtotal());
            variables.put("iva", compra.getIva());
            variables.put("total", compra.getTotal());

            // Url para reintentar el pago si fue rechazado
            if (estado.equals(EEstado.RECHAZADO)) {
                variables.put("urlReintentar", "http://localhost:8080/carrito");
            }

            this.sendEmail(
                    usuario.getCorreo(),
                    "Confirmación de tú pedido #" + compra.getCompraId() + " - " + this.getTituloEstado(estado),
                    "email-estado-pago",
                    variables
            );
        } catch (Exception e) {
            throw new RuntimeException("error al enviar el email con el estado de la compra" + e.getMessage(), e);
        }
    }

    /**
     * Extrae información básica del dispositivo desde los headers HTTP
     * usando el User-Agent para ofrecer contexto en las notificaciones de seguridad.
     *
     * @param request Petición actual que contiene el User-Agent
     * @return Texto descriptivo del dispositivo detectado
     */
    @Override
    public String getDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            if (userAgent.contains("Mobile")) {
                return "Dispositivo Móvil";
            } else if (userAgent.contains("Windows")) {
                return "Computadora Windows";
            } else if (userAgent.contains("Mac")) {
                return "Computadora Mac";
            } else if (userAgent.contains("Linux")) {
                return "Computadora Linux";
            }
        }
        return "Dispositivo Desconocido";
    }

    /**
     * Método auxiliar para el titulo del email
     *
     * @param estado Estado del pago
     * @return Un título para el email según el estado del pago
     */
    @Override
    public String getTituloEstado(EEstado estado) {
        return switch (estado) {
            case PAGADO -> "Pago Confirmado Exitosamente";
            case PENDIENTE -> "Pago en Proceso de Verificación";
            case RECHAZADO -> "Pago No Procesado";
            case CANCELADO -> "Pedido Cancelado";
        };
    }

    /**
     * Método auxiliar para el mensaje del email
     *
     * @param estado Estado del pago
     * @return Un mensaje para el email según el estado del pago
     */
    @Override
    public String getMensajeEstado(EEstado estado) {
        return switch (estado) {
            case PAGADO -> "Hemos recibido el pago de su pedido. Su orden esta siendo procesada y pronto recibirás actualizaciones sobre el envío.";
            case PENDIENTE -> "Tú pago esta en proceso de verificación. Te notificaremos tan pronto se complete la transacción.";
            case RECHAZADO -> "Lamentablemente, no pudimos procesar tú pago, te invitamos a revisar los métodos de pago e intentar nuevamente con otro método de pago si lo deseas.";
            case CANCELADO -> "Tú pedido ha sido cancelado. Si esto fue un error, puedes realizar una nueva compra en nuestro sistema";
        };
    }

    /**
     * Método auxiliar para el icono del email
     *
     * @param estado Estado del pago
     * @return Un mensaje para el email según el estado del pago
     */
    @Override
    public String getIconoEstado(EEstado estado) {
        return switch (estado) {
            case PAGADO -> "✅";
            case PENDIENTE -> "⏳";
            case RECHAZADO -> "❌";
            case CANCELADO -> "🚫";
        };
    }

}
