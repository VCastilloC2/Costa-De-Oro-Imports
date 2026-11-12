
# ============================
# Etapa 1: Build
# ============================
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /app

# Copiar configuración Maven
COPY pom.xml ./

# Descargar dependencias para aprovechar la caché
RUN mvn dependency:go-offline --batch-mode

# Copiar el proyecto
COPY . .

# Compilar sin ejecutar pruebas ni generar Javadoc
RUN mvn clean package \
    -DskipTests \
    -Dmaven.javadoc.skip=true \
    --batch-mode

# Identificar y copiar únicamente el JAR ejecutable
RUN set -eu; \
    JAR="$(find target -maxdepth 1 -type f -name '*.jar' \
    ! -name '*-javadoc.jar' \
    ! -name '*-sources.jar' -print -quit)"; \
    test -n "$JAR"; \
    cp "$JAR" /app/app.jar


# ============================
# Etapa 2: Runtime
# ============================
FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

# Copiar aplicación compilada
COPY --from=builder /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]