# Coolify Deployment - MariaStaff Inventario

## 🎯 Arquitectura: 2 Proyectos

```
┌────────────────────────────────────────────────┐
│  PROYECTO 1: Infraestructura                   │
│  ├── PostgreSQL (Authentik + App DB)           │
│  ├── Authentik (Server + Worker)               │
│  └── Tomcat (Application Server)               │
│                                                 │
│  Build Pack: Docker Compose                    │
│  Auto-deploy: ❌ Manual                         │
└────────────────────────────────────────────────┘

┌────────────────────────────────────────────────┐
│  PROYECTO 2: Builder                           │
│  └── Compila .war y lo copia a Tomcat          │
│                                                 │
│  Build Pack: Dockerfile                        │
│  Auto-deploy: ✅ En cada push                   │
└────────────────────────────────────────────────┘

        Volumen Compartido:
   /opt/mariastaff-dev/tomcat/webapps/
```

---

## 🚀 Setup Rápido

### 1️⃣ Preparar Servidor

```bash
# SSH al servidor
ssh user@your-server.com

# Crear directorios
sudo mkdir -p /opt/mariastaff-dev/{postgresql,authentik/{media,certs},tomcat/webapps}
```

---

### 2️⃣ Configurar Proyecto 1: Infraestructura

**En Coolify:**

1. **New Resource** → Docker Compose
2. **Repository:** `tu-repo/Inventario`
3. **Branch:** `main`
4. **Docker Compose Location:** `coolify/infra/docker-compose.yml`
5. **Auto-deploy:** ❌ DESACTIVADO

**Variables de Entorno (19):**
```env
INSTANCE_NAME=mariastaff-dev
NETWORK_NAME=mariastaff-dev-network

POSTGRES_DB=authentik_dev
POSTGRES_USER=authentik_dev
POSTGRES_PASSWORD=6rJ9UrArzOhwecbv5KGYw

APP_DB_NAME=dev_app_db
APP_DB_USER=dev_app_user
APP_DB_PASSWORD=F4cxO1xdCdKRx2VuHCh2gQ

AUTHENTIK_SECRET_KEY=XbMZhhsGtrnykAotX4pqPYU3AVNRQerrxNb0A7UY5qiVjLWcRx
AUTHENTIK_EXTERNAL_HOST=https://auth.mariastaff.com
AUTHENTIK_BOOTSTRAP_EMAIL=admin@dev.localhost
AUTHENTIK_BOOTSTRAP_PASSWORD=dGURiWZoV4HTegeOxMRykw

AUTHENTIK_EMAIL__FROM=authentik@localhost
AUTHENTIK_EMAIL__HOST=
AUTHENTIK_EMAIL__PORT=587
AUTHENTIK_EMAIL__USERNAME=
AUTHENTIK_EMAIL__PASSWORD=
AUTHENTIK_EMAIL__USE_TLS=true
AUTHENTIK_EMAIL__USE_SSL=false
```

**Dominios:**
- `auth.mariastaff.com` → `authentik-server:9000`
- `apps.mariastaff.com` → `tomcat:8080`

**Deploy** → Click "Deploy"

---

### 3️⃣ Configurar Proyecto 2: Builder

**En Coolify:**

1. **New Resource** → Dockerfile
2. **Repository:** `tu-repo/Inventario` (mismo repo)
3. **Branch:** `main`
4. **Dockerfile Location:** `coolify/builder/Dockerfile`
5. **Auto-deploy:** ✅ ACTIVADO

**Variables de Entorno (2):**
```env
INSTANCE_NAME=mariastaff-dev
DEPLOY_PATH=/deploy
```

**Persistent Storage (¡CRÍTICO!):**
```
Source: /deploy
Destination: /opt/mariastaff-dev/tomcat/webapps
Type: Bind mount
```

**Nota:** El `DEPLOY_PATH` debe coincidir con el "Source" del persistent storage.

**GitHub Webhook:** Configurar para auto-deploy

**Deploy** → Click "Deploy"

---

## 🔄 Flujo de Trabajo

### Deploy Inicial:
1. ✅ Deploy Proyecto 1 → Infraestructura levantada
2. ✅ Deploy Proyecto 2 → Primer .war generado
3. ✅ Tomcat auto-despliega

### Actualización de Código:
1. 💻 Push to GitHub
2. 🔔 Webhook → Coolify
3. 🔨 Builder auto-compila
4. 📦 Nuevo .war → Tomcat
5. ♻️ Tomcat auto-reload
6. ✅ App actualizada!

---

## 📁 Estructura de Archivos

