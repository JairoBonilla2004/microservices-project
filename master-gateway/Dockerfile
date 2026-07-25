# =============================================================================
#  Dockerfile — master-gateway
#
#  Archivo de construcción para la aplicación Master Gateway.
#  Implementa una estrategia de construcción multi-etapa basada en
#  Spring Boot 4, Java 21 y Maven, orientada a optimizar el tamaño de
#  la imagen final, el rendimiento y la seguridad en entornos de producción.
#
#  Características principales:
#    - Compilación mediante una etapa de construcción (builder).
#    - Imagen final basada en un JRE ligero.
#    - Ejecución con usuario sin privilegios (non-root).
#    - Verificación de disponibilidad mediante Healthcheck.
#    - Configuración de la JVM optimizada para producción.
#
#  Autores:
#    - Jairo Bonilla
#    - Julio Viche
#    - Reishel Tipán
# =============================================================================
# ---------------------------------------------------------------------------
#  Etapa 1 — Compilación
# ---------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 1. Copiar solo POM y descargar dependencias (cache layer)
#    Mientras pom.xml no cambie, Docker reusará este cache.
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# 2. Copiar el código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------------------------------------------------------------------------
#  Etapa 2 — Imagen final (producción)
# ---------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

# Metadatos de la imagen (OCI labels)
LABEL \
  org.opencontainers.image.title="master-gateway" \
  org.opencontainers.image.description="API Gateway del sistema de microservicios — Autenticación, autorización y registro de servicios" \
  org.opencontainers.image.version="0.0.1-SNAPSHOT" \
  org.opencontainers.image.vendor="ESPE" \
  org.opencontainers.image.source="https://github.com/espe/master-gateway" \
  org.opencontainers.image.licenses="UNLICENSED"

# Crear usuario no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copiar el JAR desde la etapa builder
COPY --from=builder /app/target/*.jar app.jar

# Puerto expuesto por la aplicación
EXPOSE 8080

# Healthcheck: verifica que el actuator esté vivo
HEALTHCHECK \
  --interval=30s \
  --timeout=3s \
  --retries=3 \
  --start-period=40s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Flags JVM base (ENV, no system properties — overrideable por docker-compose)
#   - UseZGC     : Garbage Collector de baja latencia (Java 21+)
#   - MaxRAMPercentage: Límite de heap adaptativo en contenedores
#   - Tuned para startups rápidos y menor huella de memoria
ENV JAVA_OPTS="-XX:+UseZGC \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/./urandom"

# Perfil activo por defecto (ENV, overrideable via docker-compose -e)
ENV SPRING_PROFILES_ACTIVE=prod

# Cambiar al usuario no-root
USER appuser

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
