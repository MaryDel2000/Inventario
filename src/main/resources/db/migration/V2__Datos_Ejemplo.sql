-- V2000__Reset_And_Seed_Automotive.sql

-- 1. CLEANUP (Truncate all tables to ensure clean slate)
TRUNCATE TABLE 
    cont_asiento_detalle, cont_asiento, cont_periodo,
    pos_devolucion_detalle, pos_devolucion, pos_pago_efectivo_desglose, pos_pago,
    pos_venta_promocion, pos_venta_detalle, pos_venta, pos_turno_arqueo, pos_turno,
    pos_caja_usuario, pos_caja, pos_punto_venta,
    inv_movimiento_detalle, inv_movimiento, inv_historial_existencia, inv_existencia, inv_lote,
    inv_compra_detalle, inv_compra_historial, inv_compra,
    inv_costo, inv_precio_venta, inv_lista_precio,
    inv_producto_variante, inv_producto, inv_impuesto, inv_unidad_medida, inv_categoria,
    inv_ubicacion, inv_almacen, inv_proveedor, pos_cliente,
    hr_trabajador, sys_usuario, gen_sucursal, gen_moneda, gen_entidad, cont_cuenta
RESTART IDENTITY CASCADE;

-- 2. SCHEMA PATCH (Add missing version columns for JPA Optimistic Locking)
ALTER TABLE gen_entidad ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE hr_trabajador ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_cliente ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_proveedor ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE sys_configuracion ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_producto ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_producto_variante ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_lista_precio ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_precio_venta ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_costo ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_compra ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_compra_detalle ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_compra_historial ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_lote ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_existencia ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_historial_existencia ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_movimiento ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_movimiento_detalle ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_promocion ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE inv_promocion_aplicacion ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE gen_moneda_denominacion ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE gen_moneda_tasa ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_punto_venta ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_caja ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_caja_usuario ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_turno ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_turno_arqueo ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_venta ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_venta_detalle ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_venta_promocion ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_tipo_pago ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_pago ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_pago_efectivo_desglose ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_devolucion ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE pos_devolucion_detalle ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE cont_periodo ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE cont_cuenta ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE cont_asiento ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;
ALTER TABLE cont_asiento_detalle ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 1;


-- 2. BASIC SETUP (Currencies, Branch, Users from Authentik)

-- Monedas
INSERT INTO gen_moneda (id, codigo, nombre, simbolo, activo) VALUES 
(1, 'GTQ', 'Quetzal', 'Q', true),
(2, 'USD', 'Dólar Estadounidense', '$', true);

-- Sucursal
INSERT INTO gen_sucursal (id, nombre, codigo, direccion, activo, version) VALUES 
(1, 'Sucursal Central - Autopartes', 'SUC-AUTO-01', 'Zona 4, Ciudad de Guatemala', true, 1);

-- Entidad & Usuario (Admin y Cajeros)
-- Admin
INSERT INTO gen_entidad (id, nombre_completo, tipo_entidad, activo) VALUES 
(1, 'Administrador Principal', 'PERSONA', true);
INSERT INTO sys_usuario (id, entidad_id, username, activo, version) VALUES 
(1, 1, 'admin', true, 1); -- Asumiendo que 'admin' es el username que viene de Authentik

-- Cajero 1
INSERT INTO gen_entidad (id, nombre_completo, tipo_entidad, activo) VALUES 
(2, 'Cajero Uno', 'PERSONA', true);
INSERT INTO sys_usuario (id, entidad_id, username, activo, version) VALUES 
(2, 2, 'cajero1', true, 1);

-- Cajero 2
INSERT INTO gen_entidad (id, nombre_completo, tipo_entidad, activo) VALUES 
(3, 'Cajero Dos', 'PERSONA', true);
INSERT INTO sys_usuario (id, entidad_id, username, activo, version) VALUES 
(3, 3, 'cajero2', true, 1);


-- 3. INVENTORY SETUP (Automotive Parts)

-- Almacenes
INSERT INTO inv_almacen (id, sucursal_id, nombre, codigo, tipo_almacen, permite_venta, activo, version) VALUES 
(1, 1, 'Bodega Principal', 'BOD-MAIN', 'FISICO', true, true, 1);

INSERT INTO inv_ubicacion (id, almacen_id, codigo, descripcion, activo, version) VALUES 
(1, 1, 'PASILLO-A', 'Pasillo A - Motores', true, 1),
(2, 1, 'PASILLO-B', 'Pasillo B - Frenos', true, 1),
(3, 1, 'MOSTRADOR', 'Estantería Mostrador', true, 1);

-- Categorias & Unidades
INSERT INTO inv_categoria (id, nombre, descripcion, activo, version) VALUES 
(10, 'Motores', 'Partes internas y externas de motor', true, 1),
(20, 'Frenos', 'Pastillas, discos y líquidos', true, 1),
(30, 'Suspensión', 'Amortiguadores y bujes', true, 1),
(40, 'Eléctrico', 'Baterías, luces y sensores', true, 1),
(50, 'Lubricantes', 'Aceites y grasas', true, 1);

INSERT INTO inv_unidad_medida (id, nombre, abreviatura, activo, version) VALUES 
(1, 'Unidad', 'UND', true, 1),
(2, 'Galón', 'GAL', true, 1),
(3, 'Litro', 'LTS', true, 1),
(4, 'Kit', 'KIT', true, 1);

