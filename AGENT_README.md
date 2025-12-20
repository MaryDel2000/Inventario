# AGENT_README - Proyecto Inventario

## 1. Tecnologías
- **Backend**: Spring Boot 3 + Data JPA.
- **Frontend**: Vaadin Flow 24 + **Tailwind CSS**.
- **DB**: PostgreSQL + Flyway (`src/main/resources/db/migration`).

## 2. Estilos y Componentes (CRÍTICO)
- **Tailwind First**: Prioridad absoluta a clases de utilidad Tailwind.
- **Componentes Personalizados**:
  - `TailwindModal`: Reemplaza a `Dialog`. Modal centrado, backdrop oscuro (`bg-gray-900/75`), soporta ancho dinámico (`setDialogMaxWidth`).
  - `TailwindToggle`: Reemplaza a `Checkbox`. Interruptor estilo switch animado. Constructor requiere label.
  - `TailwindNotification`: Reemplaza a `Notification`. Toasts semánticos (Verde/Éxito, Rojo/Error, Gris/Info).
- **Global Styles (`index.css`)**: 
  - Estilos base para `vaadin-grid`.
  - Fix transparencia en `vaadin-combo-box-overlay` y items.

## 3. Desarrollo
- **i18n**: SIEMPRE usa `getTranslation("key")`. Textos en `src/main/resources/messages.properties`.
- **Estructura**: `ui/views` (Vistas), `ui/layouts` (Layouts), `ui/components/base` (Componentes UI reutilizables).
- **Estado Actual**: 
  - **Inventario**:
    - `CategoriesView` independiente **ELIMINADA**. Gestión integrada en `ProductsView`.
    - `UOMView` independiente **ELIMINADA**. Gestión integrada en `ProductsView`.
    - `ProductsView`: 
      - Formularios permiten crear categorías y unidades dinámicamente ("AllowCustomValue").
      - Botón "Ver Categorías" abre diálogo de gestión (listar, crear, editar, toggle activo, eliminar).
      - Botón "Ver Unidades" abre diálogo de gestión de Unidades de Medida (listar, crear, editar, toggle activo, eliminar).
      - Selectores filtran solo ítems activos.
    - Vistas restantes (Almacenes, etc.) refactorizadas con componentes Tailwind.
  - **Pendiente**: Lógica de persistencia compleja de Productos (Stocks iniciales, Lotes).
- **Ejecutar**: `./run-dev.sh`.
