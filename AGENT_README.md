# AGENT_README - Proyecto Inventario

## 1. Tecnologías
- **Backend**: Spring Boot 3 + Data JPA.
- **Frontend**: Vaadin Flow 24 + **Tailwind CSS**.
- **DB**: PostgreSQL + Flyway (`src/main/resources/db/migration`).

## 2. Estilos y Componentes (CRÍTICO)
- **Tailwind First**: Prioridad absoluta a clases de utilidad Tailwind.
- **Componentes Personalizados**:
  - `TailwindModal`: Reemplaza a `Dialog`. Modal centrado, con backdrop oscuro (`bg-gray-900/75`), bloqueo de pantalla y estilos modernos.
  - `TailwindToggle`: Reemplaza a `Checkbox`. Interruptor estilo switch animado.
  - `TailwindNotification`: Reemplaza a `Notification`. Toasts en esquina inferior derecha con colores semánticos (Verde/Éxito, Rojo/Error, Gris/Info) usando estilos inline para garantizar visibilidad.
- **Global Styles (`index.css`)**: Estilos base para componentes Vaadin complejos (`vaadin-grid`) que requieren sobrescritura profunda.

## 3. Desarrollo
- **i18n**: SIEMPRE usa `getTranslation("key")`. Textos en `src/main/resources/messages.properties`.
- **Estructura**: `ui/views` (Vistas), `ui/layouts` (Layouts), `ui/components/base` (Componentes UI reutilizables).
- **Estado Actual**: 
  - Vistas de Inventario (Productos, Categorías, UOM, Almacenes, Ubicaciones) refactorizadas con nuevos componentes `TailwindModal`, `TailwindToggle` y `TailwindNotification`.
  - Formularios UI funcionales (apertura/cierre, validación básica visual).
  - **Pendiente**: Lógica de persistencia (Guardado en DB) en los botones "Guardar".
- **Ejecutar**: `./run-dev.sh`.
