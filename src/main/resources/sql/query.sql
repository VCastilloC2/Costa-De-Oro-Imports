use db_beer;

-- NO TOCAR POR NADA DEL MUNDO

select * from db_beer.empresa;

select * from db_beer.rol;

select * from db_beer.usuario;

select * from db_beer.producto;

select * from db_beer.categoria;

select * from db_beer.sub_categorias;

select * from db_beer.compra;

select * from db_beer.detalle_venta;

select * from db_beer.historia;

select * from db_beer.comentario;

select * from db_beer.peticiones;

select * from db_beer.factura_proveedor;

SELECT
    p.producto_id,
    p.nombre,
    p.marca,
    p.precio,
    COALESCE(SUM(dv.cantidad), 0) AS total_vendido
FROM producto p
LEFT JOIN detalle_venta dv ON dv.producto_id = p.producto_id
GROUP BY p.producto_id, p.nombre, p.marca, p.precio
ORDER BY total_vendido DESC;

-- deshabilitar
UPDATE usuario
SET is_enabled = true
WHERE usuario_id = 2;

-- invalidar credenciales
UPDATE usuario
SET credentials_non_expired = true
WHERE usuario_id = 2;

-- bloquear cuenta
UPDATE usuario
SET account_non_locked = false
WHERE usuario_id = 2;

-- cuenta expirada
UPDATE usuario
SET account_non_expired = false
WHERE usuario_id = 2;

-- no debe devolver nada, si devuelve algo hay una insonsitencia en la base de datos en compra y detalle_venta
SELECT
    c.compra_id,
    c.subtotal as subtotal_compra,
    SUM(dv.subtotal) as suma_detalles,
    c.total as total_compra,
    (SUM(dv.subtotal) * 1.19) as total_calculado
FROM compra c
LEFT JOIN detalle_venta dv ON c.compra_id = dv.compra_id
GROUP BY c.compra_id, c.subtotal, c.total
HAVING ABS(c.subtotal - SUM(dv.subtotal)) > 1;



Select * from db_beer.producto where categoria_id = 1;


-- Proyeccion de ventas totales (consulta)
SELECT
c.total AS venta_total,
YEAR(c.fecha) AS año,
MONTH(c.fecha) AS mes,
DAY(c.fecha) AS dia,
DAYOFWEEK(c.fecha) AS dia_semana,
COUNT(dv.detalle_venta_id) AS cantidad_productos,
SUM(dv.cantidad) AS total_unidades,
AVG(dv.precio_unitario) AS precio_promedio,
u.usuario_id,
CASE
	WHEN c.metodo_pago = 'TARJETA_CREDITO' THEN 1
	WHEN c.metodo_pago = 'TARJETA_DEBITO' THEN 2
	WHEN c.metodo_pago = 'MERCADO_PAGO' THEN 3
	ELSE 4
END AS metodo_pago_num,
CASE
	WHEN MONTH(c.fecha) IN (12,1,2) THEN 1
	WHEN MONTH(c.fecha) IN (6,7) THEN 2
	ELSE 3
END AS temporada

FROM compra c
INNER JOIN detalle_venta dv ON c.compra_id = dv.compra_id
INNER JOIN usuario u ON c.usuario_id = u.usuario_id
WHERE c.estado = 'PAGADO'
AND c.fecha >= DATE_SUB(NOW(), INTERVAL 2 YEAR)
GROUP BY c.compra_id
ORDER BY c.fecha;

-- Prediccion de demanda (Demanda de Productos por Mes)
SELECT
    p.producto_id,
    p.nombre AS nombre_producto,
    p.precio,
    p.marca,
    c.nombre AS categoria,

    YEAR(cmp.fecha) AS año,
    MONTH(cmp.fecha) AS mes,

    SUM(dv.cantidad) AS unidades_vendidas,
    AVG(dv.precio_unitario) AS precio_promedio_venta,
    COUNT(DISTINCT cmp.compra_id) AS frecuencia_ventas,
    p.stock AS stock_actual,

    (
        SELECT SUM(dv2.cantidad)
        FROM detalle_venta dv2
        JOIN compra cmp2 ON dv2.compra_id = cmp2.compra_id
        WHERE dv2.producto_id = p.producto_id
          AND cmp2.fecha >= DATE_SUB(
                DATE_FORMAT(MIN(cmp.fecha), '%Y-%m-01'),
                INTERVAL 3 MONTH
              )
          AND cmp2.fecha < DATE_ADD(
                DATE_FORMAT(MIN(cmp.fecha), '%Y-%m-01'),
                INTERVAL 1 MONTH
              )
    ) AS ventas_ultimos_3meses

