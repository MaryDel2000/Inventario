-- Insert GenSucursal
INSERT INTO gen_sucursal (id, nombre, codigo, direccion, activo, version) VALUES
(1, 'Sucursal Central', 'SUC001', 'Av. Principal 123', true, 0)
ON CONFLICT (id) DO NOTHING;

-- Insert InvAlmacen
INSERT INTO inv_almacen (id, sucursal_id, nombre, codigo, tipo_almacen, direccion, activo, version) VALUES
(1, 1, 'Almacén Principal', 'ALM-MAIN', 'PRINCIPAL', 'Zona de Carga A', true, 0),
(2, 1, 'Almacén Secundario', 'ALM-SEC', 'SECUNDARIO', 'Sótano 1', true, 0)
ON CONFLICT (id) DO NOTHING;

-- Insert InvUbicacion
INSERT INTO inv_ubicacion (id, almacen_id, codigo, descripcion, activo, version) VALUES
(1, 1, 'A-01-01', 'Estante A, Nivel 1, Posición 1', true, 0),
(2, 1, 'A-01-02', 'Estante A, Nivel 1, Posición 2', true, 0),
(3, 2, 'B-01-01', 'Estante B, Nivel 1, Posición 1', true, 0)
ON CONFLICT (id) DO NOTHING;

-- Insert InvCategoria
INSERT INTO inv_categoria (id, nombre, descripcion, activo, version) VALUES
(1, 'Electrónica', 'Dispositivos y accesorios electrónicos', true, 0),
(2, 'Papelería', 'Material de oficina y escolar', true, 0),
(3, 'Mobiliario', 'Muebles y decoración', true, 0)
ON CONFLICT (id) DO NOTHING;

-- Insert InvUnidadMedida
INSERT INTO inv_unidad_medida (id, nombre, abreviatura, activo, version) VALUES
(1, 'Unidad', 'UND', true, 0),
(2, 'Caja', 'CJA', true, 0),
(3, 'Metro', 'MTR', true, 0)
ON CONFLICT (id) DO NOTHING;

-- Insert InvProducto
INSERT INTO inv_producto (id, nombre, codigo_interno, categoria_id, unidad_medida_id, descripcion, activo, version, maneja_variantes) VALUES
(1, 'Laptop Dell Latitude', 'DELL-5420', 1, 1, 'Laptop profesional 14 pulgadas', true, 0, true),
(2, 'Monitor Samsung 24"', 'SAM-24', 1, 1, 'Monitor LED Full HD', true, 0, false),
(3, 'Silla Ergonómica', 'SILLA-ERG', 3, 1, 'Silla de oficina negra', true, 0, false),
(4, 'Papel Bond A4', 'PAPEL-A4', 2, 2, 'Caja de 500 hojas', true, 0, false)
ON CONFLICT (id) DO NOTHING;

-- Insert InvProductoVariante
INSERT INTO inv_producto_variante (id, producto_id, nombre_variante, codigo_barras, codigo_interno_variante, activo, version) VALUES
(1, 1, 'Laptop Dell Latitude - 8GB RAM', '884116385412', 'DELL-5420-8G', true, 0),
(2, 1, 'Laptop Dell Latitude - 16GB RAM', '884116385429', 'DELL-5420-16G', true, 0),
(3, 2, 'Monitor Samsung 24"', '880609097775', 'SAM-24', true, 0),
(4, 3, 'Silla Ergonómica', 'SILLA-001', 'SILLA-ERG', true, 0),
(5, 4, 'Papel Bond A4', 'PAPEL-001', 'PAPEL-A4', true, 0)
ON CONFLICT (id) DO NOTHING;

-- Insert InvLote (Optional, for products that active ManejaLotes if we implemented logic, but safe to add)
INSERT INTO inv_lote (id, producto_variante_id, codigo_lote, fecha_caducidad, observaciones, version) VALUES
(1, 1, 'LOTE-2023-001', '2026-12-31', 'Lote importación Enero', 0),
(2, 5, 'LOTE-PAPEL-01', NULL, 'Sin caducidad', 0)
ON CONFLICT (id) DO NOTHING;

-- Insert InvMovimiento (Initial Stock)
INSERT INTO inv_movimiento (id, tipo_movimiento, fecha_movimiento, observaciones, almacen_destino_id, version) VALUES
(1, 'ENTRADA', NOW(), 'Inventario Inicial', 1, 0)
ON CONFLICT (id) DO NOTHING;

-- Insert InvMovimientoDetalle
INSERT INTO inv_movimiento_detalle (id, movimiento_id, producto_variante_id, cantidad, ubicacion_destino_id, lote_id, version) VALUES
(1, 1, 1, 10, 1, 1, 0),
(2, 1, 2, 5, 1, NULL, 0),
(3, 1, 3, 20, 2, NULL, 0),
(4, 1, 5, 50, 2, 2, 0)
ON CONFLICT (id) DO NOTHING;
