# AGENT_README - Proyecto Inventario

## 1. Tecnologías
- **Backend**: Spring Boot 3 + Data JPA.
  - *Nota*: `AbstractEntity` refactorizado para usa getters en `equals/hashCode` (fix proxies Hibernate).
  - *Nota*: `ProductoService` maneja lógica compleja de creación (Producto -> Variante -> Lote -> Existencia).
- **Frontend**: Vaadin Flow 24 + **Tailwind CSS**.
- **DB**: PostgreSQL + Flyway (`src/main/resources/db/migration`).

## 2. Estilos y Componentes (CRÍTICO)
- **Tailwind First**: Prioridad absoluta a clases de utilidad Tailwind.
- **Componentes Personalizados**:
  - `TailwindModal`: Reemplaza a `Dialog`. Modal centrado, backdrop oscuro.
  - `TailwindToggle`: Switch animado para booleanos.
  - `TailwindNotification`: Toasts semánticos (Success, Error, Info).
- **Global Styles (`index.css`)**: 
  - Estilos corregidos para inputs (`vaadin-big-decimal-field`), combos y grids.

## 3. Estado del Proyecto
### Módulo Inventario (COMPLETO)
- **Dashboard**: `InventoryDashboardView` es ahora la vista principal (`@Route("")`).
- **Productos (`ProductsView`)**: 
  - Gestión integral de Categorías, UOM, Variantes y Lotes mediante diálogos modales.
  - **Creación**: Permite definir **Stock Inicial** (Cantidad, Ubicación, Lote).
  - **Visualización**: Grid principal muestra Stocks Totales calculados en tiempo real.
- **Movimientos (`MovementsView`)**:
  - Refactorizado para alta precisión.
  - Permite movimientos (Entrada/Salida/Traspaso) seleccionando **Lote** y **Ubicación específica** por cada línea de detalle.
  - Filtros dinámicos de ubicación según almacén seleccionado.
- **Almacenes y Ubicaciones**: Vistas CRUD completas y funcionales.

### Datos y Configuración
- **DataGenerator**: Implementado para tienda de repuestos automotrices.
  - *Estado*: Desactivado (`// @Component`) para evitar reinicios de datos accidentales.
  - Contiene datos de ejemplo: Aceites, Frenos, Baterías, etc.
- **i18n**: Internacionalización extendida a cabeceras de grids, filtros y formularios de movimientos (`messages.properties`).

## 4. Notas de Desarrollo
- **Fix Rutas**: `HomeView` eliminado. `InventoryDashboardView` tiene el alias de ruta raíz.
- **Fix Data Integrity**: `DataGenerator` maneja limpieza total de tablas con `create-drop` (temporal) para evitar conflictos de FK al regenerar.
- **Ejecución**: `./run-dev.sh`. (Si falla por puerto ocupado, matar procesos java).

## 5. Pendientes / Próximos Pasos
- **POS (Punto de Venta)**: Integrar con el nuevo sistema de precios y stock. Actualmente es un esqueleto.
- **Compras**: Completar vista `NewPurchaseView` para que alimente el inventario (similar a Movimientos de Entrada).
