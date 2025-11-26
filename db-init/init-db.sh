#!/bin/bash
set -e

# Las variables POSTGRES_USER, INVENTARIO_DB_USER, etc., son pasadas desde docker-compose.yml
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- 1. Crear la base de datos para la aplicación de inventario
    CREATE DATABASE inventario_db;

    -- 2. Crear un usuario y contraseña específicos para la aplicación de inventario
    CREATE USER ${INVENTARIO_DB_USER} WITH PASSWORD '${INVENTARIO_DB_PASSWORD}';

    -- 3. Darle todos los privilegios a ese usuario sobre su base de datos
    GRANT ALL PRIVILEGES ON DATABASE inventario_db TO ${INVENTARIO_DB_USER};
EOSQL