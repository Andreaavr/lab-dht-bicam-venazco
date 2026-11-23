# syntax=docker/dockerfile:1
# =============================================================================
# BiCIAM - imagen Docker en dos etapas
#   1) builder: compila y prueba el proyecto con Maven y genera biciam.jar
#   2) runtime: imagen ligera que solo contiene el JRE 17 y el jar
#
# Nota: la imagen openjdk:17-jre-slim que propone el enunciado nunca se publicó
# (las imágenes "openjdk" están obsoletas y solo hubo variantes -jre hasta Java 11).
# Se usa su sustituta oficial, eclipse-temurin:17-jre-alpine (OpenJDK de Adoptium).
# =============================================================================

# ---------- Etapa 1: construcción ----------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Primero solo el pom, para cachear la descarga de dependencias entre builds
COPY pom.xml .
RUN mvn -B -q dependency:resolve dependency:resolve-plugins

# Después el código fuente y la configuración de los plugins
COPY config ./config
COPY src ./src

# SKIP_TESTS=true permite construir más rápido: docker build --build-arg SKIP_TESTS=true .
ARG SKIP_TESTS=false
RUN mvn -B package -DskipTests=${SKIP_TESTS}

# ---------- Etapa 2: ejecución ----------
FROM eclipse-temurin:17-jre-alpine

LABEL org.opencontainers.image.title="BiCIAM" \
      org.opencontainers.image.description="Biblioteca de metaheurísticas BiCIAM (demo OneMax)" \
      org.opencontainers.image.version="1.0.0"

# Usuario sin privilegios
RUN addgroup -S biciam && adduser -S biciam -G biciam
WORKDIR /app
COPY --from=builder --chown=biciam:biciam /build/target/biciam.jar /app/biciam.jar
USER biciam

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/biciam.jar"]
# Argumentos por defecto: [algoritmo] [bits] [iteraciones]
CMD ["HillClimbing", "50", "1000"]
