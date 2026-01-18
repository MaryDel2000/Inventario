
-- Fix sequences that might be out of sync due to manual inserts/seeding
SELECT setval(pg_get_serial_sequence('gen_entidad', 'id'), (SELECT MAX(id) FROM gen_entidad));
SELECT setval(pg_get_serial_sequence('sys_usuario', 'id'), (SELECT MAX(id) FROM sys_usuario));
SELECT setval(pg_get_serial_sequence('hr_trabajador', 'id'), (SELECT MAX(id) FROM hr_trabajador));
SELECT setval(pg_get_serial_sequence('pos_cliente', 'id'), (SELECT MAX(id) FROM pos_cliente));
SELECT setval(pg_get_serial_sequence('inv_proveedor', 'id'), (SELECT MAX(id) FROM inv_proveedor));
SELECT setval(pg_get_serial_sequence('gen_sucursal', 'id'), (SELECT MAX(id) FROM gen_sucursal));
SELECT setval(pg_get_serial_sequence('inv_almacen', 'id'), (SELECT MAX(id) FROM inv_almacen));
SELECT setval(pg_get_serial_sequence('inv_ubicacion', 'id'), (SELECT MAX(id) FROM inv_ubicacion));
SELECT setval(pg_get_serial_sequence('inv_categoria', 'id'), (SELECT MAX(id) FROM inv_categoria));
SELECT setval(pg_get_serial_sequence('inv_unidad_medida', 'id'), (SELECT MAX(id) FROM inv_unidad_medida));
SELECT setval(pg_get_serial_sequence('inv_producto', 'id'), (SELECT MAX(id) FROM inv_producto));
SELECT setval(pg_get_serial_sequence('inv_producto_variante', 'id'), (SELECT MAX(id) FROM inv_producto_variante));
SELECT setval(pg_get_serial_sequence('inv_lista_precio', 'id'), (SELECT MAX(id) FROM inv_lista_precio));
SELECT setval(pg_get_serial_sequence('inv_precio_venta', 'id'), (SELECT MAX(id) FROM inv_precio_venta));
SELECT setval(pg_get_serial_sequence('inv_compra', 'id'), (SELECT MAX(id) FROM inv_compra));
SELECT setval(pg_get_serial_sequence('inv_lote', 'id'), (SELECT MAX(id) FROM inv_lote));
SELECT setval(pg_get_serial_sequence('pos_turno', 'id'), (SELECT MAX(id) FROM pos_turno));
SELECT setval(pg_get_serial_sequence('pos_venta', 'id'), (SELECT MAX(id) FROM pos_venta));
SELECT setval(pg_get_serial_sequence('cont_periodo', 'id'), (SELECT MAX(id) FROM cont_periodo));
SELECT setval(pg_get_serial_sequence('cont_asiento', 'id'), (SELECT MAX(id) FROM cont_asiento));
