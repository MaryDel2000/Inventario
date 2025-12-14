#!/bin/bash

# Este script debe ejecutarse con privilegios (sudo) si el destino es la raiz /
ARCHIVE_NAME="system_config.tar.gz"
TARGET_DIR="/system_config"

# Verificamos si somos root
if [ "$EUID" -ne 0 ]; then
  echo "Por favor, ejecuta este script como root (sudo)."
  exit 1
fi

if [ ! -f "$ARCHIVE_NAME" ]; then
    echo "No se encuentra el archivo $ARCHIVE_NAME en el directorio actual."
    exit 1
fi

echo "Eliminando instalación anterior en $TARGET_DIR (si existe)..."
rm -rf "$TARGET_DIR"

echo "Descomprimiendo en la raíz / ..."
# Al extraer en /, como el tar contiene 'system_config/', se creará /system_config
tar -xzvf "$ARCHIVE_NAME" -C /

echo "Descompresión completada. Verifica en $TARGET_DIR"
