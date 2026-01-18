# Configuración de Authentik para API Management

## Problema Actual
El error `403 FORBIDDEN: Token invalid/expired` ocurre porque el token de API estático ha expirado y las llamadas a la API de Authentik no están autenticadas.

## Solución Implementada
Hemos modificado `AuthentikService` para usar el **token OAuth2 del usuario autenticado** en lugar del token de API estático.

## Configuración Requerida en Authentik

### Opción 1: Usar Permisos de Aplicación (RECOMENDADO)

En Authentik, necesitas configurar la aplicación OAuth2 para que tenga permisos de API:

1. **Accede a Authentik Admin**: http://localhost:9000
2. **Ve a Applications > Applications**
3. **Encuentra tu aplicación "inv"** y edítala
4. **En la sección "Permissions"**:
   - Asegúrate de que está vinculada a un **Provider OAuth2/OpenID**
5. **Ve al Provider asociado** (normalmente llamado "inv-provider")
6. **En "Advanced protocol settings"**:
   - Verifica que los **Scopes** incluyan:
     - `openid`
     - `profile`
     - `email`  
     - `groups`
     
### Opción 2: Crear un Scope Personalizado con Permisos de API

Si necesitas que el token OAuth2 tenga acceso a la API de gestión:

1. **Ve a Customization > Property Mappings**
2. **Crea un nuevo Scope Mapping**:
   - Name: `api-access`
   - Scope name: `api`
   - Expression: ```python
   return {
       "api_permisos": [
           "core.view_user",
           "core.add_user", 
           "core.change_user",
           "core.view_group",
           "core.add_group",
           "core.change_group"
       ]
   }
   ```
3. **Agrega este scope al Provider**
4. **Actualiza application.properties**:
   ```properties
   spring.security.oauth2.client.registration.authentik.scope=openid,profile,email,groups,api
   ```

### Opción 3: Crear un Nuevo Token de API (SOLUCIÓN TEMPORAL)

Si prefieres mantener el token estático como fallback:

1. **Ve a Directory > Tokens & App passwords**
2. **Crea un nuevo Token**
3. **Configuración**:
   - **Identifier**: `inventario-api-token`
   - **User**: Selecciona un usuario admin (ej: akadmin)
   - **Intent**: API Token
   - **Expires**: Never (o fecha lejana)
4. **Copia el token generado**
5. **Actualiza application.properties**:
   ```properties
   authentik.api.token=<nuevo-token-aqui>
   ```

## Verificación

Con la configuración actual, cuando inicies sesión y hagas clic en "Gestionar Roles", deberías ver en los logs:

```
DEBUG: Using OAuth2 user access token for Authentik API
```

En lugar de:

```
DEBUG: Using static API token for Authentik API (fallback)
```

## Permisos por Rol (Crear Derecho)

Como mencionaste "crear derecho", en Authentik esto se refiere a asignar permisos a nivel de aplicación usando **Roles (Groups) con atributos personalizados**:

### Configuración de Roles con Permisos de Aplicación:

1. **Ve a Directory > Groups**
2. **Para cada rol (ADMIN, CAJERO, INVENTARIO, etc.)**:
3. **En la sección "Attributes"**, agrega:
   ```yaml
   app_permissions:
     - MODULE_INVENTORY
     - MODULE_SALES
     - MODULE_ACCOUNTING
     - MODULE_REPORTS
     - MODULE_SETTINGS
   ```

Esto ya está implementado en el código de la aplicación en `UsersView.java`.

## Troubleshooting

### Si sigues viendo 403:

1. **Verifica que el usuario tiene el grupo correcto** en Authentik
2. **Verifica que el token OAuth2 se está obteniendo correctamente**:
   - Revisa los logs de Spring Boot
   - Busca mensajes de "DEBUG: Using OAuth2 user access token"
3. **Verifica que el usuario admin tiene permisos** en Authentik:
   - El usuario debe estar en el grupo "authentik Admins" o tener permisos equivalentes

### Si el token OAuth2 no funciona:

El código tiene un **fallback** al token estático, por lo que si creas un nuevo token de API (Opción 3), la aplicación seguirá funcionando mientras implementas la configuración completa de OAuth2.
