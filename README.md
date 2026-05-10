# 🍻 Costa de Oro Imports

Aplicación web de e-commerce desarrollada con **Spring Boot + Thymeleaf** para la gestión y venta de bebidas alcohólicas como:

- 🍺 Cervezas
- 🍷 Vinos
- 🥃 Whiskys
- 🍹 Vodkas
- 🍸 Ginebras
- 🥃 Rones
- 🌵 Tequila y Mezcal

El proyecto incluye un sistema completo de ventas online con autenticación, panel administrativo y un chatbot inteligente con IA integrado usando **Spring AI**.

---

# ✨ Características Principales

## 🛒 E-commerce
- Catálogo dinámico de productos
- Carrito de compras
- Gestión de inventario
- Panel administrativo
- Sistema de autenticación
- Gestión de usuarios

## 🤖 Chatbot Inteligente
Asistente virtual llamado **CostaBot** integrado con IA.

Características:
- Respuestas en tiempo real (streaming)
- Renderizado Markdown
- Indicador "Escribiendo..."
- Restricción temática inteligente
- Respuestas comerciales naturales
- Integración con Ollama/OpenAI
- UI moderna estilo ChatGPT

---

# 🚀 Tecnologías Utilizadas

## 🔧 Backend
- Java 21
- Spring Boot
- Spring Security
- Spring AI
- Spring WebFlux
- Maven

## 🎨 Frontend
- Thymeleaf
- HTML5
- CSS3
- JavaScript
- Marked.js
- Remix Icons

## 🗄️ Base de Datos
- MySQL

## 🐳 DevOps
- Docker
- Docker Compose

---

# 📂 Funcionalidades Implementadas

- ✅ Registro de usuarios
- ✅ Inicio de sesión
- ✅ Seguridad con Spring Security
- ✅ Catálogo de productos
- ✅ Carrito de compras
- ✅ Gestión administrativa
- ✅ Chatbot con IA
- ✅ Streaming en tiempo real con SSE
- ✅ Renderizado Markdown
- ✅ Diseño responsive
- ✅ Auto-scroll inteligente
- ✅ Indicador de escritura
- ✅ Auto-resize del textarea

---

# 📦 Estructura del Proyecto

```plaintext
CostaDeOroImports/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── configuration/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   └── security/
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       └── application.properties
│   │
│   └── test/
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

# ⚙️ Configuración del Proyecto

## 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/tuusuario/CostaDeOroImports.git
cd CostaDeOroImports
```

---

## 2️⃣ Configurar la base de datos

Crear una base de datos MySQL:

```sql
CREATE DATABASE costa_de_oro;
```

---

## 3️⃣ Configurar `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/costa_de_oro
spring.datasource.username=root
spring.datasource.password=tu_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

# 🤖 Configuración IA

## Ollama

```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.model=llama3
```

## Opciones del modelo

```properties
spring.ai.ollama.chat.options.temperature=0.7
spring.ai.ollama.chat.options.top-k=20
spring.ai.ollama.chat.options.top-p=0.8
spring.ai.ollama.chat.options.num-predict=120
```

---

# ▶️ Ejecutar el Proyecto

## Maven

```bash
mvn spring-boot:run
```

---

## Docker

```bash
docker-compose up --build
```

---

# 🧠 Arquitectura del Chatbot IA

El chatbot utiliza:

- Spring AI
- ChatClient
- Flux<String>
- SSE (Server-Sent Events)
- Streaming en tiempo real
- Prompt Engineering

Ejemplo:

```java
return chatClient
        .prompt()
        .user(mensaje)
        .stream()
        .content();
```

---

# 🎨 Características UI/UX

- Diseño moderno
- Responsive Design
- Chat flotante
- Animaciones suaves
- Markdown dinámico
- Scroll automático
- Indicador online/offline
- Efectos visuales modernos

---

# 🔐 Seguridad

Implementado con:

- Spring Security
- Autenticación personalizada
- Protección de rutas
- Gestión de sesiones

---

# 📸 Próximas Mejoras

- ❤️ Lista de favoritos
- 📱 Aplicación PWA
- 🌎 Multiidioma
- 📊 Dashboard avanzado
- 🧾 Facturación
- 📦 Tracking de pedidos
- 💬 Historial persistente del chat

---

# 🤝 Contribuciones

Las contribuciones son bienvenidas.

1. Haz un Fork
2. Crea una rama:

```bash
git checkout -b feature/nueva-funcionalidad
```

3. Commit:

```bash
git commit -m "Agrega nueva funcionalidad"
```

4. Push:

```bash
git push origin feature/nueva-funcionalidad
```

5. Abre un Pull Request 🚀

---

# 📄 Licencia

Este proyecto está bajo la licencia MIT.

---

Proyecto creado para modernizar la experiencia de e-commerce de **Costa de Oro Imports** con inteligencia artificial integrada.