# ==============================================================
# Stage 1: Build - Compilar aplicación Java + Vaadin
# ==============================================================
FROM eclipse-temurin:21-jdk AS builder

# Instalar Node.js 20 (requerido para Vaadin frontend build)
RUN apt-get update && apt-get install -y ca-certificates curl gnupg && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg && \
    echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_20.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list && \
    apt-get update && apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar archivos del proyecto
COPY . .

# Compilar: Gradle + Vaadin Frontend + Spring Boot WAR
RUN ./gradlew clean vaadinBuildFrontend bootWar -Dvaadin.ignoreVersionChecks=true

# ==============================================================
# Stage 2: Deploy - Copiar WAR al volumen compartido de Tomcat
# ==============================================================
FROM alpine:latest

WORKDIR /output

# Copiar el WAR generado del stage anterior
COPY --from=builder /app/build/libs/Inventario-*.war /output/ROOT.war

# Variable de entorno para el path de deploy (configurable en Coolify)
ENV DEPLOY_PATH=/deploy

# Script que copia el WAR al volumen compartido y mantiene el contenedor vivo
CMD sh -c "cp /output/ROOT.war ${DEPLOY_PATH}/ROOT.war && echo \"WAR deployed to ${DEPLOY_PATH}/ROOT.war\" && tail -f /dev/null"
