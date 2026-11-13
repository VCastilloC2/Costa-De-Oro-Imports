# syntax=docker/dockerfile:1
# ==========================================================
# Etapa 1: Build (Maven + JDK 25)
# ==========================================================
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /build

# 1) Solo el pom: las dependencias quedan en una capa cacheada
#    mientras el pom.xml no cambie.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

# 2) Código fuente y empaquetado (los tests corren en CI, no en el deploy)
COPY src ./src
RUN mvn -B -q clean package -DskipTests \
 && cp target/*.jar app.jar \
 && java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted


# ==========================================================
# Etapa 2: Runtime (solo JRE 25)
# ==========================================================
FROM eclipse-temurin:25-jre-noble

# Usuario sin privilegios
RUN groupadd --system spring && useradd --system --gid spring --no-create-home spring

WORKDIR /app

# Capas de Spring Boot ordenadas de menos a más cambiantes
COPY --from=builder --chown=spring:spring /build/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /build/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /build/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /build/extracted/application/ ./

USER spring

# Render inyecta PORT; server.port=${PORT:8080}
# El perfil (SPRING_PROFILES_ACTIVE=cloud) se define en Render, así docker-compose sigue usando "local".
ENV PORT=8080 \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom -Dfile.encoding=UTF-8"

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
