package com.application.service.interfaces;

/**
 * Contrato común para todos los servicios LLM del Nivel 1.
 *
 * PROPÓSITO PEDAGÓGICO:
 * Esta interfaz hace explícito que los tres proveedores (Groq)
 * resuelven el mismo problema: dado un texto de entrada, retornar una respuesta.
 *
 * En el Nivel 2 veremos cómo Spring AI proporciona exactamente esta misma
 * abstracción (ChatClient) de forma automática, sin que tengamos que definirla
 * manualmente ni gestionar los WebClient por proveedor.
 */
public interface LlmService {
    /**
     * Envía una pregunta al LLM y retorna la respuesta como texto plano.
     *
     * @param pregunta texto de entrada del usuario
     * @return respuesta generada por el modelo
     */
    String chat(String pregunta);

    /**
     * Nombre del proveedor — útil para incluirlo en la respuesta
     * y que los estudiantes identifiquen qué servicio se invocó.
     */
    String getNombreProveedor();

    /**
     * Nombre del modelo que se está usando.
     */
    String getModelo();
}