FROM producto p
INNER JOIN detalle_venta dv ON p.producto_id = dv.producto_id
INNER JOIN compra cmp ON dv.compra_id = cmp.compra_id
INNER JOIN categoria c ON p.categoria_id = c.categoria_id

WHERE cmp.estado = 'PAGADO'
  AND cmp.fecha >= DATE_SUB(NOW(), INTERVAL 1 YEAR)

GROUP BY
    p.producto_id,
    YEAR(cmp.fecha),
    MONTH(cmp.fecha)

ORDER BY
    p.producto_id, año, mes;

-- Predicción de Compras a Proveedores para el Próximo Año
SELECT
    p.producto_id,
    p.nombre AS producto,
    u.nombres AS proveedor,

    SUM(dv.cantidad) AS ventas_anuales,

    p.stock AS stock_actual,

    (SUM(dv.cantidad) / NULLIF(p.stock, 0)) AS rotacion,

    (SUM(dv.cantidad) * 1.15 - p.stock) AS unidades_a_comprar

FROM producto p
INNER JOIN detalle_venta dv ON p.producto_id = dv.producto_id
INNER JOIN compra cmp ON dv.compra_id = cmp.compra_id
INNER JOIN usuario u ON p.usuario_id = u.usuario_id
WHERE cmp.estado = 'PAGADO'
  AND cmp.fecha >= DATE_SUB(NOW(), INTERVAL 1 YEAR)
GROUP BY p.producto_id, u.usuario_id
HAVING ventas_anuales > 0
  AND unidades_a_comprar > 0;


-- Insertar factura_proveedor
CREATE TEMPORARY TABLE temp_facturas_proveedor (
    temp_id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    fecha_emision DATE,
    numero_factura VARCHAR(255)
);

SET @seq = 0;
SET @seq2 = 375;
SET @seq3 = 750;
SET @seq4 = 1125;
SET @seq5 = 1500;
SET @seq6 = 1875;
SET @seq7 = 2250;
SET @seq8 = 2625;

INSERT INTO temp_facturas_proveedor (usuario_id, fecha_emision, numero_factura)
SELECT 2104, DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND() * 1825) DAY), CONCAT('FAC-', LPAD(@seq := @seq + 1, 4, '0'))
FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t1
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t2
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t3
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t4
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t5
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as t6
LIMIT 375;

INSERT INTO temp_facturas_proveedor (usuario_id, fecha_emision, numero_factura)
SELECT 2105, DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND() * 1825) DAY), CONCAT('FAC-', LPAD(@seq2 := @seq2 + 1, 4, '0'))
FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t1
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t2
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t3
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t4
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t5
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as t6
LIMIT 375;

INSERT INTO temp_facturas_proveedor (usuario_id, fecha_emision, numero_factura)
SELECT 2106, DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND() * 1825) DAY), CONCAT('FAC-', LPAD(@seq3 := @seq3 + 1, 4, '0'))
FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t1
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t2
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t3
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t4
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t5
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as t6
LIMIT 375;

INSERT INTO temp_facturas_proveedor (usuario_id, fecha_emision, numero_factura)
SELECT 2107, DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND() * 1825) DAY), CONCAT('FAC-', LPAD(@seq4 := @seq4 + 1, 4, '0'))
FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t1
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t2
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t3
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t4
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t5
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as t6
LIMIT 375;

INSERT INTO temp_facturas_proveedor (usuario_id, fecha_emision, numero_factura)
SELECT 2108, DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND() * 1825) DAY), CONCAT('FAC-', LPAD(@seq5 := @seq5 + 1, 4, '0'))
FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t1
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t2
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t3
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t4
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t5
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as t6
LIMIT 375;

INSERT INTO temp_facturas_proveedor (usuario_id, fecha_emision, numero_factura)
SELECT 2109, DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND() * 1825) DAY), CONCAT('FAC-', LPAD(@seq6 := @seq6 + 1, 4, '0'))
FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t1
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t2
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t3
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t4
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t5
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as t6
LIMIT 375;

