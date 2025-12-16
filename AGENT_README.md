# AGENT_README - Proyecto Inventario

## 1. Tecnologías
- **Backend**: Spring Boot 3 + Data JPA.
- **Frontend**: Vaadin Flow 24 + **Tailwind CSS**.
- **DB**: PostgreSQL + Flyway (`src/main/resources/db/migration`).

## 2. Estilos y CSS (CRÍTICO)
- **Tailwind First**: Usa clases de Tailwind para todo lo posible.
- **Global Styles (`index.css`)**:
  - Contiene **estilos base** para componentes Vaadin (`vaadin-grid`, inputs) que sobrescriben Lumo.
  - **No crear estilos Java inline** si se puede arreglar globalmente aquí (ej. espaciado de grids, alineación, cursores).
- **Tema Oscuro**: Soportado nativamente vía `theme-dark.css` y clases `dark:`.

## 3. Desarrollo
- **i18n**: SIEMPRE usa `getTranslation("key")`. Textos en `src/main/resources/messages.properties`.
- **Estructura**: Vistas en `ui/views`, Layout en `ui/layouts/MainLayout.java`.
- **Estado**: Formularios de creación en vistas de inventario (Productos, Categorías, UOM, Almacenes, Ubicaciones) listos en UI, pendientes de lógica de guardado.
- **Ejecutar**: `./run-dev.sh`.
