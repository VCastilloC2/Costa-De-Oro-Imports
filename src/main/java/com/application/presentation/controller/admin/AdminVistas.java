package com.application.presentation.controller.admin;

import com.application.presentation.dto.general.response.BaseResponse;
import com.application.service.interfaces.CloudinaryService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.temporal.Temporal;
import java.util.LinkedHashMap;
import java.util.Map;

/** Utilidades compartidas por los controladores de administración que delegan en FastAPI. */
final class AdminVistas {

    private AdminVistas() {
    }

    /** {@code redirect:<ruta>?mensaje=...&success=...} como hacían todos los controladores admin. */
    static String redirect(String ruta, BaseResponse response) {
        return "redirect:" + ruta + "?mensaje=" + UriUtils.encode(response.mensaje(), StandardCharsets.UTF_8)
                + "&success=" + response.success();
    }

    /** Sube la imagen a Cloudinary si se adjuntó y devuelve su public_id; {@code null} si no hay archivo. */
    static String subirImagen(CloudinaryService cloudinary, MultipartFile archivo, String tipo) {
        return archivo == null || archivo.isEmpty() ? null : cloudinary.subirImagen(archivo, tipo);
    }

    /**
     * Cuerpo JSON a partir de pares clave/valor (admite {@code null}, a diferencia de {@code Map.of}).
     * Fechas y enums se envían como texto ISO / nombre para no depender de la configuración de Jackson.
     */
    static Map<String, Object> cuerpo(Object... claveValor) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < claveValor.length; i += 2) {
            Object valor = claveValor[i + 1];
            if (valor instanceof Temporal || valor instanceof Enum<?>) {
                valor = valor.toString();
            }
            map.put((String) claveValor[i], valor);
        }
        return map;
    }

}
