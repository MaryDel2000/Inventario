# AGENT_README - Proyecto Inventario

## 1. Tecnologías
- **Backend**: Spring Boot 3 + Data JPA.
  - *Nota*: `AbstractEntity` refactorizado para usar getters en `equals/hashCode` (fix proxies Hibernate).
  - *Nota*: `ProductoService` implementa borrado en cascada (Producto -> Variantes -> Lotes).
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
    - `CategoriesView`, `UOMView`, `BatchesView` independientes **ELIMINADAS**. Gestión integrada en `ProductsView`.
    - `ProductsView`: 
      - Formularios permiten crear categorías y unidades dinámicamente ("AllowCustomValue").
      - Botones de acción ("Ver Categorías", "Ver Unidades", "Ver Variantes", "Ver Lotes") abren diálogos de gestión CRUD completos.
      - **Creación de Producto**:
        - Soporta creación inline de Lote inicial.
        - Si se ingresa información de lote, el sistema auto-crea una Variante default y el Lote asociado al guardar.
      - Selectores filtran solo ítems activos.
      - **Edición de Producto**:
        - Diálogo de edición carga datos frescos (`findById`) para evitar problemas de proxy.
        - **Grid Interactiva de Lotes**: Dentro del diálogo de edición de producto, se muestra una tabla con los lotes asociados. Permite editar lotes (código, caducidad, variante) directamente sin salir de la vista.
        - **Grid Principal**:
          - Nueva columna "Variantes" muestra lista de variantes asociadas para mayor visibilidad.
          - Actualización automática de la lista y filtros al cerrar los diálogos de gestión (Categorías, Variantes, etc.).

        - Botón de "Eliminar" retirado de la UI principal (lógica backend soporta borrado en cascada de variantes y lotes).
    - Vistas restantes (Almacenes, Movimientos, Ubicaciones) refactorizadas con componentes Tailwind.
  - **Pendiente**: Lógica de stocks iniciales (InvExistencia) completa en creación de productos.
- **Ejecutar**: `./run-dev.sh`.
