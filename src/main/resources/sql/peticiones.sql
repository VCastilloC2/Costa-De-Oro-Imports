USE db_beer;

-- Insertar peticiones
-- Peticiones del usuario 2
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-01-15', 2, 'Solicitud de catálogo de whiskies añejos', 'Me gustaría recibir el catálogo completo de whiskies añejos que tienen disponible, especialmente interesado en productos de más de 18 años. Requiero información sobre precios, disponibilidad y opciones de envío a mi domicilio.', 'RESUELTO', 'PETICION'),
(true, '2024-03-22', 2, 'Problema con pedido #23456', 'El pedido #23456 que realicé el 15 de marzo no ha llegado aún. Según el tracking, debería haber sido entregado hace 3 días. Necesito saber el estado actual de mi pedido y fecha estimada de entrega.', 'EN_PROCESO', 'RECLAMO'),
(true, '2024-05-10', 2, 'Sugerencia para mejorar la web', 'Sería excelente que implementaran un sistema de recomendación basado en compras anteriores. También sugiero añadir videos de cata para productos premium que ayuden en la decisión de compra.', 'PENDIENTE', 'SUGERENCIA');

-- Peticiones del usuario 3
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-02-08', 3, 'Queja sobre atención al cliente', 'El asesor que me atendió por teléfono fue poco amable y no supo resolver mis dudas sobre los vinos espumosos. Esperaba mejor trato considerando que soy cliente frecuente.', 'RESUELTO', 'QUEJA'),
(true, '2024-04-18', 3, 'Petición de vinos orgánicos', 'Estoy interesado en vinos orgánicos y biodinámicos. ¿Tienen previsto incorporar este tipo de productos en su catálogo? Me encantaría contar con más opciones sostenibles.', 'EN_PROCESO', 'PETICION'),
(true, '2024-06-05', 3, 'Reclamo por producto defectuoso', 'Recibí una botella de Ron Añejo con el sello violado y parece que ha perdido calidad. Solicito el reemplazo inmediato o la devolución del importe pagado.', 'PENDIENTE', 'RECLAMO');

-- Peticiones del usuario 4
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-01-28', 4, 'Sugerencia para eventos de cata', 'Sería fantástico organizar eventos de cata virtuales para clientes premium. Podrían incluir kits de degustación con muestras de productos exclusivos.', 'RESUELTO', 'SUGERENCIA'),
(true, '2024-03-15', 4, 'Petición de gin artesanal', 'Busco gins artesanales con botánicos locales. ¿Podrían ampliar su selección en esta categoría? Especialmente interesado en productos de pequeñas destilerías.', 'EN_PROCESO', 'PETICION'),
(true, '2024-07-12', 4, 'Queja sobre tiempos de entrega', 'Los tiempos de entrega se han extendido considerablemente en los últimos meses. Mi último pedido tardó 8 días hábiles cuando antes era máximo 3 días.', 'PENDIENTE', 'QUEJA');

-- Peticiones del usuario 5
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-02-20', 5, 'Reclamo por error en factura', 'En la factura del pedido #24567 me cobraron un producto que no recibí. Adjunto comprobante de pago y listado de productos recibidos para la rectificación.', 'RESUELTO', 'RECLAMO'),
(true, '2024-05-22', 5, 'Sugerencia app móvil', 'Sugiero desarrollar una aplicación móvil con funciones avanzadas como scanner de etiquetas, historial de compras y notificaciones de ofertas personalizadas.', 'EN_PROCESO', 'SUGERENCIA'),
(true, '2024-08-10', 5, 'Petición de licores sin alcohol', 'Cada vez hay más demanda de licores sin alcohol de calidad. ¿Consideran incorporar esta línea de productos? Sería ideal para conductores y personas que no consumen alcohol.', 'PENDIENTE', 'PETICION');

