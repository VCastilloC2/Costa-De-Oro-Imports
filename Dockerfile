# ============================
# Etapa 1: Build
# ============================
FROM eclipse-temurin:25-jdk-jammy AS builder

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline --batch-mode

COPY src ./src

RUN ./mvnw clean package -DskipTests --batch-mode

# ============================
# Etapa 2: Runtime
# ============================
FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]