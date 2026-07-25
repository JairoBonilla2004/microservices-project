# =============================================================================
#  Dockerfile — master-gateway-front
#  React 19 + TypeScript + Vite
#
#  Construcción multi-etapa optimizada:
#    - Etapa 1 (deps)     : Instalación de dependencias con npm ci (cacheable)
#    - Etapa 2 (builder)  : Compilación TypeScript + bundling con Vite
#    - Etapa 3 (final)    : Nginx Alpine con configuración segura, proxy reverso
#                           y usuario no-root.
#
#  Autores:
#    - Jairo Bonilla
#    - Julio Viche
#    - Reishel Tipán
# =============================================================================

# ---------------------------------------------------------------------------
#  Etapa 1 — Dependencias
# ---------------------------------------------------------------------------
FROM node:22-alpine AS deps

WORKDIR /app

# Copiar solo los archivos de manifiesto para cachear capas de Docker
COPY package.json package-lock.json ./

# Instalar dependencias de forma reproducible
RUN npm ci --frozen-lockfile

# ---------------------------------------------------------------------------
#  Etapa 2 — Compilación
# ---------------------------------------------------------------------------
FROM node:22-alpine AS builder

WORKDIR /app

# Copiar node_modules desde la etapa deps
COPY --from=deps /app/node_modules ./node_modules

# Copiar el código fuente
COPY . .

# Argumento de build para la URL de la API (inyectada en tiempo de build)
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

# Compilar TypeScript y empaquetar con Vite
RUN npm run build

# ---------------------------------------------------------------------------
#  Etapa 3 — Servidor Nginx (producción)
# ---------------------------------------------------------------------------
FROM nginx:1.27-alpine

# Metadatos de la imagen
LABEL \
  org.opencontainers.image.title="master-gateway-front" \
  org.opencontainers.image.description="Frontend del Master Gateway — React + TypeScript" \
  org.opencontainers.image.version="0.0.0" \
  org.opencontainers.image.vendor="ESPE" \
  org.opencontainers.image.source="https://github.com/espe/master-gateway-front" \
  org.opencontainers.image.licenses="UNLICENSED"

# Eliminar archivos default de Nginx (seguridad: no servir contenido por defecto)
RUN rm -rf /usr/share/nginx/html/* /etc/nginx/conf.d/default.conf

# Copiar el build de la etapa builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Argumento de build: URL del backend para el proxy reverso
# En Docker Compose se resuelve vía DNS interno como http://master-gateway:8080
ARG API_PROXY_PASS=http://backend-app:8080

# Copiar configuración personalizada de Nginx y reemplazar placeholder
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN sed -i "s|__API_PROXY_PASS__|${API_PROXY_PASS}|g" /etc/nginx/conf.d/default.conf

# Crear usuario no-root y ajustar permisos
RUN touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid /usr/share/nginx/html /var/cache/nginx

# Puerto expuesto
EXPOSE 80

# Healthcheck simple
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget -qO- http://localhost:80/ || exit 1

# Ejecutar como usuario no-root
USER nginx

CMD ["nginx", "-g", "daemon off;"]