-- Peticiones del usuario 6
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-03-05', 6, 'Queja por embalaje defectuoso', 'Recibí un pack de vinos donde una botella llegó quebrada debido a un embalaje insuficiente. El líquido dañó el resto del paquete. Solicito solución inmediata.', 'RESUELTO', 'QUEJA'),
(true, '2024-06-18', 6, 'Petición de tequilas premium', 'Me interesa ampliar mi colección de tequilas premium. ¿Podrían asesorarme sobre sus productos de gama alta y disponibilidad de ediciones limitadas?', 'EN_PROCESO', 'PETICION'),
(true, '2024-09-02', 6, 'Sugerencia programa fidelización', 'Propongo crear un programa de fidelización con niveles según volumen de compra, que incluya acceso a productos exclusivos y precios preferenciales.', 'PENDIENTE', 'SUGERENCIA');

-- Peticiones del usuario 7
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-01-10', 7, 'Reclamo por producto equivocado', 'En mi pedido #25678 recibí un whisky diferente al solicitado. Solicito el cambio por el producto correcto o en su defecto la devolución del importe.', 'RESUELTO', 'RECLAMO'),
(true, '2024-04-05', 7, 'Sugerencia para regalos corporativos', 'Podrían implementar una sección especial para regalos corporativos con opciones de personalización de etiquetas y embalaje para empresas.', 'EN_PROCESO', 'SUGERENCIA'),
(true, '2024-07-20', 7, 'Petición de coctelería lista', 'Estoy interesado en productos de coctelería lista para servir. ¿Tienen previsto incorporar esta línea? Sería perfecto para eventos y reuniones.', 'PENDIENTE', 'PETICION');

-- Peticiones del usuario 8
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-02-14', 8, 'Queja sobre actualización de precios', 'En la web aparecían precios promocionales que al agregar al carrito cambiaban al precio regular. Esto me generó confusión y pérdida de tiempo.', 'RESUELTO', 'QUEJA'),
(true, '2024-05-30', 8, 'Petición de whiskies japoneses', 'Me encantaría ver más variedad de whiskies japoneses en su catálogo. Actualmente la selección es bastante limitada comparada con otras categorías.', 'EN_PROCESO', 'PETICION'),
(true, '2024-08-15', 8, 'Reclamo por demora en respuesta', 'Llevo 5 días sin respuesta a mi consulta sobre disponibilidad de productos. La demora afecta mi planificación de compras para un evento importante.', 'PENDIENTE', 'RECLAMO');

-- Peticiones del usuario 9
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-03-25', 9, 'Sugerencia contenido educativo', 'Sería valioso incluir contenido educativo sobre maridaje, técnicas de cata y historia de las bebidas. Podría ser en formato blog o videotutoriales.', 'RESUELTO', 'SUGERENCIA'),
(true, '2024-06-10', 9, 'Petición de rones añejos', 'Busco rones añejos de calidad premium para mi negocio. Necesito información sobre precios por volumen y condiciones para compras recurrentes.', 'EN_PROCESO', 'PETICION'),
(true, '2024-09-18', 9, 'Queja por servicio técnico', 'He tenido problemas para acceder a mi cuenta durante 3 días. El servicio técnico no ha podido resolverlo de manera efectiva hasta el momento.', 'PENDIENTE', 'QUEJA');

-- Peticiones del usuario 10
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-01-30', 10, 'Reclamo por calidad de producto', 'El champagne que recibí parece haber estado expuesto a temperaturas inadecuadas durante el transporte. Presenta cambios en color y sabor.', 'RESUELTO', 'RECLAMO'),
(true, '2024-04-22', 10, 'Sugerencia embalaje sostenible', 'Sugiero implementar opciones de embalaje sostenible y reducir el uso de plásticos en los envíos. Muchos clientes valoraríamos esta iniciativa ecológica.', 'EN_PROCESO', 'SUGERENCIA'),
(true, '2024-07-08', 10, 'Petición de licores medievales', 'Estoy interesado en licores históricos y medievales. ¿Tienen acceso a este tipo de productos o podrían gestionar pedidos especiales?', 'PENDIENTE', 'PETICION');

