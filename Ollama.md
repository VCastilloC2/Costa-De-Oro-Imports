# Integración de Ollama con Spring AI

Este proyecto implementa un asistente virtual utilizando Spring Boot y Spring AI, ejecutando modelos de lenguaje de forma local mediante Ollama.

---

## 1. Requisitos

- Java 17 o superior
- Maven o Gradle
- Ollama instalado en el sistema

---

## 2. Instalación de Ollama

Descargar Ollama desde el sitio oficial:
https://ollama.com/download

Seguir el proceso de instalación según el sistema operativo.

---

## 3. Iniciar el servicio de Ollama

Ejecutar el siguiente comando en la terminal:

```bash
ollama serve
```

Esto iniciará el servidor en:
http://localhost:11434

Es obligatorio que este servicio esté activo antes de ejecutar la aplicación Spring Boot.

## 4. Modelos necesarios

El proyecto utiliza por defecto el siguiente modelo:  
`spring.ai.ollama.chat.options.model=gemma3:latest`

**Descargar el modelo requerido:**

```bash
ollama pull gemma3:latest
```

Verificar modelos instalados:
```bash
ollama list
```

Modelos alternativos (opcional):
```bash
ollama pull llama3
ollama pull mistral
```

## 5. Configuración en Spring Boot
Archivo: application.properties

```properties
spring.ai.model.chat=ollama
spring.ai.ollama.base-url=${OPENAI_BASE_URL:"http://localhost:11434/"}
spring.ai.ollama.chat.options.model=${OPENAI_MODEL:"gemma3:latest"}
```

- **spring.ai.model.chat**: define que se usará Ollama como proveedor.
- **spring.ai.ollama.base-url**: URL del servidor de Ollama.
- **spring.ai.ollama.chat.options.model**: modelo a utilizar.

## 6. Configuración del cliente de chat

**Clase:** `ChatConfig.java`

Se configura el cliente de Spring AI y se define el comportamiento del asistente mediante un prompt del sistema.

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder) {
    return builder
            .defaultSystem(SYSTEM_PROMPT)
            .defaultAdvisors(new SimpleLoggerAdvisor())
            .build();
}
```
Función del SYSTEM_PROMPT:
- Define el rol del asistente.
- Restringe las respuestas a temas específicos.
- Controla el estilo de comunicación.
- Evita respuestas fuera del dominio definido.

---

## 7. Servicio de procesamiento de mensajes

**Clase:** `AIHttp.java`  
**Método principal:** `public String preguntar(String mensaje, String chatId)`

**Responsabilidades:**

* **Limpieza del mensaje de entrada:** Elimina espacios al inicio y final (`strip`) y reduce múltiples espacios a uno (`replaceAll`).
* **Contexto:** Mantiene el contexto de la conversación usando `chatId`.
* **Memoria:** Utiliza `PromptChatMemoryAdvisor` para conservar el historial.
* **Robustez:** Maneja errores y respuestas vacías.

---

## 8. Controlador REST

**Clase:** `AIController.java`  
**Endpoint:** `POST /api/chat`

**Request Body (JSON):**

```json
{
  "message": "Texto del usuario",
  "chatId": "identificador-unico"
}
```

**Response (JSON):**
```json
{
  "response": "Respuesta generada por la IA"
}
```

## 9. Flujo de funcionamiento

1.  El cliente envía un mensaje al endpoint `/api/chat`.
2.  El controlador recibe el mensaje y el `chatId`.
3.  El servicio procesa la entrada (limpieza y preparación).
4.  Spring AI envía la solicitud enriquecida a Ollama.
5.  Ollama genera la respuesta usando el modelo configurado.
6.  La respuesta se devuelve al cliente.

---

## 10. Problemas comunes

* **Error de conexión a Ollama:** Si aparece `Connection refused`, asegúrate de haber ejecutado el comando `ollama serve`.
* **Modelo no encontrado:** Verifica los modelos disponibles con `ollama list`. Si el modelo configurado no aparece, descárgalo con `ollama pull gemma3:latest`.
* **Respuestas vacías:** Valida que el servicio de Ollama esté activo, que el modelo esté correctamente descargado y que los recursos del sistema no estén saturados.

---

## 11. Consideraciones

* El sistema depende completamente de que Ollama esté en ejecución.
* El rendimiento (latencia) depende del modelo seleccionado y de los recursos de hardware (CPU/GPU) del sistema local.
* El uso de `chatId` es fundamental para mantener la coherencia y el contexto entre múltiples mensajes.
* El prompt del sistema es el encargado de controlar estrictamente el comportamiento y las limitaciones del asistente.

---

## 12. Ejecución del proyecto

1.  **Iniciar Ollama:** Ejecutar `ollama serve` en una terminal.
2.  **Verificar modelo:** Confirmar la instalación con `ollama list`.
3.  **Ejecutar Spring Boot:** Iniciar la aplicación desde tu IDE favorito o mediante la terminal.
4.  **Prueba:** Consumir el endpoint `/api/chat` utilizando herramientas como Postman, Insomnia o cURL.