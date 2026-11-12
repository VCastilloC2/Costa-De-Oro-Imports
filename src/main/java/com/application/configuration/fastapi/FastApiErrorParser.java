package com.application.configuration.fastapi;

import org.jspecify.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrae el mensaje para el usuario de un error de FastAPI, sin depender de la versión de Jackson.
 *
 * <p>Formatos que devuelve FastAPI:</p>
 * <ul>
 *   <li>{@code {"detail": {"mensaje": "...", "success": false}}} — errores de negocio (409, 404...)</li>
 *   <li>{@code {"detail": "..."}} — errores de autenticación/autorización</li>
 *   <li>{@code {"detail": [{"loc": [...], "msg": "..."}]}} — validación (422)</li>
 * </ul>
 */
final class FastApiErrorParser {

    private static final String JSON_STRING = "\"((?:[^\"\\\\]|\\\\.)*)\"";
    private static final Pattern MENSAJE = Pattern.compile("\"mensaje\"\\s*:\\s*" + JSON_STRING);
    private static final Pattern DETAIL = Pattern.compile("\"detail\"\\s*:\\s*" + JSON_STRING);
    private static final Pattern MSG = Pattern.compile("\"msg\"\\s*:\\s*" + JSON_STRING);
    private static final Pattern CAMPO = Pattern.compile("\"loc\"\\s*:\\s*\\[[^\\]]*?\"([A-Za-z_]+)\"\\s*\\]");

    private FastApiErrorParser() {
    }

    static @Nullable String mensaje(@Nullable String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        Matcher m = MENSAJE.matcher(body);
        if (m.find()) {
            return unescape(m.group(1));
        }
        m = DETAIL.matcher(body);
        if (m.find()) {
            return unescape(m.group(1));
        }
        m = MSG.matcher(body);
        if (m.find()) {
            Matcher campo = CAMPO.matcher(body);
            String prefijo = campo.find() ? "Dato inválido en '" + campo.group(1) + "': " : "Datos inválidos: ";
            return prefijo + unescape(m.group(1));
        }
        return null;
    }

    private static String unescape(String s) {
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\' || i + 1 >= s.length()) {
                out.append(c);
                continue;
            }
            char n = s.charAt(++i);
            switch (n) {
                case 'n' -> out.append('\n');
                case 't' -> out.append('\t');
                case 'r' -> out.append('\r');
                case 'u' -> {
                    if (i + 4 < s.length()) {
                        out.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
                        i += 4;
                    }
                }
                default -> out.append(n); // \" \\ \/
            }
        }
        return out.toString();
    }
}
