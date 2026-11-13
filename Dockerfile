# ============================
# Etapa 1: Build
# ============================
FROM maven:3.9-eclipse-temurin-26 AS builder

WORKDIR /app

# Copiar primero el archivo de configuración de Maven
COPY pom.xml ./

# Descargar las dependencias para aprovechar la caché de Docker
RUN mvn dependency:go-offline --batch-mode

# Copiar el resto del proyecto
COPY . .

# Compilar la aplicación
RUN mvn clean package -DskipTests --batch-mode


# ============================
# Etapa 2: Runtime
# ============================
FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

# Copiar el JAR generado desde la etapa de compilación
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]