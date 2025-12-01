#!/bin/bash
# Fuerza la recreación de los contenedores para aplicar cambios en la configuración (docker-compose.yml, variables, volúmenes, etc.)
docker compose --env-file .env.dev up -d --force-recreate
