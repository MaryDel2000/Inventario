DO $$
DECLARE
    v_caja_id bigint;
    v_user_id bigint;
BEGIN
    -- 1. Ensure a User exists
    INSERT INTO sys_usuario (username, activo, version)
    VALUES ('admin', true, 1)
    ON CONFLICT DO NOTHING;
    
    -- Select the user (either the one we just made or an existing one)
    SELECT id INTO v_user_id FROM sys_usuario LIMIT 1;

    -- 2. Ensure a Box exists
    INSERT INTO pos_caja (nombre, activo, version)
    VALUES ('Caja Principal', true, 1)
    ON CONFLICT DO NOTHING;

    SELECT id INTO v_caja_id FROM pos_caja LIMIT 1;

    -- 3. Insert Shifts if we have both user and box
    IF v_user_id IS NOT NULL AND v_caja_id IS NOT NULL THEN
        
        -- Delete overlapping shifts to avoid duplicates if re-running logic manually
        DELETE FROM pos_turno WHERE fecha_hora_apertura BETWEEN '2026-01-01' AND '2026-02-01';

        -- Jan 5
        INSERT INTO pos_turno (caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version)
        VALUES (v_caja_id, v_user_id, '2026-01-05 08:00:00', '2026-01-05 17:05:00', 300.00, 300.00, 300.00, 0.00, 'CERRADO', 1);

        -- Jan 6
        INSERT INTO pos_turno (caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version)
        VALUES (v_caja_id, v_user_id, '2026-01-06 08:10:00', '2026-01-06 17:00:00', 300.00, 295.00, 300.00, -5.00, 'CERRADO', 1);

        -- Jan 7
        INSERT INTO pos_turno (caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version)
        VALUES (v_caja_id, v_user_id, '2026-01-07 08:00:00', '2026-01-07 16:55:00', 350.00, 352.00, 350.00, 2.00, 'CERRADO', 1);

        -- Jan 8
        INSERT INTO pos_turno (caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version)
        VALUES (v_caja_id, v_user_id, '2026-01-08 08:05:00', '2026-01-08 17:10:00', 300.00, 300.00, 300.00, 0.00, 'CERRADO', 1);

        -- Jan 9
        INSERT INTO pos_turno (caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version)
        VALUES (v_caja_id, v_user_id, '2026-01-09 08:00:00', '2026-01-09 13:30:00', 250.00, 250.00, 250.00, 0.00, 'CERRADO', 1);

        -- Jan 10
        INSERT INTO pos_turno (caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version)
        VALUES (v_caja_id, v_user_id, '2026-01-10 08:30:00', '2026-01-10 14:00:00', 250.00, 250.00, 250.00, 0.00, 'CERRADO', 1);

        -- Jan 11 (Sun)
        INSERT INTO pos_turno (caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version)
        VALUES (v_caja_id, v_user_id, '2026-01-11 09:00:00', '2026-01-11 12:00:00', 100.00, 100.00, 100.00, 0.00, 'CERRADO', 1);

        -- Jan 12 (Current Open)
        INSERT INTO pos_turno (caja_id, usuario_cajero_id, fecha_hora_apertura, fecha_hora_cierre, monto_inicial_efectivo, monto_final_efectivo_declarado, monto_final_efectivo_calculado, diferencia, estado, version)
        VALUES (v_caja_id, v_user_id, '2026-01-12 08:15:00', NULL, 300.00, NULL, 0.00, NULL, 'ABIERTO', 1);

    END IF;
END $$;