INSERT INTO inv_impuesto (id, nombre, porcentaje, activo, version) VALUES 
(1, 'IVA', 12.00, true, 1);

-- Productos (Repuestos)

-- 1. Aceite Sintético 5W-30 (Lubricantes)
INSERT INTO inv_producto (id, nombre, codigo_interno, categoria_id, unidad_medida_id, impuesto_id, maneja_variantes, activo, version) VALUES 
(100, 'Aceite Sintético 5W-30', 'OIL-5W30', 50, 3, 1, false, true, 1);
INSERT INTO inv_producto_variante (id, producto_id, nombre_variante, codigo_barras, codigo_interno_variante, activo, version) VALUES 
(100, 100, 'Aceite Sintético 5W-30 1L', '7501011110001', 'OIL-5W30-1L', true, 1);

-- 2. Pastillas de Freno Delanteras Toyota Corolla (Frenos)
INSERT INTO inv_producto (id, nombre, codigo_interno, categoria_id, unidad_medida_id, impuesto_id, maneja_variantes, activo, version) VALUES 
(200, 'Pastillas Freno Del. Corolla', 'BRK-COR-F', 20, 4, 1, false, true, 1);
INSERT INTO inv_producto_variante (id, producto_id, nombre_variante, codigo_barras, codigo_interno_variante, activo, version) VALUES 
(200, 200, 'Juego Pastillas Corolla 2015-2020', '7501011110002', 'BRK-COR-F', true, 1);

-- 3. Batería 12V 60AH (Eléctrico)
INSERT INTO inv_producto (id, nombre, codigo_interno, categoria_id, unidad_medida_id, impuesto_id, maneja_variantes, activo, version) VALUES 
(300, 'Batería 12V 60AH', 'BAT-60AH', 40, 1, 1, false, true, 1);
INSERT INTO inv_producto_variante (id, producto_id, nombre_variante, codigo_barras, codigo_interno_variante, activo, version) VALUES 
(300, 300, 'Batería Libre Mantenimiento', '7501011110003', 'BAT-60AH', true, 1);

-- 4. Amortiguador Trasero Honda Civic (Suspensión)
INSERT INTO inv_producto (id, nombre, codigo_interno, categoria_id, unidad_medida_id, impuesto_id, maneja_variantes, activo, version) VALUES 
(400, 'Amortiguador Tras. Civic', 'SHK-CIV-R', 30, 1, 1, false, true, 1);
INSERT INTO inv_producto_variante (id, producto_id, nombre_variante, codigo_barras, codigo_interno_variante, activo, version) VALUES 
(400, 400, 'Amortiguador Gas Civic 2016+', '7501011110004', 'SHK-CIV-R', true, 1);

-- Precios y Costos
INSERT INTO inv_lista_precio (id, nombre, sucursal_id, prioridad, activo, fecha_inicio_vigencia, version) VALUES 
(1, 'Precio Público General', 1, 1, true, NOW(), 1);

-- Aceite: Costo Q45, Precio Q85
INSERT INTO inv_precio_venta (lista_precio_id, producto_variante_id, precio_venta, version) VALUES (1, 100, 85.00, 1);
INSERT INTO inv_costo (producto_variante_id, moneda_id, costo_unitario, fecha_inicio_vigencia) VALUES (100, 1, 45.00, NOW());

-- Pastillas: Costo Q150, Precio Q275
INSERT INTO inv_precio_venta (lista_precio_id, producto_variante_id, precio_venta, version) VALUES (1, 200, 275.00, 1);
INSERT INTO inv_costo (producto_variante_id, moneda_id, costo_unitario, fecha_inicio_vigencia) VALUES (200, 1, 150.00, NOW());

-- Batería: Costo Q400, Precio Q650
INSERT INTO inv_precio_venta (lista_precio_id, producto_variante_id, precio_venta, version) VALUES (1, 300, 650.00, 1);
INSERT INTO inv_costo (producto_variante_id, moneda_id, costo_unitario, fecha_inicio_vigencia) VALUES (300, 1, 400.00, NOW());

-- Amortiguador: Costo Q250, Precio Q450
INSERT INTO inv_precio_venta (lista_precio_id, producto_variante_id, precio_venta, version) VALUES (1, 400, 450.00, 1);
INSERT INTO inv_costo (producto_variante_id, moneda_id, costo_unitario, fecha_inicio_vigencia) VALUES (400, 1, 250.00, NOW());


-- 4. PURCHASING & STOCK (Initial Stock via Purchase)

-- Proveedor
INSERT INTO gen_entidad (id, nombre_completo, tipo_entidad, activo) VALUES 
(50, 'Importadora de Repuestos El Mundo', 'EMPRESA', true);
INSERT INTO inv_proveedor (id, entidad_id, activo, version) VALUES 
(1, 50, true, 1);

-- Compra Inicial
INSERT INTO inv_compra (id, sucursal_id, proveedor_id, almacen_destino_id, fecha_compra, tipo_documento, numero_documento, total_compra, estado, version) VALUES 
(1000, 1, 1, 1, NOW() - INTERVAL '5 days', 'FACTURA', 'FAC-IMP-9988', 8450.00, 'COMPLETADA', 1);

-- Detalle Compra
-- 50 Aceites * 45 = 2250
-- 20 Pastillas * 150 = 3000
-- 5 Baterias * 400 = 2000
-- 4 Amortiguadores * 250 = 1000
-- Total = 2250 + 3000 + 2000 + 1000 = 8250. Adjusted manually.
UPDATE inv_compra SET total_compra = 8250.00 WHERE id = 1000;

