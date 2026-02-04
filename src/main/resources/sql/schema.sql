USE db_beer;
DROP TABLE IF EXISTS detalle_venta, compra, usuario, producto, sub_categorias, categoria, historia, comentario, rol, empresa, peticiones, factura_proveedor, detalle_factura;

-- TABLA ROL
CREATE TABLE `rol` (
    `rol_id` bigint NOT NULL AUTO_INCREMENT,
    `rol` enum('ADMIN','INVITADO','PERSONA_CONTACTO','PERSONA_JURIDICA','PERSONA_NATURAL', 'PROVEEDOR') DEFAULT NULL,
    PRIMARY KEY (`rol_id`),
    UNIQUE KEY `UKgidd9huji2j14xop37v9dc7li` (`rol`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA EMPRESA
CREATE TABLE `empresa` (
    `activo` bit(1) NOT NULL,
    `empresa_id` bigint NOT NULL AUTO_INCREMENT,
    `nit` varchar(12) NOT NULL,
    `telefono` varchar(20) NOT NULL,
    `correo` varchar(100) NOT NULL,
    `razon_social` varchar(175) NOT NULL,
    `ciudad` varchar(255) NOT NULL,
    `direccion` varchar(255) NOT NULL,
    `imagen` varchar(255) DEFAULT NULL,
    `e_sector` enum('BAR','CAFE','CATERING','HOTEL','NIGHTCLUB','RESTAURANT') DEFAULT NULL,
    PRIMARY KEY (`empresa_id`),
    UNIQUE KEY `uk_empresa_nit` (`nit`),
    UNIQUE KEY `uk_empresa_razon_social` (`razon_social`),
    UNIQUE KEY `uk_empresa_telefono` (`telefono`),
    UNIQUE KEY `uk_empresa_correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABAL USUARIO
CREATE TABLE `usuario` (
    `account_non_expired` bit(1) DEFAULT NULL,
    `account_non_locked` bit(1) DEFAULT NULL,
    `credentials_non_expired` bit(1) DEFAULT NULL,
    `is_enabled` bit(1) DEFAULT NULL,
    `empresa_id` bigint DEFAULT NULL,
    `rol_id` bigint DEFAULT NULL,
    `usuario_id` bigint NOT NULL AUTO_INCREMENT,
    `numero_identificacion` varchar(15) DEFAULT NULL,
    `telefono` varchar(20) DEFAULT NULL,
    `correo` varchar(100) NOT NULL,
    `direccion` varchar(100) DEFAULT NULL,
    `password` varchar(100) DEFAULT NULL,
    `apellidos` varchar(175) DEFAULT NULL,
    `nombres` varchar(175) DEFAULT NULL,
    `imagen` varchar(255) DEFAULT NULL,
    `tipo_identificacion` enum('CC','CE','DE','NIT','PP','TE','TI') DEFAULT NULL,
    PRIMARY KEY (`usuario_id`),
    UNIQUE KEY `uk_usuario_correo` (`correo`),
    UNIQUE KEY `uk_usuario_telefono` (`telefono`),
    UNIQUE KEY `UKpxkkfbhkbchlb7g4ybf8e4aqj` (`empresa_id`),
    KEY `fk_usuario_rol` (`rol_id`),
    CONSTRAINT `fk_usuario_empresa` FOREIGN KEY (`empresa_id`) REFERENCES `empresa` (`empresa_id`),
    CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`rol_id`) REFERENCES `rol` (`rol_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA CATEGORIA
CREATE TABLE `categoria` (
    `activo` bit(1) NOT NULL,
    `categoria_id` bigint NOT NULL AUTO_INCREMENT,
    `nombre` varchar(175) NOT NULL,
    `descripcion` varchar(500) NOT NULL,
    `imagen` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`categoria_id`),
    UNIQUE KEY `uk_categoria_nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA SUBCATEGORIA
CREATE TABLE `sub_categorias` (
    `categoria_id` bigint DEFAULT NULL,
    `subcategoria_id` bigint NOT NULL AUTO_INCREMENT,
    `nombre` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`subcategoria_id`),
    KEY `fk_sub-Categoria_categoria` (`categoria_id`),
    CONSTRAINT `fk_sub-Categoria_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categoria` (`categoria_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA PRODUCTO
CREATE TABLE `producto` (
  `activo` bit(1) NOT NULL,
  `precio` double NOT NULL,
  `precio_regular` double DEFAULT 0,
  `stock` int NOT NULL,
  `categoria_id` bigint DEFAULT NULL,
  `producto_id` bigint NOT NULL AUTO_INCREMENT,
  `subcategoria_id` bigint DEFAULT NULL,
  `usuario_id` bigint DEFAULT NULL,
  `descripcion` varchar(700) DEFAULT NULL,
  `codigo_producto` varchar(255) DEFAULT NULL,
  `imagen` varchar(255) DEFAULT NULL,
  `marca` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `pais` varchar(255) DEFAULT NULL,
  `tipo` enum('CAJA','COMBO','PACK','UNIDAD') DEFAULT NULL,
  PRIMARY KEY (`producto_id`),
  UNIQUE KEY `uk_producto_codigo` (`codigo_producto`),
  KEY `fk_producto_categoria` (`categoria_id`),
  KEY `fk_producto_proveedor` (`usuario_id`),
  KEY `fk_producto_subcategoria` (`subcategoria_id`),
  CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categoria` (`categoria_id`),
  CONSTRAINT `fk_producto_proveedor` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`),
  CONSTRAINT `fk_producto_subcategoria` FOREIGN KEY (`subcategoria_id`) REFERENCES `sub_categorias` (`subcategoria_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA COMPRA
CREATE TABLE `compra` (
    `iva` double NOT NULL,
    `subtotal` double NOT NULL,
    `total` double NOT NULL,
    `compra_id` bigint NOT NULL AUTO_INCREMENT,
    `fecha` datetime(6) DEFAULT NULL,
    `usuario_id` bigint DEFAULT NULL,
    `cupon_descuento` varchar(255) DEFAULT NULL,
    `estado` enum('CANCELADO','PAGADO','PENDIENTE','RECHAZADO') DEFAULT NULL,
    `metodo_pago` enum('DAVIPLATA','MERCADO_PAGO','NEQUI','PAYPAL','PSE','TARJETA_CREDITO','TARJETA_DEBITO','TRANSFERENCIA') DEFAULT NULL,
    PRIMARY KEY (`compra_id`),
    KEY `fk_compra_usuario` (`usuario_id`),
    CONSTRAINT `fk_compra_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA DETALLE_VENTA
CREATE TABLE `detalle_venta` (
    `cantidad` int NOT NULL,
    `precio_unitario` int DEFAULT NULL,
    `subtotal` int NOT NULL,
    `compra_id` bigint DEFAULT NULL,
    `detalle_venta_id` bigint NOT NULL AUTO_INCREMENT,
    `producto_id` bigint DEFAULT NULL,
    PRIMARY KEY (`detalle_venta_id`),
    KEY `fk_detalleVenta_compra` (`compra_id`),
    KEY `fk_detalleVenta_producto` (`producto_id`),
    CONSTRAINT `fk_detalleVenta_compra` FOREIGN KEY (`compra_id`) REFERENCES `compra` (`compra_id`),
    CONSTRAINT `fk_detalleVenta_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`producto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA HISTORIA
CREATE TABLE `historia` (
    `fecha` date NOT NULL,
    `is_enabled` bit(1) DEFAULT NULL,
    `historia_id` bigint NOT NULL AUTO_INCREMENT,
    `titulo` varchar(100) NOT NULL,
    `descripcion` varchar(1000) NOT NULL,
    `historia_completa` TEXT NOT NULL,
    `imagen` varchar(255) NOT NULL,
    PRIMARY KEY (`historia_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA FACTURA_PROVEEDOR
CREATE TABLE `factura_proveedor` (
    `activo` bit(1) NOT NULL,
    `fecha_emision` date DEFAULT NULL,
    `total` double NOT NULL,
    `factura_id` bigint NOT NULL AUTO_INCREMENT,
    `fecha_registro` datetime(6) DEFAULT NULL,
    `usuario_id` bigint DEFAULT NULL,
    `numero_factura` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`factura_id`),
    KEY `fk_factura_proveedor` (`usuario_id`),
    CONSTRAINT `fk_factura_proveedor` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA DETALLE_FACTURA
CREATE TABLE `detalle_factura` (
    `cantidad` int NOT NULL,
    `precio_compra` int NOT NULL,
    `subtotal` int NOT NULL,
    `detalle_factura_id` bigint NOT NULL AUTO_INCREMENT,
    `factura_id` bigint DEFAULT NULL,
    `producto_id` bigint DEFAULT NULL,
    PRIMARY KEY (`detalle_factura_id`),
    UNIQUE KEY `uk_detalleFactura_factura` (`producto_id`,`factura_id`),
    KEY `fk_detalleFactura_factura` (`factura_id`),
    CONSTRAINT `fk_detalleFactura_factura` FOREIGN KEY (`factura_id`) REFERENCES `factura_proveedor` (`factura_id`),
    CONSTRAINT `fk_detalleFactura_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`producto_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA COMENTARIO
CREATE TABLE `comentario` (
    `activo` bit(1) NOT NULL,
    `calificacion` int NOT NULL,
    `fecha` date NOT NULL,
    `comentario_id` bigint NOT NULL AUTO_INCREMENT,
    `historia_id` bigint DEFAULT NULL,
    `usuario_id` bigint DEFAULT NULL,
    `titulo` varchar(100) NOT NULL,
    `mensaje` varchar(1000) NOT NULL,
    PRIMARY KEY (`comentario_id`),
    KEY `fk_comentario_historia` (`historia_id`),
    KEY `fk_comentario_usuario` (`usuario_id`),
    CONSTRAINT `fk_comentario_historia` FOREIGN KEY (`historia_id`) REFERENCES `historia` (`historia_id`),
    CONSTRAINT `fk_comentario_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- TABLA PETICIONES (PQRS)
CREATE TABLE `peticiones` (
    `activo` bit(1) NOT NULL,
    `fecha` date DEFAULT NULL,
    `peticion_id` bigint NOT NULL AUTO_INCREMENT,
    `usuario_id` bigint DEFAULT NULL,
    `asunto` TEXT DEFAULT NULL,
    `mensaje` TEXT DEFAULT NULL,
    `estado` enum('EN_PROCESO','PENDIENTE','RESUELTO') DEFAULT NULL,
    `tipo` enum('PETICION','QUEJA','RECLAMO','SUGERENCIA') DEFAULT NULL,
    PRIMARY KEY (`peticion_id`),
    KEY `fk_peticion_usuario` (`usuario_id`),
    CONSTRAINT `fk_peticion_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`usuario_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;