-- Peticiones del usuario 11
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-02-18', 11, 'Queja por política de devoluciones', 'La política de devoluciones actual es muy restrictiva. Tuve un problema con un producto y el proceso de devolución fue complicado y lento.', 'RESUELTO', 'QUEJA'),
(true, '2024-05-12', 11, 'Petición de vodka ultrapremium', 'Busco vodkas de gama ultrapremium para mi restaurante. Necesito información sobre marcas exclusivas y condiciones para compras al por mayor.', 'EN_PROCESO', 'PETICION'),
(true, '2024-08-28', 11, 'Reclamo por error en descuento', 'Se aplicó mal el descuento promocional en mi última compra. Pagué de más y necesito que rectifiquen la factura y me reintegren la diferencia.', 'PENDIENTE', 'RECLAMO');

-- Peticiones del usuario 12
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-03-08', 12, 'Sugerencia para búsqueda avanzada', 'La función de búsqueda podría mejorarse con filtros avanzados por tipo de bebida, región, añada, precio y valoraciones de otros clientes.', 'RESUELTO', 'SUGERENCIA'),
(true, '2024-06-25', 12, 'Petición de mezcal artesanal', 'Estoy interesado en mezcal artesanal de pequeñas productoras. ¿Podrían ampliar su catálogo en esta categoría con productos auténticos?', 'EN_PROCESO', 'PETICION'),
(true, '2024-10-05', 12, 'Queja por comunicación', 'No recibí notificación sobre el retraso en mi pedido. Una comunicación proactiva hubiera mejorado mucho la experiencia de compra.', 'PENDIENTE', 'QUEJA');

-- Peticiones del usuario 13
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-04-14', 13, 'Reclamo por producto no recibido', 'El tracking indica que mi pedido fue entregado pero nunca llegó a mis manos. Necesito aclarar qué sucedió y recibir mi pedido o el reembolso.', 'RESUELTO', 'RECLAMO'),
(true, '2024-07-30', 13, 'Sugerencia cajas regalo', 'Podrían ofrecer cajas regalo temáticas (por ejemplo, "Iniciación al whisky", "Cócteles clásicos") que incluyan varios productos y guías de consumo.', 'EN_PROCESO', 'SUGERENCIA'),
(true, '2024-11-12', 13, 'Petición de cervezas artesanales', 'Aunque se especializan en licores, ¿consideran incorporar cervezas artesanales premium? Sería un complemento ideal para su catálogo.', 'PENDIENTE', 'PETICION');

-- Peticiones del usuario 14
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-05-05', 14, 'Queja por atención postventa', 'El servicio postventa no cumplió con lo prometido. Me aseguraron una llamada de seguimiento que nunca recibí después de un problema con mi pedido.', 'RESUELTO', 'QUEJA'),
(true, '2024-08-18', 14, 'Petición de whiskies single malt', 'Busco whiskies single malt de destilerías independientes. Me interesan productos con carácter único y no solo las marcas comerciales grandes.', 'EN_PROCESO', 'PETICION'),
(true, '2024-12-01', 14, 'Reclamo por facturación duplicada', 'Me cobraron dos veces el mismo pedido. Adjunto comprobantes de ambos cargos y solicito la devolución inmediata de uno de ellos.', 'PENDIENTE', 'RECLAMO');

-- Peticiones del usuario 15
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-06-20', 15, 'Sugerencia para recomendaciones', 'Podrían implementar un sistema de recomendaciones basado en el historial de navegación y compras, similar a lo que hacen otras plataformas de ecommerce.', 'RESUELTO', 'SUGERENCIA'),
(true, '2024-09-10', 15, 'Petición de vinos de Jerez', 'Estoy interesado en vinos de Jerez y generosos. La selección actual es limitada y me gustaría ver más variedad en esta categoría especial.', 'EN_PROCESO', 'PETICION');

