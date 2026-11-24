package com.application.service.implementation;

import com.application.persistence.entity.compra.Compra;
import com.application.persistence.entity.compra.enums.EEstado;
import com.application.persistence.entity.usuario.Usuario;
import com.application.service.interfaces.CompraService;
import com.application.service.interfaces.EmailService;
import com.application.service.interfaces.MercadoPagoService;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MercadoPagoServiceImpl implements MercadoPagoService {

    private final CompraService compraService;
    private final EmailService emailService;

    /** URL pública (APP_BASE_URL o RENDER_EXTERNAL_URL). Para pruebas locales con ngrok: APP_BASE_URL=https://xxxx.ngrok-free.dev */
    @Value("${app.base-url}")
    private String appBaseUrl;

    /**
     * @param compraId
     * @param request
     * @return
     */
    @Override
    public Preference crearPreferenciaParaCompra(Long compraId, HttpServletRequest request) {
       try {
           // Obtener compra de la base de datos
           Compra compra = compraService.getCompraById(compraId);
           Usuario usuario = compra.getUsuario();

           // Creación de items desde los detalles de las compras
           List<PreferenceItemRequest> items = compra.getDetalleVentas().stream()
                   .map(detalleVenta -> PreferenceItemRequest.builder()
                           .id(detalleVenta.getProducto().getProductoId().toString())
                           .title(detalleVenta.getProducto().getNombre())
                           .description(detalleVenta.getProducto().getDescripcion())
                           .categoryId(detalleVenta.getProducto().getCategoria().getNombre())
                           .quantity(detalleVenta.getCantidad())
                           .currencyId("COP") // moneda Colombiana
                           .unitPrice(new BigDecimal(detalleVenta.getPrecioUnitario()))
                           .pictureUrl(this.getImagenUrl(detalleVenta.getProducto().getImagen()))
                           .build())
                   .toList();

           // Configuración del comprador
           PreferencePayerRequest payer = PreferencePayerRequest.builder()
                   .name(usuario.getNombres().split(" ")[0])
                   .surname(usuario.getApellidos().split(" ")[0])
                   .email(usuario.getCorreo())
                   .phone(PhoneRequest.builder()
                           .areaCode("57")
                           .number(usuario.getTelefono())
                           .build())
                   .identification(IdentificationRequest.builder()
                           .type(usuario.getTipoIdentificacion().toString())
                           .number(usuario.getNumeroIdentificacion())
                           .build())
                   .build();

           // Urls de retorno: siempre terminan en "/" para concatenar rutas relativas
           String baseUrl = appBaseUrl.endsWith("/") ? appBaseUrl : appBaseUrl + "/";
           PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                   .success(baseUrl + "carrito?status=success&compra=" + compraId)
                   .failure(baseUrl + "carrito?status=error&compra=" + compraId)
                   .pending(baseUrl + "carrito?status=pending&compra=" + compraId)
                   .build();

           // Creación de la preferencia
           PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                   .items(items)
                   .payer(payer)
                   .backUrls(backUrls)
                   .autoReturn("approved")
                   .notificationUrl(baseUrl + "api/mercado-pago/notificacion")
                   .externalReference("REF-" + compraId.toString())
                   .statementDescriptor("COSTA DE ORO IMPORTS")
                   .build();

           PreferenceClient client = new PreferenceClient();

           return client.create(preferenceRequest);
       } catch (MPException | MPApiException e) {
           throw new RuntimeException("error al crear la preferencia de pago: " + e.getMessage());
       }
    }

    /**
     * @param paymentId
     */
    @Override
    public void procesarPago(String paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.valueOf(paymentId));

            String externalReference = payment.getExternalReference();
            String status = payment.getStatus();

            Long compraId = Long.parseLong(externalReference.replace("REF-", ""));

            // Actualizar el estado de la compra de la base de datos según el pago
            switch (status) {
                case "approved" -> compraService.updateEstadoCompra(compraId, EEstado.PAGADO);
                case "rejected" -> compraService.updateEstadoCompra(compraId, EEstado.RECHAZADO);
                case "in_process" -> compraService.updateEstadoCompra(compraId, EEstado.PENDIENTE);
                case "cancelled" -> compraService.updateEstadoCompra(compraId, EEstado.CANCELADO);
            }

            Compra compra = compraService.getCompraById(compraId);
            Usuario usuario = compra.getUsuario();
            EEstado estado = compra.getEstado();

            emailService.sendEmailEstadoPago(usuario, compra, estado);
        } catch (MPException | MPApiException e) {
            throw new RuntimeException("error al procesar el pago" + e.getMessage());
        }
    }

    /**
     * @param imagen
     * @return
     */
    @Override
    public String getImagenUrl(String imagen) {
        if (imagen == null || imagen.trim().isEmpty()) {
            return "https://via.placeholder.com/150"; // imagen por defecto
        }
        if (imagen.startsWith("http")) {
            return imagen;
        }
        return "https://costa-de-oro-imports.com/uploads/" + imagen;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() +
                (request.getServerPort() != 80 && request.getServerPort() != 443
                ? ":" + request.getServerPort() : "");
    }
}
