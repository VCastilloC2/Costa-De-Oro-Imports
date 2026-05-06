SET SQL_SAFE_UPDATES = 0;

-- =========================
-- 1. GENERAR 10.000 COMPRAS
-- =========================
INSERT INTO compra (iva, subtotal, total, fecha, usuario_id, cupon_descuento, estado, metodo_pago)
SELECT
    0.19,
    0, -- luego se calcula
    0,
    DATE_ADD('2026-01-01', INTERVAL FLOOR(RAND()*151) DAY),
    FLOOR(1 + (RAND()*2000)), -- usuarios random
    ELT(FLOOR(1 + RAND()*8),
        NULL,
        'VERANO2026',
        'BLACKFRIDAY',
        'PROMO-2026',
        'DESCUENTO26',
        'VERANO2026'
    ),
    ELT(FLOOR(1 + RAND()*4),
        'PAGADO',
        'CANCELADO',
        'PENDIENTE',
        'RECHAZADO'
    ),
    ELT(FLOOR(1 + RAND()*3),
        'TARJETA_CREDITO',
        'TARJETA_DEBITO',
        'PSE'
    )
FROM (
         SELECT 1 FROM dual
         UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     ) t1,
     (
         SELECT 1 FROM dual
         UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     ) t2,
     (
         SELECT 1 FROM dual
         UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     ) t3,
     (
         SELECT 1 FROM dual
         UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     ) t4,
     (
         SELECT 1 FROM dual
         UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     ) t5
    LIMIT 10000;


-- =========================
-- 2. GENERAR 10.000 DETALLES
-- =========================
INSERT INTO detalle_venta (cantidad, compra_id, producto_id, precio_unitario, subtotal)
SELECT
    FLOOR(1 + RAND()*3),
    c.compra_id,
    FLOOR(1 + RAND()*70), -- productos random
    0,
    0
FROM compra c
WHERE YEAR(c.fecha) = 2026
    LIMIT 10000;


-- =========================
-- 3. AJUSTAR CANTIDADES
-- =========================
UPDATE detalle_venta dv
    JOIN compra c ON dv.compra_id = c.compra_id
    SET dv.cantidad = FLOOR(1 + (RAND()*2))
WHERE YEAR(c.fecha) = 2026;


-- =========================
-- 4. RESET TOTAL
-- =========================
UPDATE compra
SET subtotal = 0,
    total = 0
WHERE YEAR(fecha) = 2026;


-- =========================
-- 5. CAMBIAR ESTADOS RANDOM
-- =========================
UPDATE compra
SET estado = CASE
                 WHEN RAND() < 0.30 THEN 'PAGADO'
                 ELSE ELT(FLOOR(1 + RAND() * 3), 'CANCELADO','PENDIENTE','RECHAZADO')
    END
WHERE YEAR(fecha) = 2026;


-- =========================
-- 6. ACTUALIZAR DETALLE
-- =========================
UPDATE detalle_venta dv
    INNER JOIN producto p ON dv.producto_id = p.producto_id
    INNER JOIN compra c ON dv.compra_id = c.compra_id
    SET dv.precio_unitario = p.precio,
        dv.subtotal = dv.cantidad * p.precio
WHERE YEAR(c.fecha) = 2026;


-- =========================
-- 7. CALCULAR TOTALES
-- =========================
UPDATE compra c
    JOIN (
    SELECT dv.compra_id,
    COALESCE(SUM(dv.subtotal), 0) AS suma_subtotal
    FROM detalle_venta dv
    JOIN compra c2 ON dv.compra_id = c2.compra_id
    WHERE YEAR(c2.fecha) = 2026
    GROUP BY dv.compra_id
    ) AS dv_sum ON c.compra_id = dv_sum.compra_id
    SET
        c.subtotal = dv_sum.suma_subtotal,
        c.total = dv_sum.suma_subtotal * 1.19
WHERE YEAR(c.fecha) = 2026;

SET SQL_SAFE_UPDATES = 1;