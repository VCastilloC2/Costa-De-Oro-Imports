# 🤖 Ollama + Spring AI Integration

Guía completa para la integración de **Ollama** (LLM local) con **Spring AI 2.0** en Costa de Oro Imports.

---

## 📋 Tabla de Contenidos

1. [¿Qué es Ollama?](#-qué-es-ollama)
2. [Arquitectura](#-arquitectura)
3. [Instalación de Ollama](#-instalación-de-ollama)
4. [Descargar Modelos](#-descargar-modelos)
5. [Configuración en Spring Boot](#-configuración-en-spring-boot)
6. [Implementación del ChatClient](#-implementación-del-chatclient)
7. [Servicio de IA (AIService)](#-servicio-de-ia-aiservice)
8. [Controlador REST](#-controlador-rest)
9. [Ejemplo Completo](#-ejemplo-completo)
10. [Tuning y Optimización](#-tuning-y-optimización)
11. [Troubleshooting](#-troubleshooting)

---

## 🤔 ¿Qué es Ollama?

**Ollama** es un framework que te permite ejecutar modelos de lenguaje grandes (LLM) localmente sin necesidad de APIs externas como OpenAI.

### Ventajas

| Ventaja | Descripción |
|---------|-------------|
| 🔒 **Privacidad** | Los datos nunca salen de tu servidor |
| 💰 **Costo** | Sin suscripción o pago por token |
| ⚡ **Velocidad** | Respuestas en milisegundos (según hardware) |
| 🎯 **Control** | Puedes ajustar parámetros del modelo |
| 🚀 **Flexibilidad** | Cambiar modelos sin cambiar código |

### Desventajas

| Desventaja | Descripción |
|------------|-------------|
| 💻 **Hardware** | Requiere GPU/CPU potente |
| 📦 **Tamaño** | Modelos ocupan 3-10+ GB |
| 🎓 **Calidad** | Modelos locales < OpenAI/Claude |
| ⏱️ **Velocidad** | Más lento que APIs cloud |

> [!NOTE]
> Para MVP/desarrollo: Ollama + tinyllama. Para producción con alta calidad: considera OpenAI o Azure OpenAI.

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────┐
│                    Cliente Web                           │
│                  (Thymeleaf + JS)                        │
└────────────────────────┬────────────────────────────────┘
                         │ HTTP POST /api/chat
                         ▼
┌─────────────────────────────────────────────────────────┐
│              Spring Boot Application                     │
├─────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────┐   │
│  │  REST Controller (AIController)                  │   │
│  │  - Recibe mensajes                              │   │
│  │  - Valida entrada                               │   │
│  │  - Retorna respuesta                            │   │
│  └──────────────────┬───────────────────────────────┘   │
│                     │                                   │
│  ┌──────────────────▼───────────────────────────────┐   │
│  │  AI Service (AIService)                          │   │
│  │  - Procesa mensajes                              │   │
│  │  - Gestiona contexto/memoria                     │   │
│  │  - Llama a Spring AI                             │   │
│  └──────────────────┬───────────────────────────────┘   │
│                     │                                   │
│  ┌──────────────────▼───────────────────────────────┐   │
│  │  ChatClient (Spring AI)                          │   │
│  │  - Interfaz con LLM                              │   │
│  │  - Maneja prompts + contexto                     │   │
│  │  - Streaming (si se habilita)                    │   │
│  └──────────────────┬───────────────────────────────┘   │
│                     │                                   │
└─────────────────────┼───────────────────────────────────┘
                      │ HTTP REST
                      ▼
        ┌─────────────────────────────┐
        │   Ollama Server             │
        │  :11434                     │
        └─────────────────────────────┘
                      │
                      ▼
        ┌─────────────────────────────┐
        │  Modelo LLM (gemma3, llama3) │
        │  (En VRAM/RAM)              │
        └─────────────────────────────┘
```

---

## 📥 Instalación de Ollama

### Requisitos Previos

| Sistema | Requerimiento | Recomendado |
|---------|:-------------:|:-----------:|
| **RAM** | 4 GB mín. | 16+ GB |
| **Almacenamiento** | 30 GB libre | 100+ GB |
| **CPU** | Cualquiera | i7/Ryzen 7+ |
| **GPU** | Opcional | NVIDIA/AMD (10GB+ VRAM) |
| **OS** | macOS/Linux/Windows | Cualquiera |

> [!IMPORTANT]
> Sin GPU, un modelo mediano (7B params) tardará 5-30s por respuesta. Con GPU, 1-2s.

### Instalación por Sistema Operativo

#### macOS

```bash
# Opción 1: Descargar de ollama.com
# https://ollama.com/download/mac
# Instalar el .dmg como cualquier app

# Opción 2: Homebrew
brew install ollama

# Verificar instalación
ollama --version
```

#### Linux

```bash
# Instalar desde script oficial
curl -fsSL https://ollama.ai/install.sh | sh

# O manual: descargar desde https://ollama.com/download/linux

# Verificar
ollama --version

# Iniciar como servicio
sudo systemctl start ollama
sudo systemctl enable ollama  # Inicia al boot
```

#### Windows

```bash
# Descargar instalador desde
# https://ollama.com/download/windows

# Ejecutar .exe y seguir wizard

# Verificar en PowerShell
ollama --version

# Iniciar servicio (se ejecuta al boot)
# El servicio se inicia en background automáticamente
```

### Verificar Instalación

```bash
# Debe devolver versión
ollama --version

# Debe devolver lista de modelos (vacía al inicio)
ollama list

# Verificar que el servidor puede iniciarse
ollama serve
# Debe mostrar "Listening on 127.0.0.1:11434"
```

---

## 📦 Descargar Modelos

### Modelos Disponibles

| Modelo | Tamaño | Velocidad | Calidad | Memoria | Uso |
|--------|:------:|:---------:|:-------:|:-------:|-----|
| **tinyllama** | 1.1 GB | ⚡⚡⚡ Muy rápido | ⭐ Baja | 2 GB | Desarrollo rápido |
| **neural-chat** | 4.1 GB | ⚡⚡ Rápido | ⭐⭐⭐ Media | 6 GB | Balanceado |
| **gemma3:latest** | 5 GB | ⚡ Normal | ⭐⭐⭐⭐ Buena | 7 GB | Recomendado |
| **mistral** | 4.1 GB | ⚡⚡ Rápido | ⭐⭐⭐ Media | 8 GB | Rápido + bueno |
| **llama3** | 8 GB | ⚡ Normal | ⭐⭐⭐⭐⭐ Excelente | 10 GB | Producción |
| **neural-chat-v3-1** | 4.1 GB | ⚡⚡ Rápido | ⭐⭐⭐ Media | 6 GB | Chat balanceado |

### Descargar Modelos

```bash
# Descargar modelo por defecto (recomendado)
ollama pull gemma3:latest

# O alternativas
ollama pull llama3
ollama pull mistral
ollama pull tinyllama  # Muy rápido, ideal para desarrollo

# Verificar descarga
ollama list
```

> [!TIP]
> Primera descarga puede tardar 10-30 minutos. **Usa wifi rápida**. Una vez descargado, se cachea localmente.

### Eliminar Modelos

```bash
# Si necesitas espacio
ollama rm gemma3:latest

# O remover todos
ollama rm -a
```

---

## ⚙️ Configuración en Spring Boot

### 1. Dependencias Maven

```xml
<!-- En pom.xml -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 2. application.properties

```properties
# Servidor Ollama
spring.ai.ollama.base-url=${SPRING_AI_OLLAMA_BASE_URL:http://localhost:11434}

# Modelo a usar
spring.ai.ollama.chat.model=${SPRING_AI_OLLAMA_MODEL:gemma3:latest}

# Opciones del modelo
spring.ai.ollama.chat.options.temperature=0.7           # Creatividad (0-1)
spring.ai.ollama.chat.options.top-k=40                  # Diversidad tokens
spring.ai.ollama.chat.options.top-p=0.9                 # Nucleus sampling
spring.ai.ollama.chat.options.num-predict=150           # Máx tokens respuesta
spring.ai.ollama.chat.options.repeat-penalty=1.1        # Penalizar repetición
spring.ai.ollama.chat.options.repeat-last-n=64          # N tokens para repetición
spring.ai.ollama.chat.options.num-thread=4              # Threads CPU (si sin GPU)
```

### 3. application-dev.properties (desarrollo)

```properties
# Para desarrollo rápido
spring.ai.ollama.chat.model=tinyllama:latest
spring.ai.ollama.chat.options.temperature=0.5
spring.ai.ollama.chat.options.num-predict=100           # Respuestas más cortas = más rápido
```

### 4. application-prod.properties (producción)

```properties
# Para producción: mejor calidad
spring.ai.ollama.chat.model=llama3:latest
spring.ai.ollama.chat.options.temperature=0.3           # Menos aleatorio
spring.ai.ollama.chat.options.num-predict=300           # Respuestas completas
spring.ai.ollama.chat.options.num-thread=8
```

### 5. Variables de Entorno (.env)

```bash
# Obligatorias
SPRING_AI_OLLAMA_BASE_URL=http://localhost:11434
SPRING_AI_OLLAMA_MODEL=gemma3:latest

# Opcionalessi usas OpenAI en lugar de Ollama
SPRING_AI_OPENAI_API_KEY=sk-...
SPRING_AI_OPENAI_MODEL=gpt-4-turbo
```

> [!IMPORTANT]
> El servidor Ollama debe estar corriendo en la URL especificada. Sin él, la app falla al iniciar o en runtime.

---

## 💬 Implementación del ChatClient

### Clase: ChatConfig.java

```java
package com.costadeoro.app.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    private static final String SYSTEM_PROMPT = """
        Eres CostaBot, un asistente amable de Costa de Oro Imports, especializado en bebidas alcohólicas premium.
        
        Tu responsabilidad es:
        1. Responder preguntas sobre productos (cervezas, vinos, whiskies, licores)
        2. Proporcionar recomendaciones personalizadas
        3. Ayudar con información de envío y políticas
        4. Responder dudas sobre métodos de pago
        
        Restricciones:
        - SOLO responde sobre Costa de Oro Imports y sus productos
        - Si la pregunta es sobre otro tema, responde amablemente pero limitado:
          "Eso es interesante, pero estoy especializado en Costa de Oro Imports. ¿Hay algo sobre nuestros productos?"
        - Nunca inventes información que no conozcas
        - Mantén un tono profesional pero cálido
        - Usa Markdown para formatear respuestas (listas, negritas, etc.)
        
        Ejemplos de respuestas:
        - "Tenemos excelentes cervezas artesanales con envío rápido a todo el país"
        - "¿Buscas algo en particular? Recomiendo explorar nuestro catálogo"
        """;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }
}
```

### Parámetros del System Prompt

| Parámetro | Impacto | Ejemplo |
|-----------|--------|---------|
| **Rol** | Define qué es el bot | "Eres CostaBot, asistente..." |
| **Responsabilidades** | Qué debe hacer | "Responder preguntas sobre productos" |
| **Restricciones** | Qué NO debe hacer | "SOLO sobre Costa de Oro" |
| **Tono** | Cómo debe comunicarse | "Profesional pero cálido" |
| **Ejemplos** | Few-shot learning | Ejemplos de buenas respuestas |

> [!TIP]
> Cuanto más específico el prompt, mejor. Ollama respeta instrucciones claras. Prueba iterativamente y ajusta.

---

## 🔧 Servicio de IA (AIService)

### Clase: AIService.java

```java
package com.costadeoro.app.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AIService {

    @Autowired
    private ChatClient chatClient;

    /**
     * Procesa un mensaje y devuelve respuesta de la IA.
     * 
     * @param message Mensaje del usuario (se limpia automáticamente)
     * @param chatId Identificador de sesión de chat para mantener contexto
     * @return Respuesta del modelo LLM
     * @throws RuntimeException Si Ollama no está disponible o hay error
     */
    public String chat(String message, String chatId) {
        try {
            // 1. Limpieza de entrada
            String cleanedMessage = cleanMessage(message);
            
            // 2. Validación
            if (cleanedMessage.isEmpty()) {
                return "Por favor, escriba un mensaje válido.";
            }
            
            // 3. Llamada a Spring AI + Ollama
            String response = chatClient
                    .prompt()
                    .user(cleanedMessage)
                    .call()
                    .content();
            
            // 4. Validar respuesta
            if (response == null || response.isEmpty()) {
                log.warn("Respuesta vacía del modelo para: {}", cleanedMessage);
                return "Disculpa, no pude procesar tu pregunta. Intenta de nuevo.";
            }
            
            log.info("Chat response for session {}: {} chars", chatId, response.length());
            return response;
            
        } catch (WebClientResponseException.ServiceUnavailable e) {
            log.error("Ollama no está disponible en {}", chatClient.toString(), e);
            return "El servicio de IA no está disponible. Contacta a soporte.";
        } catch (Exception e) {
            log.error("Error procesando mensaje en IA", e);
            return "Ocurrió un error inesperado. Por favor intenta de nuevo.";
        }
    }

    /**
     * Limpia el mensaje de usuario:
     * - Trim (espacios inicio/fin)
     * - Reduce múltiples espacios a uno
     * - Valida longitud
     */
    private String cleanMessage(String message) {
        if (message == null) {
            return "";
        }
        
        // Trim
        String cleaned = message.strip();
        
        // Reducir espacios múltiples
        cleaned = cleaned.replaceAll("\\s+", " ");
        
        // Validar longitud (máx 1000 caracteres)
        if (cleaned.length() > 1000) {
            cleaned = cleaned.substring(0, 1000);
        }
        
        return cleaned;
    }

    /**
     * Versión con streaming (para respuestas progresivas)
     * Útil para responses largas
     */
    public String chatStream(String message, String chatId) {
        // Próxima implementación con Flux<String> y SSE
        return chat(message, chatId);
    }
}
```

### Manejo de Errores

```java
// Casos comunes a manejar:

// 1. Ollama no está corriendo
catch (WebClientResponseException.ServiceUnavailable e) {
    log.error("Ollama unavailable at configured URL");
    return "Servicio IA no disponible";
}

// 2. Modelo no existe
catch (WebClientResponseException.NotFound e) {
    log.error("Model not found in Ollama");
    return "Modelo no encontrado";
}

// 3. Timeout (respuesta lenta)
catch (TimeoutException e) {
    log.error("Ollama response timeout");
    return "La IA tardó demasiado. Intenta de nuevo.";
}
```

---

## 📡 Controlador REST

### Clase: AIController.java

```java
package com.costadeoro.app.controller;

import com.costadeoro.app.dto.ChatRequest;
import com.costadeoro.app.dto.ChatResponse;
import com.costadeoro.app.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * POST /api/chat
     * Envía un mensaje y recibe respuesta de la IA
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Chat request: {}", request.getMessage().substring(0, 50));
        
        // Usar email del usuario como chatId si está autenticado
        String chatId = request.getChatId() != null 
            ? request.getChatId() 
            : "guest-session";
        
        // Procesar
        String response = aiService.chat(request.getMessage(), chatId);
        
        // Responder
        return ResponseEntity.ok(new ChatResponse(response));
    }

    /**
     * GET /api/chat/health
     * Verificar si Ollama está disponible
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        try {
            String response = aiService.chat("Responde 'OK' brevemente", "health-check");
            if (response.isEmpty()) {
                return ResponseEntity.status(503).body("Ollama no responde");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(503).body("Error: " + e.getMessage());
        }
    }
}
```

### DTOs

```java
// ChatRequest.java
package com.costadeoro.app.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String chatId;  // Opcional, para mantener contexto
}

// ChatResponse.java
package com.costadeoro.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String response;
    private long timestamp = System.currentTimeMillis();
}
```

---

## 💻 Ejemplo Completo

### Flujo de Usuario

```
1. Usuario en web escribe: "¿Qué cervezas artesanales tienen?"
   ↓
2. Frontend envía POST /api/chat
   Body: { "message": "¿Qué cervezas artesanales tienen?", "chatId": "user123" }
   ↓
3. Controller recibe request
   ↓
4. AIService.chat() limpia mensaje y llama ChatClient
   ↓
5. ChatClient envía a Ollama: 
   System: "Eres CostaBot..."
   User: "¿Qué cervezas artesanales tienen?"
   ↓
6. Ollama procesa con modelo gemma3 (~2-5 segundos)
   ↓
7. Ollama devuelve respuesta:
   "Contamos con excelentes opciones de cervezas artesanales..."
   ↓
8. ChatClient retorna respuesta a AIService
   ↓
9. AIService valida y retorna
   ↓
10. Controller devuelve JSON:
    { "response": "Contamos con excelentes opciones..." }
   ↓
11. Frontend renderiza en Markdown y muestra al usuario
```

### Test Manual con cURL

```bash
# Verificar que Ollama esté disponible
curl http://localhost:11434/api/tags

# Probar chat
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hola, ¿qué son ustedes?",
    "chatId": "test-session"
  }'

# Respuesta esperada
{
  "response": "¡Hola! Soy CostaBot, tu asistente de Costa de Oro Imports. Estoy aquí para ayudarte con preguntas sobre nuestras bebidas alcohólicas premium...",
  "timestamp": 1720105600000
}

# Verificar health
curl http://localhost:8080/api/chat/health
```

---

## 🎛️ Tuning y Optimización

### Parámetros del Modelo

```properties
# Temperature (creatividad)
# 0.0 = Determinístico, siempre igual
# 0.7 = Balanceado (DEFAULT)
# 1.0 = Muy creativo, impredecible
spring.ai.ollama.chat.options.temperature=0.7

# Top-K (límita a K tokens más probables)
# Valores menores = más focalizadas, menos aleatorias
spring.ai.ollama.chat.options.top-k=40

# Top-P (nucleus sampling)
# 0.9 = Considera 90% de probabilidad acumulada
spring.ai.ollama.chat.options.top-p=0.9

# Num-Predict (máximo tokens de respuesta)
# Menos = respuestas cortas + rápido
spring.ai.ollama.chat.options.num-predict=150

# Repeat Penalty (evita repetición)
# > 1.0 penaliza más
spring.ai.ollama.chat.options.repeat-penalty=1.1

# Repeat Last N (N tokens para verificar repetición)
spring.ai.ollama.chat.options.repeat-last-n=64
```

### Perfiles de Optimización

#### Perfil: Velocidad (MVP rápido)

```properties
spring.ai.ollama.chat.model=tinyllama:latest
spring.ai.ollama.chat.options.temperature=0.3
spring.ai.ollama.chat.options.num-predict=80
spring.ai.ollama.chat.options.top-k=20
```

**Resultado:** 1-2 segundos por respuesta, calidad media.

#### Perfil: Balanceado (Recomendado)

```properties
spring.ai.ollama.chat.model=gemma3:latest
spring.ai.ollama.chat.options.temperature=0.7
spring.ai.ollama.chat.options.num-predict=150
spring.ai.ollama.chat.options.top-k=40
```

**Resultado:** 3-5 segundos, buena calidad.

#### Perfil: Calidad (Producción)

```properties
spring.ai.ollama.chat.model=llama3:latest
spring.ai.ollama.chat.options.temperature=0.5
spring.ai.ollama.chat.options.num-predict=300
spring.ai.ollama.chat.options.top-k=50
```

**Resultado:** 5-10 segundos, excelente calidad.

### Benchmarks (M1 Mac, 16GB RAM)

| Modelo | Tokens/seg | Latencia | Memoria |
|--------|:----------:|:--------:|:-------:|
| tinyllama | 40 | 2.5s | 4 GB |
| gemma3 | 25 | 4s | 7 GB |
| mistral | 30 | 3.5s | 8 GB |
| llama3 | 20 | 5s | 10 GB |

> [!NOTE]
> Con GPU (NVIDIA): 2-3x más rápido. Sin GPU: espera latencias de 3-10s.

---

## 🐛 Troubleshooting

### Error: `Connection refused: localhost:11434`

**Solución:**

```bash
# Terminal 1: Iniciar Ollama
ollama serve

# Terminal 2: Verificar conexión
curl http://localhost:11434/api/tags

# Si devuelve JSON, Ollama funciona
```

### Error: `Model not found: gemma3:latest`

**Solución:**

```bash
# Descargar el modelo
ollama pull gemma3:latest

# Listar modelos
ollama list

# Actualizar application.properties si cambias modelo
spring.ai.ollama.chat.model=llama3:latest
```

### Error: `Empty response from model`

**Solución:**

```bash
# 1. Reducir num-predict (modelo generó poco)
spring.ai.ollama.chat.options.num-predict=50

# 2. Reducir temperatura
spring.ai.ollama.chat.options.temperature=0.3

# 3. Verificar recursos
# ¿CPU/RAM saturados?
top  # macOS/Linux
Task Manager  # Windows

# 4. Reiniciar Ollama
killall ollama
ollama serve
```

### Error: `Timeout after 60s`

**Solución:**

```bash
# 1. Aumentar timeout en Spring
spring.ai.ollama.timeout=120000  # 2 minutos

# 2. Usar modelo más rápido
ollama pull tinyllama

# 3. Verificar hardware
# ¿Hay CPU disponible?
# ¿Hay RAM libre?
```

### Respuestas lentas en producción

**Solución:**

```bash
# 1. Usar GPU (NVIDIA/AMD)
# https://ollama.ai/blog/ollama-supports-ggml

# 2. Cantidad threads
spring.ai.ollama.chat.options.num-thread=8

# 3. Cache de modelos
# Ollama cachea automáticamente después del primer uso

# 4. Load balancing
# Varios servidores Ollama con round-robin
```

---

## 📚 Referencias

- [Ollama Documentación](https://github.com/ollama/ollama)
- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/)
- [Modelos disponibles](https://ollama.ai/library)
- [LLM Benchmarks](https://huggingface.co/spaces/HuggingFaceH4/open_llm_leaderboard)

---

**Última actualización:** Julio 2026  
**Versión Ollama:** 1.0  
**Spring AI:** 2.0.0
