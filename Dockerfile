# ============================
# Etapa 1: Construcción
# ============================
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copiar solo Maven wrapper y config para cache
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline --batch-mode

# Copiar código fuente
COPY src src

# Construir JAR (sin tests, batch-mode)
RUN ./mvnw clean package -DskipTests --batch-mode

# ============================
# Etapa 2: Producción ligera
# ============================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copiar JAR generado
COPY --from=builder /app/target/*.jar app.jar

# Puerto Spring Boot
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]