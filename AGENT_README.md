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
  - **Filtros**: Se agregó filtro por **Almacén** para ver productos con existencia en una ubicación específica.
- **Movimientos (`MovementsView`)**:
  - Refactorizado para alta precisión.
  - **Lógica Estricta**: `MovimientoService` ahora valida obligatoriedad de almacenes (Origen/Destino) según tipo de movimiento y **prohíbe stocks negativos**.
  - Permite movimientos (Entrada/Salida/Traspaso) seleccionando **Lote** y **Ubicación específica** por cada línea de detalle.
  - Filtros dinámicos de ubicación según almacén seleccionado.
- **Almacenes y Ubicaciones**: Vistas CRUD completas y funcionales.

### Datos y Configuración
- **DataGenerator**: Implementado para tienda de repuestos automotrices.
  - *Estado*: Desactivado (`// @Component`) para evitar reinicios de datos accidentales.
  - Contiene datos de ejemplo: Aceites, Frenos, Baterías, etc.
- **i18n**: Internacionalización extendida a cabeceras de grids, filtros y formularios de movimientos (`messages.properties`). Corregido bug de etiquetas faltantes (`###`).

### Módulo Compras y Ventas (NUEVO - COMPLETADO)
### Módulo Compras y Ventas (NUEVO - COMPLETADO)
- **POS (Punto de Venta)**: `POSView` plenamente operativa.
  - **Validación de Stock**: Impide ventas si no hay existencias suficientes.
  - **Seguridad**: Asigna automáticamente el usuario logueado (OAuth2/Database) a la venta.
  - **Pago**: Soporte para múltiples métodos de pago (Efectivo, Tarjeta, Transferencia) con **captura detallada** (Núm. Tarjeta, Titular, Ref. Transferencia, Banco).
  - **Clientes**: Creación rápida de clientes ("on-the-fly") escribiendo el nombre en el selector.
  - **Multi-moneda**: Precios en USD con conversión automática y visualización de total en C$ (NIO) según tasa de cambio.
  - **UX**: Modal de cobro centrado y responsive. Filtro de búsqueda solo muestra productos activos.
- **Compras (`NewPurchaseView`)**:
  - **Flujo Completo**: Registro de compra -> Generación de Lote -> Entrada de Stock (Movimiento).
  - **Creación Rápida de Productos**: Implementado botón (+) para registrar nuevos productos directamente desde la vista de compra sin salir del flujo.
  - **Requisito**: Requiere tener **Proveedores** registrados previamente.
  - **UI/UX**: Solucionado centrado de modales y superposición de DatePicker/ComboBox (z-index). Estado persistente (@UIScope). Reemplazo de DatePickers nativos por `TailwindDatePicker` para consistencia visual.
  - **Fixes**: Soporte completo para **decimales (punto)** en precios y cantidades (Locale.US).

### Precios y Monedas (ACTUALIZADO)
- **Multi-moneda**: Soporte dual para Córdoba (NIO) y Dólar (USD) en Costos y Precios de Venta.
- **Formulario Producto**:
  - Layout mejorado con precios side-by-side para fácil comparación.
  - "Modo Compra": Oculta campos de stock inicial (Ubicación, Lote) cuando se crea desde una Compra, delegando la entrada de stock al proceso de compra.
- **Migración V1000**: Solucionado conflicto `ON CONFLICT` en script Flyway para inserción de monedas default.

## 4. Notas de Desarrollo
- **Fix Rutas**: `HomeView` eliminado. `InventoryDashboardView` tiene el alias de ruta raíz.
- **DataGenerator**: Activo. Genera datos de repuestos automotrices al inicio. **Nota**: No crea Proveedores ni Usuarios; estos deben gestionarse o existir previamente.
- **Ejecución**: `./run-dev.sh`.

## 5. Pendientes / Próximos Pasos
- **Reportes**: Validar que los reportes de ventas y compras reflejen correctamente los nuevos datos transaccionales.
- **Impresión**: Implementar generación de tickets/facturas PDF desde el POS.
- **UI**: Refinar dashboard principal para incluir KPIs de ventas recientes.
