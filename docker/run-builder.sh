#!/bin/bash
set -e

echo "Starting Build Process..."

# mimics run-dev2.sh logic

echo "Compiling and generating WAR..."
./gradlew clean vaadinBuildFrontend bootWar -Dvaadin.ignoreVersionChecks=true

if [ $? -eq 0 ]; then
    echo "Build Successful. Deploying..."
    
    # Ensure deploy directory exists
    mkdir -p /deploy
    
    # Copy WAR to shared volume
    echo "Copying to /deploy/ROOT.war..."
    cp build/libs/Inventario-*.war /deploy/ROOT.war
    
    echo "Deployment Complete. Tomcat should auto-reload."
else
    echo "Build Failed."
    exit 1
fi
