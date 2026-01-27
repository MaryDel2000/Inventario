#!/bin/bash
set -e

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
PROJECT_ROOT="$SCRIPT_DIR/.."

echo "Compiling and generating WAR from $PROJECT_ROOT..."
cd "$PROJECT_ROOT"
./gradlew clean vaadinBuildFrontend bootWar -Dvaadin.ignoreVersionChecks=true

if [ $? -eq 0 ]; then
    echo "Build Successful. Deploying..."
    
    SERVER="148.113.173.174"
    USER="root"
    PORT="2244"
    REMOTE_PATH="/data/coolify/applications/g8k4w0w8kco884cs0gkkcgw4/data/tomcat/webapps/inventario.war"
    # Key is successfully located in the deploy dir relative to script execution or absolute path
    IDENTITY_FILE="$SCRIPT_DIR/deploy_key"

    echo "Transferring WAR to $SERVER..."
    scp -P $PORT -i "$IDENTITY_FILE" -o StrictHostKeyChecking=no build/libs/Inventario-*.war ${USER}@${SERVER}:${REMOTE_PATH}
    
    echo "Deployment Complete."
else
    echo "Build Failed."
    exit 1
fi
