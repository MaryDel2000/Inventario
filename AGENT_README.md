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

### Base de Datos y Migraciones (ACTUALIZADO)
- **Consolidación**: Se han reorganizado las migraciones en dos archivos maestros para simplificar el mantenimiento y asegurar consistencia:
  - `V1__Esquema_inicial.sql`: Estructura completa de la base de datos (Entidades, Inventario, POS, Contabilidad).
  - `V2__Datos_Ejemplo.sql`: Script unificado que:
    1. Limpia datos previos (`TRUNCATE`).
    2. Carga datos semilla (Monedas, Sucursales, Usuarios).
    3. Simula operaciones históricas (Compras y Ventas de hace 60 días).
    4. Genera Turnos de caja cerrados y Periodos contables.
    5. Carga cuentas por cobrar y abonos parciales.

### Módulo Inventario (COMPLETO)
- **Visualización**: Grid principal muestra Stocks Totales calculados en tiempo real.
- **Movimientos**: Kardex completo (`inv_movimiento`) poblado automáticamente con las operaciones simuladas.
- **Productos**: Configuración compleja (Variantes, Lotes) soportada en V2.

### Módulo Compras y Ventas (COMPLETO)
- **POS (Punto de Venta)**:
  - **I18n**: Interfaz totalmente traducida (`messages.properties`).
  - **Turnos**: Generación automática de turnos cerrados históricos para reportes.
  - **Pagos**: Soporte múltiple (Efectivo, Tarjeta, Transferencia) con validaciones.
- **Cuentas por Cobrar (`ReceivablesView`)**:
  - **Vista Nueva**: Listado de ventas a crédito pendientes.
  - **Funcionalidad**: Integrada con datos simulados (Facturas pendientes y pagos parciales).
  - **I18n**: Cabeceras y estados traducidos.

### Contabilidad (EN PROCESO)
- **Integración**: Las operaciones de Compra y Venta (incluyendo Costo de Venta e Ingreso) generan automáticamente asientos en `cont_asiento`.
- **Periodos**: Periodos contables (Nov 2025, Dic 2025, Ene 2026) generados y enlazados.

## 4. Notas de Desarrollo
- **Reinicio de Base de Datos**: Si Flyway reporta error de "checksum mismatch" en V1 al iniciar, es necesario limpiar la base de datos. Como estamos en entorno DEV, el script V2 se encarga de truncar tablas, pero un `DROP SCHEMA public CASCADE; CREATE SCHEMA public;` puede ser necesario si cambia la estructura de tablas.
- **Puerto**: Configurado en 8081 (asegurarse de liberar puerto si hay conflictos).
- **Formatos**: Precios y Cantidades manejan decimales correctamente.

## 5. Pendientes / Próximos Pasos
- **Reportes**: Validar que los reportes financieros (Balance, Resultados) cuadren con los asientos generados.
- **Impresión**: Formato de factura/ticket.
- **Dashboard**: Agregar widgets de KPI reales conectados a los datos históricos.
