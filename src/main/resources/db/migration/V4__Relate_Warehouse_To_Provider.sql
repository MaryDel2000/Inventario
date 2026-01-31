ALTER TABLE inv_almacen ADD COLUMN proveedor_id BIGINT;
ALTER TABLE inv_almacen ADD CONSTRAINT fk_almacen_proveedor_new FOREIGN KEY (proveedor_id) REFERENCES inv_proveedor(id);

-- Drop the previous VARCHAR column if exists (from V3) to replace with FK
ALTER TABLE inv_compra DROP COLUMN IF EXISTS almacen_proveedor;
ALTER TABLE inv_compra ADD COLUMN almacen_proveedor_id BIGINT;
ALTER TABLE inv_compra ADD CONSTRAINT fk_compra_almacen_proveedor FOREIGN KEY (almacen_proveedor_id) REFERENCES inv_almacen(id);
