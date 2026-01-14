-- 1. Add moneda_id to inv_precio_venta
ALTER TABLE inv_precio_venta ADD COLUMN moneda_id BIGINT REFERENCES gen_moneda(id);

-- 2. Insert Default Currencies if not exist
INSERT INTO gen_moneda (codigo, nombre, simbolo, activo, version)
SELECT 'USD', 'Dólar Estadounidense', '$', true, 0
WHERE NOT EXISTS (SELECT 1 FROM gen_moneda WHERE codigo = 'USD');

INSERT INTO gen_moneda (codigo, nombre, simbolo, activo, version)
SELECT 'NIO', 'Córdoba Oro', 'C$', true, 0
WHERE NOT EXISTS (SELECT 1 FROM gen_moneda WHERE codigo = 'NIO');
