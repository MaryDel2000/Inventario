# AGENT_README - Documentación del Proyecto Inventario

Este documento resume el estado actual, la arquitectura y las decisiones de diseño del proyecto "Inventario". Está dirigido a agentes de IA y desarrolladores para entender rápidamente el contexto.

## 1. Resumen del Proyecto
Sistema de gestión de inventario, punto de venta (POS), compras y contabilidad desarrollado en Java.
- **Backend**: Spring Boot 3.x with Spring Data JPA.
- **Frontend**: Vaadin Flow 24.x (Server-side rendering).
- **Base de Datos**: PostgreSQL (migraciones con Flyway).
- **Seguridad**: Spring Security (integrado con Authentik).

## 2. Estrategia de Diseño y Estilos (CRÍTICO)
**IMPORTANTE**: Aunque el proyecto utiliza Vaadin, **NO dependemos de los estilos predeterminados de Lumo**.
Se ha adoptado una estrategia de **"Utility-First" con Tailwind CSS**.

### Reglas de Estilo:
*   **Tailwind CSS**: Es la fuente principal de estilos. Se utiliza para márgenes, padding, colores, tipografía, flexbox, grid, etc.
*   **Lumo Desactivado/Sobrescrito**: Se han eliminado o sobrescrito gran parte de los estilos por defecto de Vaadin Lumo para permitir que Tailwind controle la apariencia.
*   **Tema Oscuro/Claro**: La gestión del tema se hace mediante clases de Tailwind (`dark:` prefix) y variables CSS personalizadas definidas en `theme-dark.css` y `theme-light.css`.

### Archivos Clave de Estilos:
*   `src/main/frontend/styles/index.css`: Estilos globales y parches críticos (ej. scrollbars, overlays).
*   `src/main/frontend/styles/theme-dark.css`: Definición de variables CSS para el modo oscuro.
*   `src/main/frontend/styles/theme-light.css`: Definición de variables CSS para el modo claro.
*   `tailwind.config.js`: Configuración de Tailwind, incluyendo colores personalizados como `bg-primary`, `text-main`, etc.

## 3. Componentes UI Personalizados
Para lograr una apariencia moderna que Lumo no ofrece por defecto, se han creado componentes compuestos (`Composite`):

*   **`AppHeader.java`**: Barra superior personalizada. Contiene el toggle de sidebar, toggle de tema y menú de usuario.
    *   *Nota*: El menú de usuario utiliza un `ContextMenu` altamente personalizado con clases Tailwind. Se ha parcheado `index.css` para hacer transparente el overlay de Vaadin y permitir que Tailwind controle el fondo.
*   **`AppSidebar.java`**: Barra lateral personalizada con soporte para ítems expandibles y colapsado responsivo.
*   **`AppNavItem.java`**: Ítems de navegación estilizados manualmente con Tailwind (colores específicos `#607d8b` para inactivos, blanco/primary para activos).

## 4. Estructura del Proyecto
*   `com.mariastaff.Inventario`
    *   `security`: Configuración de seguridad.
    *   `ui`: **Ruta Base**: La aplicación Vaadin se sirve bajo `/ui/*` (configurado en `application.properties` o `SecurityConfig`).
        *   `components`: Componentes UI reutilizables (`base`, `composite`).
        *   `layouts`: Layout principal (`MainLayout.java`).
        *   `views`: Vistas organizadas por módulos (`inventory`, `sales`, `purchases`, `accounting`, `settings`).

## 5. Base de Datos y Migraciones (Flyway)
*   **Gestión de Esquemas**: Se utiliza **Flyway** para el control de versiones de la base de datos.
*   **Ubicación**: Los scripts SQL de migración se encuentran en `src/main/resources/db/migration`.
*   **Convención**: `V{version}__{descripcion}.sql` (ej. `V1__init_schema.sql`).
*   **Nota**: Si hay errores de permisos ("permission denied for schema public"), verificar los permisos del usuario de BD configurado en `application.properties`.

## 6. Internacionalización (i18n)
*   **Soporte**: El proyecto está preparado para múltiples idiomas.
*   **Archivo de Recursos**: Los textos se almacenan en `src/main/resources/messages.properties`.
*   **Uso en Vistas**: 
    *   Utilizar `getTranslation("clave.propiedad")` en los componentes.
    *   Evitar textos "hardcodeados" en las clases Java.
    *   Ejemplo: `new Button(getTranslation("action.save"))`.

## 7. Comandos Útiles
*   **Ejecutar en desarrollo**: `./run-dev.sh` (Ejecuta Gradle bootRun).
    *   *Nota*: Si falla por puerto ocupado, asegurar matar procesos java anteriores.

## 8. Contexto de Soluciones Recientes
*   **Problema ContextMenu**: El menú de usuario tenía fondo blanco en modo oscuro.
    *   **Solución**: Se añadió una regla en `index.css` para `vaadin-context-menu-overlay::part(overlay) { background-color: transparent; }` y se aplicaron clases `bg-white dark:bg-[#2c2c2c]` directamente al `Div` contenido en el menú.
*   **Responsive**: El Header y Sidebar tienen lógica JS inyectada en `MainLayout.java` para manejar el estado móvil/escritorio.

