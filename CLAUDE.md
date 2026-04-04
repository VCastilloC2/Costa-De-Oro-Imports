# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

E-commerce Beer (Costa De Oro Imports) - A Spring Boot e-commerce application for beer sales. Built with Java 21, Spring Boot 3.5.x, Thymeleaf, MySQL, and integrates with external services: Cloudinary (image storage), Mercado Pago (payments), Google OAuth2, reCAPTCHA, and OpenAI-compatible AI services.

## Build & Run Commands

```bash
# Build project
./mvnw clean package -DskipTests

# Run tests
./mvnw test

# Run single test class
./mvnw test -Dtest=ECommerceBeerApplicationTests

# Run application (requires environment variables)
./mvnw spring-boot:run

# Docker build
docker build -t ecommerce-beer .
docker run -p 8080:8080 ecommerce-beer
```

## Required Environment Variables

The application requires these environment variables (see `application.properties`):

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` - MySQL database connection
- `REMEMBER_ME_KEY`, `REMEMBER_ME_TOKEN` - Session persistence
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` - OAuth2 authentication
- `MAIL_USERNAME`, `MAIL_PASSWORD` - SMTP email service
- `CLOUD_NAME`, `CLOUD_API_KEY`, `CLOUD_API_SECRET` - Cloudinary image storage
- `MERCADO_PAGO_ACCESS_TOKEN` - Payment processing
- `PASSWORD_URL_RECAPTCHA`, `PASSWORD_KEY_RECAPTCHA` - reCAPTCHA validation
- `OPENAI_BASE_URL`, `OPENAI_API_KEY`, `OPENAI_MODEL` - AI integration (supports Ollama/OpenAI)

## Architecture

### Package Structure
```
com.application/
├── configuration/          # Spring configuration beans
│   ├── cloudinary/         # Cloudinary image upload config
│   ├── custom/             # Custom auth handlers, UserPrincipal, OAuth2
│   ├── filter/            # reCAPTCHA filter
│   ├── mvc/               # MVC config (view resolvers, static resources)
│   ├── payment/           # Mercado Pago SDK initialization
│   └── security/          # SecurityConfig (Spring Security filter chain)
├── persistence/
│   ├── entity/            # JPA entities with Lombok
│   │   ├── categoria/     # Categoria, SubCategoria
│   │   ├── compra/        # Compra, DetalleVenta + enums
│   │   ├── empresa/       # Empresa + sector enum
│   │   ├── factura/       # FacturaProveedor, DetalleFactura
│   │   ├── producto/      # Producto + ETipo enum
│   │   ├── rol/           # Rol + ERol enum
│   │   └── usuario/       # Usuario + EIdentificacion enum
│   └── repository/        # Spring Data JPA repositories
├── presentation/
│   ├── controller/
│   │   ├── admin/         # Admin panel controllers (Role: ADMIN)
│   │   ├── principal/     # Public-facing controllers
│   │   ├── error/         # Global error handler
│   │   └── ia/            # AI integration REST controller
│   └── dto/               # Request/Response DTOs organized by domain
├── service/
│   ├── interfaces/        # Service interfaces
│   └── implementation/    # Service implementations (@Service)
└── ECommerceBeerApplication.java
```

### Key Architectural Patterns

- **Layered Architecture**: Controller → Service → Repository → Entity
- **DTO Pattern**: Separate Request/Response DTOs in `presentation/dto/`
- **Service Interfaces**: All services define interfaces in `service/interfaces/` with implementations in `service/implementation/`
- **Custom Authentication**: `CustomUserPrincipal` wraps `Usuario` entity for Spring Security
- **Session-based Auth**: Uses sessions with "Remember Me" support, maximum 2 concurrent sessions per user
- **Thymeleaf Views**: HTML templates in `resources/templates/`, static assets in `resources/static/`

### Entity Relationships

- `Usuario` → `Rol` (Many-to-One), `Empresa` (Many-to-One), `Producto` (One-to-Many as provider)
- `Producto` → `Categoria` (Many-to-One), `SubCategoria` (Many-to-One), `Usuario` (Many-to-One as provider)
- `Compra` → `Usuario` (Many-to-One), `DetalleVenta` (One-to-Many)
- `FacturaProveedor` → `Empresa` (Many-to-One), `DetalleFactura` (One-to-Many)

### Security Model

- Public endpoints: `/auth/**`, `/`, `/Assets/**`, `/Js/**`, `/Css/**`, `/ia/**`, `/webjars/**`
- Admin endpoints: `/admin/**` requires `ROLE_ADMIN`
- Form login + OAuth2 (Google) authentication
- reCAPTCHA filter validates registration/login forms

### AI Integration

- `AIService` uses Spring AI with OpenAI-compatible API (configurable for Ollama)
- Default model: `gemma3:latest` (configurable via `OPENAI_MODEL`)
- REST endpoint: `/ia/ask` (see `AIController`)

### External Services

- **Cloudinary**: Product images uploaded via `CloudinaryService`
- **Mercado Pago**: Payment processing via `MercadoPagoService`
- **Email**: Welcome emails and payment notifications via `EmailService`
- **Weka**: ML predictions in `PrediccionServiceImpl`

## Database

- MySQL in production, H2 for tests
- JPA DDL mode: `validate` (schema must match entities)
- SQL initialization files in `resources/sql/` (disabled by default: `spring.sql.init.mode=never`)
- Historical purchase data split by year files (compra-2020 through compra-2025)

## Testing

- Tests use H2 in-memory database (see `TestSecurityConfig`)
- Run tests with: `./mvnw test`
- Test scope dependencies: `spring-boot-starter-test`, `spring-security-test`, `h2`