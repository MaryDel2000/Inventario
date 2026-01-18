#!/bin/bash
# run-dev.sh
# Construye el proyecto y despliega el WAR en la carpeta de Tomcat

echo "Compilando y generando WAR..."
./gradlew clean vaadinBuildFrontend bootWar -Dvaadin.ignoreVersionChecks=true

if [ $? -eq 0 ]; then
    echo "Construcción exitosa. Desplegando..."
    
    # Limpiar la carpeta deploy para un despliegue limpio
    echo "Limpiando carpeta docker/deploy..."
    sudo rm -rf docker/deploy/*
    
    # Asegurarse de que el directorio existe
    mkdir -p docker/deploy
    
    # Copiar el WAR renombrándolo a ROOT.war para que sea la aplicación raíz (localhost:8081)
    # Si prefieres un context path específico (ej. localhost:8081/mi-app), cambia ROOT.war por mi-app.war
    cp build/libs/Inventario-*.war docker/deploy/ROOT.war
    
    echo "Archivo copiado a docker/deploy/ROOT.war"
    echo "Tomcat detectará el cambio y redeplegará la aplicación automáticamente."
else
    echo "Error en la construcción. No se ha desplegado nada."
    exit 1
fi