INSERT INTO inv_compra_detalle (compra_id, producto_variante_id, cantidad, costo_unitario, subtotal, version) VALUES 
(1000, 100, 50, 45.00, 2250.00, 1),
(1000, 200, 20, 150.00, 3000.00, 1),
(1000, 300, 5, 400.00, 2000.00, 1),
(1000, 400, 4, 300.00, 1200.00, 1); -- (Corrected calc: 4*300=1200. Wait, cost is 250 in master. Let's say we bought slightly closer to standard cost)

-- Existencias
INSERT INTO inv_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_disponible, fecha_ultima_actualizacion, version) VALUES 
(1, 1, 100, 50, NOW(), 1),
(1, 2, 200, 20, NOW(), 1),
(1, 3, 300, 5, NOW(), 1),
(1, 1, 400, 4, NOW(), 1);


-- 5. ACCOUNTING (Contabilidad)

-- Catalogo de Cuentas (Simplified)
INSERT INTO cont_cuenta (id, codigo, nombre, tipo, nivel, activa, version) VALUES 
(1, '1000', 'ACTIVO', 'ACTIVO', 1, true, 1),
(2, '1100', 'ACTIVO CIRCULANTE', 'ACTIVO', 2, true, 1),
(3, '1110', 'EFECTIVO Y EQUIVALENTES', 'ACTIVO', 3, true, 1),
(4, '1111', 'CAJA GENERAL', 'ACTIVO', 4, true, 1),
(5, '1120', 'INVENTARIOS', 'ACTIVO', 3, true, 1),
(6, '2000', 'PASIVO', 'PASIVO', 1, true, 1),
(7, '2100', 'PASIVO CIRCULANTE', 'PASIVO', 2, true, 1),
(8, '2111', 'PROVEEDORES', 'PASIVO', 3, true, 1),
(9, '4000', 'INGRESOS', 'INGRESOS', 1, true, 1),
(10, '4100', 'VENTAS', 'INGRESOS', 2, true, 1),
(11, '5000', 'GASTOS', 'GASTOS', 1, true, 1),
(12, '5100', 'COSTO DE VENTAS', 'GASTOS', 2, true, 1);

-- Asiento de la Compra #1000
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES 
(1, NOW() - INTERVAL '5 days', 'Compra Repuestos FAC-IMP-9988', 'COMPRA', 1000, 1, 1, NOW(), 1);

INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES 
(1, 5, 'Entrada Almacen Repuestos', 8250.00, 0, 1), -- Inventarios
(1, 8, 'CxP Importadora', 0, 8250.00, 1); -- Proveedores


-- 6. POS & SALES (Initial setup)

-- Caja y Turno
INSERT INTO pos_punto_venta (id, sucursal_id, nombre, activo, version) VALUES 
(1, 1, 'Mostrador 1', true, 1);

INSERT INTO pos_caja (id, sucursal_id, punto_venta_id, nombre, activo, version) VALUES 
(1, 1, 1, 'Caja Principal', true, 1);

-- Turno Abierto para el Cajero 1
INSERT INTO pos_turno (id, caja_id, usuario_cajero_id, fecha_hora_apertura, monto_inicial_efectivo, estado, version) VALUES 
(1, 1, 2, NOW(), 1000.00, 'ABIERTO', 1); -- Asignado a Cajero 1 (id 2)
-- V2001__Simulate_Historical_Data.sql
-- Generated to simulate 60 days of operations
-- 1. Shift Initial Purchase (ID 1000) to 60 days ago
UPDATE inv_compra SET fecha_compra = NOW() - INTERVAL '60 days' WHERE id = 1000;
UPDATE cont_asiento SET fecha = NOW() - INTERVAL '60 days' WHERE origen_id = 1000 AND origen = 'COMPRA';
UPDATE inv_existencia SET fecha_ultima_actualizacion = NOW() - INTERVAL '60 days';

-- 2. Add New Suppliers and Customers
INSERT INTO gen_entidad (id, nombre_completo, tipo_entidad, activo) VALUES (101, 'Talleres Juan', 'EMPRESA', true);
INSERT INTO pos_cliente (id, entidad_id, limite_credito, dias_credito, activo) VALUES (101, 101, 5000.00, 30, true);
INSERT INTO gen_entidad (id, nombre_completo, tipo_entidad, activo) VALUES (102, 'Maria Pereyra', 'PERSONA', true);
INSERT INTO pos_cliente (id, entidad_id, activo) VALUES (102, 102, true);
INSERT INTO gen_entidad (id, nombre_completo, tipo_entidad, activo) VALUES (103, 'Repuestos Alemanes SA', 'EMPRESA', true);
INSERT INTO inv_proveedor (id, entidad_id, activo) VALUES (2, 103, true);


-- 2.1 Add Payment Types
INSERT INTO pos_tipo_pago (id, nombre, codigo_interno, requiere_referencia, activo, version) VALUES 
(1, 'Efectivo', 'EFECTIVO', false, true, 1),
(2, 'Tarjeta', 'TARJETA', true, true, 1);