```
coolify/
├── infra/
│   ├── docker-compose.yml    # Proyecto 1: Infraestructura
│   └── assets/                # Assets de Authentik (custom.css, logos)
│       ├── custom.css
│       ├── icon-MariaStaff.png
│       └── logo-MariaStaff.png
│
├── builder/
│   └── Dockerfile             # Proyecto 2: Builder
│
└── README.md                  # Esta guía
```

**Nota:** El script `init-app-db.sh` está **inline en docker-compose.yml**, no es necesario como archivo separado.

---

## 🔌 Conectividad

### Red Interna (`mariastaff-dev-network`):
```
db:5432 ◄─── authentik-server:9000 ◄─── tomcat:8080
              authentik-worker
```

### Referencias en Código:
- **Base de datos de app:** `jdbc:postgresql://db:5432/dev_app_db`
- **Authentik Backend:** `http://authentik-server:9000`

### Acceso Externo:
- **Authentik:** https://auth.mariastaff.com
- **App:** https://apps.mariastaff.com

---

## 🆘 Troubleshooting

### Ver Logs:
```bash
# Infraestructura
docker logs -f mariastaff-dev-db
docker logs -f mariastaff-dev-authentik-server
docker logs -f mariastaff-dev-tomcat

# Builder
docker logs -f <builder-container-name>
```

### Verificar .war desplegado:
```bash
# Ver contenido del volumen
ls -la /opt/mariastaff-dev/tomcat/webapps/

# Ver si Tomcat lo detectó
docker exec -it mariastaff-dev-tomcat ls -la /usr/local/tomcat/webapps/
```

### Verificar comunicación:
```bash
# Desde Tomcat
docker exec -it mariastaff-dev-tomcat ping db
docker exec -it mariastaff-dev-tomcat ping authentik-server
```

### Builder no copia el .war:
```bash
# Verificar persistent storage en Coolify
# Debe estar: /deploy → /opt/mariastaff-dev/tomcat/webapps

# Ver logs del builder
docker logs -f <builder-container>
```

---

## ⚠️ Puntos Importantes

1. **Assets de Authentik:** 
   - Están en `coolify/infra/assets/`
   - Se montan en Authentik Server via: `./assets:/web/dist/custom`
   - Al hacer push, Coolify los trae automáticamente

2. **Init Script PostgreSQL:**
   - Ya NO es un archivo separado
   - Está inline en el `command:` del servicio `db`
   - Se ejecuta automáticamente al crear el contenedor

3. **Volumen Compartido:**
   - **Proyecto 1 (Tomcat):** Lee de `/usr/local/tomcat/webapps` que está mapeado a `/opt/mariastaff-dev/tomcat/webapps`
   - **Proyecto 2 (Builder):** Escribe en `${DEPLOY_PATH}` (default: `/deploy`) que Coolify mapea a `/opt/mariastaff-dev/tomcat/webapps`
   - **En Coolify (Proyecto 2):** Configurar Persistent Storage:
     ```
     Source: /deploy  ← Debe coincidir con DEPLOY_PATH
     Destination: /opt/mariastaff-dev/tomcat/webapps
     ```
   - **Flujo completo:**
     ```
     Builder escribe: ${DEPLOY_PATH}/ROOT.war
                         ↓ (Coolify mapea)
     Filesystem: /opt/mariastaff-dev/tomcat/webapps/ROOT.war
                         ↓ (Volume mount)
     Tomcat lee: /usr/local/tomcat/webapps/ROOT.war
     ```

4. **Auto-reload de Tomcat:**
   - Tomcat detecta cambios en `ROOT.war` automáticamente
   - No necesitas reiniciar nada manualmente

---

## ✅ Checklist de Deploy

- [ ] Servidor preparado (`/opt/mariastaff-dev/...` creado)
- [ ] Proyecto 1 creado en Coolify
- [ ] 19 variables configuradas en Proyecto 1
- [ ] Dominios configurados
- [ ] Proyecto 1 desplegado exitosamente
- [ ] Proyecto 2 creado en Coolify
- [ ] Persistent storage configurado en Proyecto 2
- [ ] Auto-deploy activado en Proyecto 2
- [ ] GitHub webhook configurado
- [ ] Proyecto 2 desplegado exitosamente
- [ ] `https://auth.mariastaff.com` accesible
- [ ] `https://apps.mariastaff.com` accesible
- [ ] Push de prueba → auto-deploy funciona

---

## 🎉 Listo!

Ahora cada vez que hagas push a GitHub, tu aplicación se compilará y desplegará automáticamente.

**No más builds manuales! 🚀**