-- Peticiones del usuario 16
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-07-15', 16, 'Queja por información incompleta', 'La ficha de varios productos carece de información esencial como añada, tipo de barrica o tiempo de maduración. Esto dificulta la decisión de compra.', 'RESUELTO', 'QUEJA'),
(true, '2024-10-22', 16, 'Reclamo por producto vencido', 'Recibí una botella de licor de hierbas que estaba cerca de su fecha de vencimiento. No es aceptable para un producto de calidad premium.', 'PENDIENTE', 'RECLAMO');

-- Peticiones del usuario 17
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-08-05', 17, 'Sugerencia para wishlist', 'Sería útil tener una lista de deseos donde poder guardar productos de interés y recibir notificaciones cuando bajen de precio o estén disponibles.', 'RESUELTO', 'SUGERENCIA'),
(true, '2024-11-18', 17, 'Petición de accesorios', '¿Consideran vender accesorios para coctelería? Copas especializadas, herramientas de bar y kits de regalo serían un complemento perfecto.', 'EN_PROCESO', 'PETICION');

-- Peticiones del usuario 18
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-09-12', 18, 'Queja por cambios en pedido', 'Modificaron mi pedido sin consultarme, reemplazando un producto agotado por otro que no era de mi interés. Deberían avisar antes de hacer cambios.', 'RESUELTO', 'QUEJA'),
(true, '2025-01-08', 18, 'Reclamo por garantía', 'Un whisky de edición limitada llegó con el corcho defectuoso y se evaporó parcialmente. Solicito aplicación de la garantía y reemplazo del producto.', 'PENDIENTE', 'RECLAMO');

-- Peticiones del usuario 19
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-10-30', 19, 'Sugerencia programa referidos', 'Podrían implementar un programa de referidos donde los clientes actuales puedan recomendar amigos y recibir beneficios por nuevas compras.', 'RESUELTO', 'SUGERENCIA'),
(true, '2025-02-14', 19, 'Petición de destilados locales', 'Me interesa apoyar productores locales. ¿Tienen destilados de la región o podrían incorporar productos de pequeñas destilerías nacionales?', 'EN_PROCESO', 'PETICION');

-- Peticiones del usuario 20
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-11-25', 20, 'Queja por confidencialidad', 'Recibí publicidad de terceros relacionados con mis compras. Considero que compartieron mis datos sin autorización expresa.', 'RESUELTO', 'QUEJA'),
(true, '2025-03-10', 20, 'Reclamo por incumplimiento', 'No se respetó la fecha de entrega prometida para un regalo importante, causándome un problema personal con el destinatario.', 'PENDIENTE', 'RECLAMO');

-- Peticiones del usuario 21
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2024-12-15', 21, 'Sugerencia para pedidos recurrentes', 'Podrían ofrecer un sistema de pedidos recurrentes con descuento para productos de consumo frecuente, como los que usan bares y restaurantes.', 'RESUELTO', 'SUGERENCIA'),
(true, '2025-04-05', 21, 'Petición de licores de importación', 'Estoy buscando licores de importación poco comunes en el mercado local. ¿Ofrecen servicio de pedidos especiales por encargo?', 'EN_PROCESO', 'PETICION');

-- Peticiones del usuario 22
INSERT INTO peticiones (activo, fecha, usuario_id, asunto, mensaje, estado, tipo) VALUES
(true, '2025-01-20', 22, 'Queja por actualización de stock', 'El sistema mostró disponibilidad de un producto que luego estaba agotado. Perdí tiempo seleccionando productos que finalmente no podía comprar.', 'RESUELTO', 'QUEJA'),
(true, '2025-05-12', 22, 'Reclamo por servicio de entrega', 'El servicio de mensajería no cumplió con las instrucciones especiales de entrega que había especificado para mi pedido.', 'PENDIENTE', 'RECLAMO');