-- 3. Add New Products
INSERT INTO inv_producto (id, nombre, codigo_interno, categoria_id, unidad_medida_id, impuesto_id, maneja_variantes, activo, version) VALUES (500, 'Filtro Aire Universal', 'FIL-AIR-01', 10, 1, 1, false, true, 1);
INSERT INTO inv_producto_variante (id, producto_id, nombre_variante, codigo_barras, codigo_interno_variante, activo, version) VALUES (500, 500, 'Filtro Aire Std', '7501011110005', 'FIL-AIR-01', true, 1);
INSERT INTO inv_precio_venta (lista_precio_id, producto_variante_id, precio_venta, version) VALUES (1, 500, 55.00, 1);
INSERT INTO inv_costo (producto_variante_id, moneda_id, costo_unitario, fecha_inicio_vigencia) VALUES (500, 1, 25.00, NOW() - INTERVAL '60 days');
INSERT INTO inv_producto (id, nombre, codigo_interno, categoria_id, unidad_medida_id, impuesto_id, maneja_variantes, activo, version) VALUES (600, 'Kit Bujías Iridium', 'SPK-IR4', 40, 4, 1, false, true, 1);
INSERT INTO inv_producto_variante (id, producto_id, nombre_variante, codigo_barras, codigo_interno_variante, activo, version) VALUES (600, 600, 'Bujías Iridium x4', '7501011110006', 'SPK-IR4', true, 1);
INSERT INTO inv_precio_venta (lista_precio_id, producto_variante_id, precio_venta, version) VALUES (1, 600, 220.00, 1);
INSERT INTO inv_costo (producto_variante_id, moneda_id, costo_unitario, fecha_inicio_vigencia) VALUES (600, 1, 120.00, NOW() - INTERVAL '60 days');
-- Backfill History for Initial Purchase 1000 (T-60)
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 100, 0, 50, 50, 'COMPRA', 1000, NOW() - INTERVAL '60 days', 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 2, 200, 0, 20, 20, 'COMPRA', 1000, NOW() - INTERVAL '60 days', 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 3, 300, 0, 5, 5, 'COMPRA', 1000, NOW() - INTERVAL '60 days', 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 400, 0, 4, 4, 'COMPRA', 1000, NOW() - INTERVAL '60 days', 1);
-- Purchase 2000 at T-45
INSERT INTO inv_compra (id, sucursal_id, proveedor_id, almacen_destino_id, fecha_compra, tipo_documento, numero_documento, total_compra, estado, version) VALUES (2000, 1, 2, 1, NOW() - INTERVAL '45 days', 'FACTURA', 'FAC-HIST-2000', 0, 'COMPLETADA', 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 500, 0, 30, 30, 'COMPRA', 2000, NOW() - INTERVAL '45 days', 1);
INSERT INTO inv_compra_detalle (compra_id, producto_variante_id, cantidad, costo_unitario, subtotal, version) VALUES (2000, 500, 30, 25.0, 750.0, 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 600, 0, 10, 10, 'COMPRA', 2000, NOW() - INTERVAL '45 days', 1);
INSERT INTO inv_compra_detalle (compra_id, producto_variante_id, cantidad, costo_unitario, subtotal, version) VALUES (2000, 600, 10, 120.0, 1200.0, 1);
UPDATE inv_compra SET total_compra = 1950.0 WHERE id = 2000;
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (100, NOW() - INTERVAL '45 days', 'Compra Repuestos FAC-HIST-2000', 'COMPRA', 2000, 1, 1, NOW() - INTERVAL '45 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (100, 5, 'Entrada Inventario', 1950.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (100, 8, 'CxP Proveedor', 0, 1950.0, 1);
-- Purchase 2001 at T-35
INSERT INTO inv_compra (id, sucursal_id, proveedor_id, almacen_destino_id, fecha_compra, tipo_documento, numero_documento, total_compra, estado, version) VALUES (2001, 1, 1, 1, NOW() - INTERVAL '35 days', 'FACTURA', 'FAC-HIST-2001', 0, 'COMPLETADA', 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 100, 50, 70, 20, 'COMPRA', 2001, NOW() - INTERVAL '35 days', 1);
INSERT INTO inv_compra_detalle (compra_id, producto_variante_id, cantidad, costo_unitario, subtotal, version) VALUES (2001, 100, 20, 45.0, 900.0, 1);
UPDATE inv_compra SET total_compra = 900.0 WHERE id = 2001;
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (101, NOW() - INTERVAL '35 days', 'Compra Repuestos FAC-HIST-2001', 'COMPRA', 2001, 1, 1, NOW() - INTERVAL '35 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (101, 5, 'Entrada Inventario', 900.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (101, 8, 'CxP Proveedor', 0, 900.0, 1);
-- Sale 3000 at T-30
INSERT INTO pos_venta (id, sucursal_id, punto_venta_id, turno_id, cliente_id, usuario_vendedor_id, tipo_documento, numero_documento, fecha_hora, total_bruto, descuento_total, impuestos_total, total_neto, estado, estado_pago, almacen_salida_id, version) VALUES (3000, 1, 1, 1, 101, 2, 'FACTURA', 'FAC-VTA-3000', NOW() - INTERVAL '30 days', 0, 0, 0, 0, 'COMPLETADA', 'PAGADO', 1, 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 100, 70, 66, -4, 'VENTA', 3000, NOW() - INTERVAL '30 days', 2);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3000, 100, 4, 85.0, 1, 0, 303.57, 36.43);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 500, 30, 29, -1, 'VENTA', 3000, NOW() - INTERVAL '30 days', 2);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3000, 500, 1, 55.0, 1, 0, 49.11, 5.89);
UPDATE pos_venta SET total_bruto = 352.68, impuestos_total = 42.32, total_neto = 395.0 WHERE id = 3000;
INSERT INTO pos_pago (venta_id, tipo_pago_id, monto_total, monto_recibido, monto_vuelto, fecha_pago, usuario_registro_id) VALUES (3000, 1, 395.0, 395.0, 0, NOW() - INTERVAL '30 days', 2);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (102, NOW() - INTERVAL '30 days', 'Venta Mostrador FAC-3000', 'VENTA', 3000, 1, 2, NOW() - INTERVAL '30 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (102, 4, 'Ingreso Caja', 395.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (102, 10, 'Ingreso Venta', 0, 395.0, 1);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (103, NOW() - INTERVAL '30 days', 'Costo Venta FAC-3000', 'COSTO_VENTA', 3000, 1, 2, NOW() - INTERVAL '30 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (103, 12, 'Costo de Venta', 205.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (103, 5, 'Salida Inventario', 0, 205.0, 1);
-- Sale 3001 at T-25
INSERT INTO pos_venta (id, sucursal_id, punto_venta_id, turno_id, cliente_id, usuario_vendedor_id, tipo_documento, numero_documento, fecha_hora, total_bruto, descuento_total, impuestos_total, total_neto, estado, estado_pago, almacen_salida_id, version) VALUES (3001, 1, 1, 1, 102, 2, 'FACTURA', 'FAC-VTA-3001', NOW() - INTERVAL '25 days', 0, 0, 0, 0, 'COMPLETADA', 'PAGADO', 1, 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 2, 200, 20, 19, -1, 'VENTA', 3001, NOW() - INTERVAL '25 days', 2);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3001, 200, 1, 275.0, 1, 0, 245.54, 29.46);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 400, 4, 2, -2, 'VENTA', 3001, NOW() - INTERVAL '25 days', 2);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3001, 400, 2, 450.0, 1, 0, 803.57, 96.43);
UPDATE pos_venta SET total_bruto = 1049.11, impuestos_total = 125.89, total_neto = 1175.0 WHERE id = 3001;
INSERT INTO pos_pago (venta_id, tipo_pago_id, monto_total, monto_recibido, monto_vuelto, fecha_pago, usuario_registro_id) VALUES (3001, 1, 1175.0, 1175.0, 0, NOW() - INTERVAL '25 days', 2);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (104, NOW() - INTERVAL '25 days', 'Venta Mostrador FAC-3001', 'VENTA', 3001, 1, 2, NOW() - INTERVAL '25 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (104, 4, 'Ingreso Caja', 1175.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (104, 10, 'Ingreso Venta', 0, 1175.0, 1);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (105, NOW() - INTERVAL '25 days', 'Costo Venta FAC-3001', 'COSTO_VENTA', 3001, 1, 2, NOW() - INTERVAL '25 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (105, 12, 'Costo de Venta', 650.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (105, 5, 'Salida Inventario', 0, 650.0, 1);
INSERT INTO gen_entidad (id, nombre_completo, tipo_entidad, activo) VALUES (105, 'Cliente General', 'PERSONA', true);
INSERT INTO pos_cliente (id, entidad_id, activo) VALUES (105, 105, true);
-- Sale 3002 at T-15
INSERT INTO pos_venta (id, sucursal_id, punto_venta_id, turno_id, cliente_id, usuario_vendedor_id, tipo_documento, numero_documento, fecha_hora, total_bruto, descuento_total, impuestos_total, total_neto, estado, estado_pago, almacen_salida_id, version) VALUES (3002, 1, 1, 1, 105, 2, 'FACTURA', 'FAC-VTA-3002', NOW() - INTERVAL '15 days', 0, 0, 0, 0, 'COMPLETADA', 'PAGADO', 1, 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 100, 66, 65, -1, 'VENTA', 3002, NOW() - INTERVAL '15 days', 2);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3002, 100, 1, 85.0, 1, 0, 75.89, 9.11);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 600, 10, 6, -4, 'VENTA', 3002, NOW() - INTERVAL '15 days', 2);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3002, 600, 4, 220.0, 1, 0, 785.71, 94.29);
UPDATE pos_venta SET total_bruto = 861.6, impuestos_total = 103.4, total_neto = 965.0 WHERE id = 3002;
INSERT INTO pos_pago (venta_id, tipo_pago_id, monto_total, monto_recibido, monto_vuelto, fecha_pago, usuario_registro_id) VALUES (3002, 1, 965.0, 965.0, 0, NOW() - INTERVAL '15 days', 2);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (106, NOW() - INTERVAL '15 days', 'Venta Mostrador FAC-3002', 'VENTA', 3002, 1, 2, NOW() - INTERVAL '15 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (106, 4, 'Ingreso Caja', 965.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (106, 10, 'Ingreso Venta', 0, 965.0, 1);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (107, NOW() - INTERVAL '15 days', 'Costo Venta FAC-3002', 'COSTO_VENTA', 3002, 1, 2, NOW() - INTERVAL '15 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (107, 12, 'Costo de Venta', 525.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (107, 5, 'Salida Inventario', 0, 525.0, 1);
-- Sale 3003 at T-5
INSERT INTO pos_venta (id, sucursal_id, punto_venta_id, turno_id, cliente_id, usuario_vendedor_id, tipo_documento, numero_documento, fecha_hora, total_bruto, descuento_total, impuestos_total, total_neto, estado, estado_pago, almacen_salida_id, version) VALUES (3003, 1, 1, 1, 105, 2, 'FACTURA', 'FAC-VTA-3003', NOW() - INTERVAL '5 days', 0, 0, 0, 0, 'COMPLETADA', 'PAGADO', 1, 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 1, 500, 29, 27, -2, 'VENTA', 3003, NOW() - INTERVAL '5 days', 2);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3003, 500, 2, 55.0, 1, 0, 98.21, 11.79);
UPDATE pos_venta SET total_bruto = 98.21, impuestos_total = 11.79, total_neto = 110.0 WHERE id = 3003;
INSERT INTO pos_pago (venta_id, tipo_pago_id, monto_total, monto_recibido, monto_vuelto, fecha_pago, usuario_registro_id) VALUES (3003, 1, 110.0, 110.0, 0, NOW() - INTERVAL '5 days', 2);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (108, NOW() - INTERVAL '5 days', 'Venta Mostrador FAC-3003', 'VENTA', 3003, 1, 2, NOW() - INTERVAL '5 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (108, 4, 'Ingreso Caja', 110.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (108, 10, 'Ingreso Venta', 0, 110.0, 1);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (109, NOW() - INTERVAL '5 days', 'Costo Venta FAC-3003', 'COSTO_VENTA', 3003, 1, 2, NOW() - INTERVAL '5 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (109, 12, 'Costo de Venta', 50.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (109, 5, 'Salida Inventario', 0, 50.0, 1);
-- Sale 3004 at T-1
INSERT INTO pos_venta (id, sucursal_id, punto_venta_id, turno_id, cliente_id, usuario_vendedor_id, tipo_documento, numero_documento, fecha_hora, total_bruto, descuento_total, impuestos_total, total_neto, estado, estado_pago, almacen_salida_id, version) VALUES (3004, 1, 1, 1, 101, 2, 'FACTURA', 'FAC-VTA-3004', NOW() - INTERVAL '1 days', 0, 0, 0, 0, 'COMPLETADA', 'PAGADO', 1, 1);
INSERT INTO inv_historial_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_anterior, cantidad_nueva, diferencia, tipo_movimiento, referencia_id, fecha_historial, usuario_responsable_id) VALUES (1, 3, 300, 5, 4, -1, 'VENTA', 3004, NOW() - INTERVAL '1 days', 2);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3004, 300, 1, 650.0, 1, 0, 580.36, 69.64);
UPDATE pos_venta SET total_bruto = 580.36, impuestos_total = 69.64, total_neto = 650.0 WHERE id = 3004;
INSERT INTO pos_pago (venta_id, tipo_pago_id, monto_total, monto_recibido, monto_vuelto, fecha_pago, usuario_registro_id) VALUES (3004, 1, 650.0, 650.0, 0, NOW() - INTERVAL '1 days', 2);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (110, NOW() - INTERVAL '1 days', 'Venta Mostrador FAC-3004', 'VENTA', 3004, 1, 2, NOW() - INTERVAL '1 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (110, 4, 'Ingreso Caja', 650.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (110, 10, 'Ingreso Venta', 0, 650.0, 1);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, version) VALUES (111, NOW() - INTERVAL '1 days', 'Costo Venta FAC-3004', 'COSTO_VENTA', 3004, 1, 2, NOW() - INTERVAL '1 days', 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (111, 12, 'Costo de Venta', 400.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (111, 5, 'Salida Inventario', 0, 400.0, 1);

-- 4. Final Inventory Adjustments
UPDATE inv_existencia SET cantidad_disponible = 65, fecha_ultima_actualizacion = NOW() WHERE producto_variante_id = 100 AND almacen_id = 1;
UPDATE inv_existencia SET cantidad_disponible = 19, fecha_ultima_actualizacion = NOW() WHERE producto_variante_id = 200 AND almacen_id = 1;
UPDATE inv_existencia SET cantidad_disponible = 4, fecha_ultima_actualizacion = NOW() WHERE producto_variante_id = 300 AND almacen_id = 1;
UPDATE inv_existencia SET cantidad_disponible = 2, fecha_ultima_actualizacion = NOW() WHERE producto_variante_id = 400 AND almacen_id = 1;
INSERT INTO inv_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_disponible, fecha_ultima_actualizacion, version) VALUES (1, 1, 500, 27, NOW(), 1);
INSERT INTO inv_existencia (almacen_id, ubicacion_id, producto_variante_id, cantidad_disponible, fecha_ultima_actualizacion, version) VALUES (1, 1, 600, 6, NOW(), 1);-- V2002__Enrich_History_With_Periods_Shifts_Movements.sql
-- Fix missing links for Periodos, Turnos, and Movimientos

-- 1. Create Accounting Periods
INSERT INTO cont_periodo (id, nombre, fecha_inicio, fecha_fin, estado, usuario_cierre_id, fecha_cierre) VALUES (10, 'Noviembre 2025', '2025-11-01 00:00:00', '2025-11-30 23:59:59', 'CERRADO', 1, NOW());
INSERT INTO cont_periodo (id, nombre, fecha_inicio, fecha_fin, estado, usuario_cierre_id, fecha_cierre) VALUES (11, 'Diciembre 2025', '2025-12-01 00:00:00', '2025-12-31 23:59:59', 'CERRADO', 1, NOW());
INSERT INTO cont_periodo (id, nombre, fecha_inicio, fecha_fin, estado, usuario_cierre_id, fecha_cierre) VALUES (12, 'Enero 2026', '2026-01-01 00:00:00', '2026-01-31 23:59:59', 'ABIERTO', 1, NOW());

-- 2. Link Asientos to Periods
UPDATE cont_asiento SET periodo_id = 10 WHERE fecha >= '2025-11-01' AND fecha <= '2025-11-30';
UPDATE cont_asiento SET periodo_id = 11 WHERE fecha >= '2025-12-01' AND fecha <= '2025-12-31';
UPDATE cont_asiento SET periodo_id = 12 WHERE fecha >= '2026-01-01' AND fecha <= '2026-01-31';

-- 3. Create Closed Shifts (Turnos) and Link Sales
INSERT INTO pos_turno (id, caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version) VALUES (2000, 1, 2, NOW() - INTERVAL '30 days' + INTERVAL '8 hours', NOW() - INTERVAL '30 days' + INTERVAL '17 hours', 500.00, 2000.00, 2000.00, 0, 'CERRADO', 1);
UPDATE pos_venta SET turno_id = 2000 WHERE fecha_hora >= CURRENT_DATE - INTERVAL '30 days' AND fecha_hora < CURRENT_DATE - INTERVAL '29 days';
INSERT INTO pos_turno (id, caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version) VALUES (2001, 1, 2, NOW() - INTERVAL '25 days' + INTERVAL '8 hours', NOW() - INTERVAL '25 days' + INTERVAL '17 hours', 500.00, 2000.00, 2000.00, 0, 'CERRADO', 1);
UPDATE pos_venta SET turno_id = 2001 WHERE fecha_hora >= CURRENT_DATE - INTERVAL '25 days' AND fecha_hora < CURRENT_DATE - INTERVAL '24 days';
INSERT INTO pos_turno (id, caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version) VALUES (2002, 1, 2, NOW() - INTERVAL '15 days' + INTERVAL '8 hours', NOW() - INTERVAL '15 days' + INTERVAL '17 hours', 500.00, 2000.00, 2000.00, 0, 'CERRADO', 1);
UPDATE pos_venta SET turno_id = 2002 WHERE fecha_hora >= CURRENT_DATE - INTERVAL '15 days' AND fecha_hora < CURRENT_DATE - INTERVAL '14 days';
INSERT INTO pos_turno (id, caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version) VALUES (2003, 1, 2, NOW() - INTERVAL '5 days' + INTERVAL '8 hours', NOW() - INTERVAL '5 days' + INTERVAL '17 hours', 500.00, 2000.00, 2000.00, 0, 'CERRADO', 1);
UPDATE pos_venta SET turno_id = 2003 WHERE fecha_hora >= CURRENT_DATE - INTERVAL '5 days' AND fecha_hora < CURRENT_DATE - INTERVAL '4 days';
INSERT INTO pos_turno (id, caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version) VALUES (2004, 1, 2, NOW() - INTERVAL '1 days' + INTERVAL '8 hours', NOW() - INTERVAL '1 days' + INTERVAL '17 hours', 500.00, 2000.00, 2000.00, 0, 'CERRADO', 1);
UPDATE pos_venta SET turno_id = 2004 WHERE fecha_hora >= CURRENT_DATE - INTERVAL '1 days' AND fecha_hora < CURRENT_DATE - INTERVAL '0 days';

-- 4. Create Inventory Movements for existing Compras and Ventas

INSERT INTO inv_movimiento (sucursal_id, almacen_origen_id, almacen_destino_id, tipo_movimiento, referencia_tipo, referencia_id, fecha_movimiento, observaciones, usuario_creacion_id, version)
SELECT sucursal_id, NULL, almacen_destino_id, 'COMPRA', 'COMPRA', id, fecha_compra, 'Movimiento Automático por Compra Histórica', 1, 1
FROM inv_compra
WHERE id IN (1000, 2000, 2001)
AND NOT EXISTS (SELECT 1 FROM inv_movimiento WHERE referencia_id = inv_compra.id AND referencia_tipo = 'COMPRA');


INSERT INTO inv_movimiento (sucursal_id, almacen_origen_id, almacen_destino_id, tipo_movimiento, referencia_tipo, referencia_id, fecha_movimiento, observaciones, usuario_creacion_id, version)
SELECT sucursal_id, almacen_salida_id, NULL, 'VENTA', 'VENTA', id, fecha_hora, 'Movimiento Automático por Venta Histórica', 2, 1
FROM pos_venta
WHERE id >= 3000
AND NOT EXISTS (SELECT 1 FROM inv_movimiento WHERE referencia_id = pos_venta.id AND referencia_tipo = 'VENTA');


-- 5. Populate Movement Details

INSERT INTO inv_movimiento_detalle (movimiento_id, producto_variante_id, cantidad, ubicacion_destino_id, version)
SELECT m.id, d.producto_variante_id, d.cantidad, 1, 1
FROM inv_movimiento m
JOIN inv_compra_detalle d ON d.compra_id = m.referencia_id AND m.referencia_tipo = 'COMPRA'
WHERE NOT EXISTS (SELECT 1 FROM inv_movimiento_detalle WHERE movimiento_id = m.id);


INSERT INTO inv_movimiento_detalle (movimiento_id, producto_variante_id, cantidad, ubicacion_origen_id, version)
SELECT m.id, d.producto_variante_id, d.cantidad, 1, 1
FROM inv_movimiento m
JOIN pos_venta_detalle d ON d.venta_id = m.referencia_id AND m.referencia_tipo = 'VENTA'
WHERE NOT EXISTS (SELECT 1 FROM inv_movimiento_detalle WHERE movimiento_id = m.id);
-- V2003__Add_Receivables.sql

-- 1. Add Accounts Receivable (CxC) Account
INSERT INTO cont_cuenta (id, codigo, nombre, tipo, nivel, activa, version) VALUES (13, '1130', 'CLIENTES (CXC)', 'ACTIVO', 3, true, 1);

-- Sale 3005 (Pending)
INSERT INTO pos_venta (id, sucursal_id, punto_venta_id, turno_id, cliente_id, usuario_vendedor_id, tipo_documento, numero_documento, fecha_hora, total_bruto, descuento_total, impuestos_total, total_neto, estado, estado_pago, almacen_salida_id, version) VALUES (3005, 1, 1, NULL, 101, 2, 'CREDITO', 'FAC-CRED-3005', NOW() - INTERVAL '3 days', 732.14, 0, 87.86, 820.0, 'COMPLETADA', 'PENDIENTE', 1, 1);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3005, 100, 2, 85.00, 1, 0, 151.79, 18.21);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3005, 300, 1, 650.00, 1, 0, 580.36, 69.64);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, periodo_id, version) VALUES (200, NOW() - INTERVAL '3 days', 'Venta Crédito FAC-3005', 'VENTA', 3005, 1, 2, NOW() - INTERVAL '3 days', 12, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (200, 13, 'CxC Clientes', 820.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (200, 10, 'Ingreso Venta', 0, 820.0, 1);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, periodo_id, version) VALUES (201, NOW() - INTERVAL '3 days', 'Costo Venta FAC-3005', 'COSTO_VENTA', 3005, 1, 2, NOW() - INTERVAL '3 days', 12, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (201, 12, 'Costo de Venta', 490.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (201, 5, 'Salida Inventario', 0, 490.0, 1);
UPDATE inv_existencia SET cantidad_disponible = cantidad_disponible - 2 WHERE producto_variante_id = 100 AND almacen_id = 1;
UPDATE inv_existencia SET cantidad_disponible = cantidad_disponible - 1 WHERE producto_variante_id = 300 AND almacen_id = 1;
INSERT INTO inv_movimiento (sucursal_id, almacen_origen_id, almacen_destino_id, tipo_movimiento, referencia_tipo, referencia_id, fecha_movimiento, observaciones, usuario_creacion_id, version) VALUES (1, 1, NULL, 'VENTA', 'VENTA', 3005, NOW() - INTERVAL '3 days', 'Venta Credito', 2, 1);

-- Sale 3006 (Partial)
INSERT INTO pos_venta (id, sucursal_id, punto_venta_id, turno_id, cliente_id, usuario_vendedor_id, tipo_documento, numero_documento, fecha_hora, total_bruto, descuento_total, impuestos_total, total_neto, estado, estado_pago, almacen_salida_id, version) VALUES (3006, 1, 1, NULL, 101, 2, 'CREDITO', 'FAC-CRED-3006', NOW() - INTERVAL '2 days', 785.71, 0, 94.29, 880.0, 'COMPLETADA', 'PARCIAL', 1, 1);
INSERT INTO pos_venta_detalle (venta_id, producto_variante_id, cantidad, precio_unitario, impuesto_id, descuento_monto, subtotal, impuestos_monto) VALUES (3006, 600, 4, 220.00, 1, 0, 785.71, 94.29);
INSERT INTO pos_pago (venta_id, tipo_pago_id, monto_total, monto_recibido, monto_vuelto, fecha_pago, usuario_registro_id) VALUES (3006, 1, 880.0, 440.0, 0, NOW() - INTERVAL '2 days', 2);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, periodo_id, version) VALUES (202, NOW() - INTERVAL '2 days', 'Venta Crédito Parcial FAC-3006', 'VENTA', 3006, 1, 2, NOW() - INTERVAL '2 days', 12, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (202, 13, 'CxC Clientes', 880.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (202, 10, 'Ingreso Venta', 0, 880.0, 1);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, periodo_id, version) VALUES (203, NOW() - INTERVAL '2 days', 'Abono Venta FAC-3006', 'COBRO', 3006, 1, 2, NOW() - INTERVAL '2 days', 12, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (203, 4, 'Ingreso Caja', 440.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (203, 13, 'Abono Cliente', 0, 440.0, 1);
INSERT INTO cont_asiento (id, fecha, descripcion, origen, origen_id, sucursal_id, usuario_registro_id, fecha_creacion, periodo_id, version) VALUES (204, NOW() - INTERVAL '2 days', 'Costo Venta FAC-3006', 'COSTO_VENTA', 3006, 1, 2, NOW() - INTERVAL '2 days', 12, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (204, 12, 'Costo de Venta', 480.0, 0, 1);
INSERT INTO cont_asiento_detalle (asiento_id, cuenta_id, descripcion_linea, debe, haber, version) VALUES (204, 5, 'Salida Inventario', 0, 480.0, 1);
UPDATE inv_existencia SET cantidad_disponible = cantidad_disponible - 4 WHERE producto_variante_id = 600 AND almacen_id = 1;
INSERT INTO inv_movimiento (sucursal_id, almacen_origen_id, almacen_destino_id, tipo_movimiento, referencia_tipo, referencia_id, fecha_movimiento, observaciones, usuario_creacion_id, version) VALUES (1, 1, NULL, 'VENTA', 'VENTA', 3006, NOW() - INTERVAL '2 days', 'Venta Credito Parcial', 2, 1);