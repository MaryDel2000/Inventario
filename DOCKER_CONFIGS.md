# Docker Configurations

Este proyecto contiene dos configuraciones de Docker separadas:

## 📂 `docker/` - Configuración Local/Original

**Para desarrollo local con Docker Compose tradicional**

### Características:
- ✅ Usa rutas relativas (`./postgresql`, `./media`, etc.)
- ✅ Archivos `.env.dev` y `.env` para configuración
- ✅ `env_file:` en docker-compose para cargar variables
- ✅ Scripts helper (`iniciar.sh`, `detener.sh`, `recrear.sh`)
- ✅ Configuración separada por servicio (postgres.yml, authentik.yml, tomcat.yml)

### Uso:
```bash
cd docker
./iniciar.sh    # Iniciar servicios
./detener.sh    # Detener servicios
./recrear.sh    # Recrear servicios
```

### Acceso:
- Authentik: http://localhost:9000
- PostgreSQL: localhost:5432
- Tomcat: http://localhost:8081

---

## 📂 `docker2/` - Configuración para Coolify

**Para despliegue en producción con Coolify**

### Características:
- ✅ Rutas absolutas (`/opt/${INSTANCE_NAME}/...`)
- ✅ Sin archivos `.env` (Coolify gestiona las variables)
- ✅ Networks dinámicas con `${NETWORK_NAME}`
- ✅ Dockerfiles separados para cada servicio
- ✅ Multi-instancia (dev, staging, prod)
- ✅ Documentación completa de despliegue

### Archivos Principales:
- `docker-compose.coolify.yml` - Stack principal para Coolify
- `Dockerfile-postgres` - PostgreSQL personalizado
- `Dockerfile-authentik-server` - Authentik Server
- `Dockerfile-authentik-worker` - Authentik Worker
- `Dockerfile-tomcat` - Tomcat App Server
- `.env.coolify.example` - Referencia de variables
- `COOLIFY_DEPLOYMENT.md` - Guía de despliegue completa

### Uso:
1. Lee `COOLIFY_DEPLOYMENT.md` para instrucciones completas
2. Configura variables en Coolify (no uses archivos .env)
3. Despliega usando el dashboard de Coolify

---

## 🔄 Diferencias Clave

| Aspecto | `docker/` | `docker2/` |
|---------|-----------|------------|
| **Propósito** | Desarrollo local | Producción (Coolify) |
| **Rutas** | Relativas (`./`) | Absolutas (`/opt/...`) |
| **Config** | Archivos `.env` | Variables en Coolify |
| **Networks** | Por defecto | Dinámicas (`${NETWORK_NAME}`) |
| **Instancias** | Una sola | Múltiples (dev/prod) |
| **Documentación** | Scripts básicos | Guía completa |

---

## 🎯 ¿Cuál Usar?

### Usa `docker/` si:
- Estás desarrollando localmente
- Quieres levantar rápido el entorno
- Necesitas hacer debug
- Trabajas en tu laptop/PC

### Usa `docker2/` si:
- Vas a desplegar en Coolify
- Necesitas múltiples entornos (dev, staging, prod)
- Quieres gestión centralizada de variables
- Estás en producción o staging

---

## 📝 Notas

- Ambas configuraciones son independientes
- Puedes mantener ambas en el repositorio
- **NO** mezcles archivos entre carpetas
- Los cambios en Dockerfiles deben replicarse manualmente si aplican a ambos

## 🔗 Más Información

- Para `docker/`: Ver los scripts en la carpeta y comentarios en docker-compose-infra.yml
- Para `docker2/`: Leer [COOLIFY_DEPLOYMENT.md](./docker2/COOLIFY_DEPLOYMENT.md)
