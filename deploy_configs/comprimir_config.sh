#!/bin/bash

# Este script comprime la carpeta system_config desde su ubicación actual
SOURCE_DIR="/home/ulloa/Documentos/maria3/docker"
ARCHIVE_NAME="system_config.tar.gz"

echo "Comprimiendo system_config..."

# Usamos -C para cambiar al directorio y comprimir 'system_config' como ruta relativa
# Esto asegura que el tarball contenga la carpeta 'system_config' en su raiz
tar -czvf "$SOURCE_DIR/$ARCHIVE_NAME" -C "$SOURCE_DIR" system_config

echo "Compresión completada: $SOURCE_DIR/$ARCHIVE_NAME"