INSERT INTO temp_facturas_proveedor (usuario_id, fecha_emision, numero_factura)
SELECT 2111, DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND() * 1825) DAY), CONCAT('FAC-', LPAD(@seq7 := @seq7 + 1, 4, '0'))
FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t1
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t2
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t3
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t4
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t5
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as t6
LIMIT 375;

INSERT INTO temp_facturas_proveedor (usuario_id, fecha_emision, numero_factura)
SELECT 2112, DATE_ADD('2020-01-01', INTERVAL FLOOR(RAND() * 1825) DAY), CONCAT('FAC-', LPAD(@seq8 := @seq8 + 1, 4, '0'))
FROM (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t1
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t2
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t3
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t4
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t5
CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as t6
LIMIT 375;

INSERT INTO `factura_proveedor` (`activo`, `fecha_emision`, `total`, `fecha_registro`, `usuario_id`, `numero_factura`)
SELECT
    1 as activo,
    fecha_emision,
    0 as total,
    fecha_emision as fecha_registro,
    usuario_id,
    numero_factura
FROM temp_facturas_proveedor
ORDER BY fecha_emision;

DROP TEMPORARY TABLE temp_facturas_proveedor;


-- Insertar detalle_factura
CREATE TEMPORARY TABLE temp_detalles_factura (
    temp_id INT AUTO_INCREMENT PRIMARY KEY,
    factura_id BIGINT,
    producto_id BIGINT,
    cantidad INT,
    precio_compra INT,
    subtotal INT
);

INSERT INTO temp_detalles_factura (factura_id, producto_id, cantidad, precio_compra, subtotal)
SELECT
    fp.factura_id,
    p.producto_id,
    FLOOR(2 + (RAND() * 4)) as cantidad,
    CASE
        WHEN p.producto_id BETWEEN 1 AND 14 THEN FLOOR(p.precio * 0.6)
        WHEN p.producto_id BETWEEN 15 AND 22 THEN FLOOR(p.precio * 0.65)
        WHEN p.producto_id BETWEEN 23 AND 29 THEN FLOOR(p.precio * 0.7)
        WHEN p.producto_id BETWEEN 30 AND 35 THEN FLOOR(p.precio * 0.6)
        WHEN p.producto_id BETWEEN 36 AND 44 THEN FLOOR(p.precio * 0.65)
        WHEN p.producto_id BETWEEN 45 AND 50 THEN FLOOR(p.precio * 0.7)
        WHEN p.producto_id BETWEEN 51 AND 65 THEN FLOOR(p.precio * 0.55)
        WHEN p.producto_id BETWEEN 66 AND 70 THEN FLOOR(p.precio * 0.6)
        ELSE FLOOR(p.precio * 0.7)
    END as precio_compra,
    0 as subtotal
FROM factura_proveedor fp
JOIN producto p ON fp.usuario_id = p.usuario_id
CROSS JOIN (SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) as multiples
WHERE RAND() < 0.6
GROUP BY fp.factura_id, p.producto_id  -- Esto evita duplicados
ORDER BY RAND()
LIMIT 9000;

-- Actualizar subtotales con WHERE que use KEY column
UPDATE temp_detalles_factura
SET subtotal = cantidad * precio_compra
WHERE temp_id > 0;

-- Insertar evitando duplicados que puedan existir en la tabla destino
INSERT INTO `detalle_factura` (`cantidad`, `precio_compra`, `subtotal`, `factura_id`, `producto_id`)
SELECT
    t.cantidad,
    t.precio_compra,
    t.subtotal,
    t.factura_id,
    t.producto_id
FROM temp_detalles_factura t
LEFT JOIN detalle_factura df ON t.factura_id = df.factura_id AND t.producto_id = df.producto_id
WHERE df.detalle_factura_id IS NULL;  -- Solo inserta si no existe

-- Actualizar totales en factura_proveedor
UPDATE factura_proveedor fp
JOIN (
    SELECT factura_id, SUM(subtotal) as total_calculado
    FROM detalle_factura
    GROUP BY factura_id
) as calculos ON fp.factura_id = calculos.factura_id
SET fp.total = calculos.total_calculado
WHERE fp.factura_id > 0;

DROP TEMPORARY TABLE temp_detalles_factura;