-- Insertar Comentarios
-- COMENTARIOS PARA HISTORIA 1 (Whisky Escocés)
INSERT INTO comentario (titulo, mensaje, calificacion, fecha, activo, usuario_id, historia_id) VALUES
('¡Excelente guía!', 'Como amante del whisky escocés, esta guía me pareció incredibly útil. La explicación de las regiones es precisa y ayuda mucho a entender las diferencias entre estilos. ¡Perfecta para recomendarla a amigos que están empezando!', 5, '2024-02-20', true, 2, 1),
('Muy informativo', 'Siempre me había confundido con las regiones de Escocia, pero este artículo lo explica de manera clara y concisa. La parte sobre la degustación profesional es especialmente valiosa para mejorar la experiencia.', 4, '2024-03-10', true, 3, 1),
('Buen contenido técnico', 'Como bartender profesional, valoro la exactitud de la información sobre el proceso de doble destilación. Solo echo en falta más detalles sobre tiempos de maduración específicos por región.', 4, '2024-04-05', true, 4, 1),
('Increíble recorrido sensorial', 'La descripción de los sabores y aromas me transportó directamente a las Highlands. Ya estoy planeando mi próxima compra basándome en estas recomendaciones regionales.', 5, '2024-05-15', true, 5, 1),
('Básico pero completo', 'Buena introducción para principiantes. Me hubiera gustado más profundidad en las técnicas de cata avanzada, pero como guía inicial cumple perfectamente su propósito.', 3, '2024-06-22', true, 6, 1);

-- COMENTARIOS PARA HISTORIA 2 (Vino Tinto)
INSERT INTO comentario (titulo, mensaje, calificacion, fecha, activo, usuario_id, historia_id) VALUES
('Guía de cata excepcional', 'Las técnicas de cata profesional explicadas aquí han elevado mi apreciación del vino tinto. El apartado sobre maridajes es particularmente útil para mis cenas de negocios.', 5, '2024-02-28', true, 3, 2),
('Perfecto para principiantes', 'Empecé en el mundo del vino hace poco y esta guía me ha dado la confianza para elegir y degustar correctamente. Las variedades de uva bien explicadas hacen la diferencia.', 4, '2024-03-18', true, 4, 2),
('Análisis profesional', 'Como sommelier, apruebo la precisión técnica en la descripción de las fases de cata. La sección de maridaje con carnes es especialmente acertada.', 5, '2024-04-12', true, 5, 2),
('Buenas bases', 'El contenido es sólido y bien estructurado. Sugeriría añadir una sección sobre temperatura de servicio ideal para cada variedad de uva.', 4, '2024-05-20', true, 6, 2),
('Transformó mis catas', 'Aplicando las técnicas descritas he descubierto matices en vinos que antes pasaba por alto. La explicación visual de los "vino legs" fue reveladora.', 5, '2024-06-30', true, 2, 2);

-- COMENTARIOS PARA HISTORIA 3 (Coctelería Clásica)
INSERT INTO comentario (titulo, mensaje, calificacion, fecha, activo, usuario_id, historia_id) VALUES
('¡Revolutionó mi bar en casa!', 'Seguí las técnicas de preparación y la diferencia en mis cócteles es abismal. El tip sobre stirring vs shaking cambió completamente mi approach a la mixología casera.', 5, '2024-01-25', true, 4, 3),
('Historia viva en cada trago', 'Me encanta cómo contextualizan cada cóctel en su época histórica. El Old Fashioned nunca había sabido tan bien como ahora que conozco su tradición.', 4, '2024-02-14', true, 5, 3),
('Selección impecable', 'Los 10 cócteles elegidos son verdaderamente los fundamentales. Como bartender, los recomiendo constantemente a nuevos colegas que quieren aprender lo esencial.', 5, '2024-03-22', true, 6, 3),
('Técnicas que marcan diferencia', 'La explicación sobre la calidad de los licores base es crucial. Demasiada gente invierte en ingredientes secundarios pero escatima en el espíritu principal.', 4, '2024-04-18', true, 2, 3),
('Guía completa', 'Desde el equipo básico hasta las técnicas avanzadas, todo está cubierto. Perfecto para quien quiere montar un home bar profesional.', 5, '2024-05-25', true, 3, 3);

-- COMENTARIOS PARA HISTORIA 4 (Ron Premium)
INSERT INTO comentario (titulo, mensaje, calificacion, fecha, activo, usuario_id, historia_id) VALUES
('Viaje caribeño en botella', 'La descripción de los estilos regionales es tan vívida que casi puedo sentir la brisa del Caribe. Excelente trabajo documentando esta diversidad.', 5, '2024-03-05', true, 5, 4),
('Aprendí más que en años', 'Llevo años bebiendo ron sin entender las diferencias entre estilos. Este artículo iluminó por qué prefiero los rones ingleses sobre los españoles.', 4, '2024-04-10', true, 6, 4),
('Técnicas de envejecimiento bien explicadas', 'La influencia del clima tropical en la maduración es un punto que pocos mencionan pero que es fundamental. Información valiosa para coleccionistas.', 5, '2024-05-08', true, 2, 4),
('Cócteles que resaltan el ron', 'Las recomendaciones de cócteles son acertadas. El Mai Tai con ron agricole fue un descubrimiento espectacular para mi paladar.', 4, '2024-06-12', true, 3, 4),
('Profundidad histórica', 'Me encantó el contexto histórico de las influencias coloniales en la producción. Da mayor apreciación a cada botella que adquiero.', 5, '2024-07-20', true, 4, 4);

-- COMENTARIOS PARA HISTORIA 5 (Tequila y Mezcal)
INSERT INTO comentario (titulo, mensaje, calificacion, fecha, activo, usuario_id, historia_id) VALUES
('Destruye mitos', 'Por fin entiendo la diferencia real entre tequila y mezcal. La explicación sobre los tipos de agave y regiones es clarísima.', 5, '2024-02-15', true, 6, 5),
('Cultura mexicana en estado puro', 'La sección sobre cultura de degustación me hizo apreciar estas bebidas como nunca. Ahora disfruto el mezcal con el respeto que merece.', 4, '2024-03-25', true, 2, 5),
('Guía de categorías útil', 'La clasificación por maduración es perfecta para hacer compras informadas. Evité comprar un añejo innecesario gracias a esta información.', 5, '2024-04-30', true, 3, 5),
('Podría profundizar más', 'Buen contenido base, pero echo en falta más detalles sobre las denominaciones de origen protegidas y procesos de certificación.', 3, '2024-05-28', true, 4, 5),
('Transformó mi perspectiva', 'Siempre asocié el tequila con fiestas universitarias. Ahora lo valoro como una bebida compleja y llena de tradición.', 5, '2024-07-05', true, 5, 5);

-- COMENTARIOS PARA HISTORIA 6 (Gin Artesanal)
INSERT INTO comentario (titulo, mensaje, calificacion, fecha, activo, usuario_id, historia_id) VALUES
('Revolución botánica bien explicada', 'La evolución del London Dry al gin moderno está perfectamente documentada. Como coleccionista, valoro esta perspectiva histórica.', 5, '2024-03-12', true, 2, 6),
('Botánicos al detalle', 'La descripción de los ingredientes secundarios me ayudó a entender por qué prefiero ciertas marcas. Ahora elijo gins basado en su perfil botánico.', 4, '2024-04-22', true, 3, 6),
('Técnicas innovadoras fascinantes', 'La maceración al vacío era un concepto nuevo para mí. Esta información me hace apreciar aún más el trabajo detrás de cada botella artesanal.', 5, '2024-05-17', true, 4, 6),
('Equilibrio perfecto', 'Excelente balance entre información técnica y accesibilidad. Hace complejos procesos de destilación comprensibles para el consumidor promedio.', 4, '2024-06-24', true, 5, 6),
('Inspirador', 'Después de leer esto, estoy considerando empezar mis propias infusiones de gin en casa. La sección de botánicos es especialmente inspiradora.', 5, '2024-08-10', true, 6, 6);

-- COMENTARIOS PARA HISTORIA 7 (Vodka Premium)
INSERT INTO comentario (titulo, mensaje, calificacion, fecha, activo, usuario_id, historia_id) VALUES
('Rompe estereotipos', 'Por fin alguien que explica que el vodka no es una bebida sin carácter. Las diferencias entre materias primas son más notables de lo que pensaba.', 5, '2024-01-30', true, 3, 7),
('Filtración reveladora', 'Nunca entendí por qué algunos vodkas son más suaves hasta que leí sobre los procesos de filtración multi-etapa. Información invaluable.', 4, '2024-03-08', true, 4, 7),
('Coctelería de alta gama', 'Los ejemplos de uso en cócteles premium son perfectos. El Moscow Mule con vodka de centeno fue una revelación para mi negocio.', 5, '2024-04-15', true, 5, 7),
('Podría incluir marcas', 'Buena información general, pero sugeriría ejemplos específicos de marcas que ejemplifiquen cada tipo de materia prima.', 3, '2024-05-22', true, 6, 7),
('Pureza bien explicada', 'La estética glacial en la descripción captura perfectamente la esencia del vodka premium. Ahora busco específicamente esas características.', 5, '2024-07-18', true, 2, 7);

-- COMENTARIOS PARA HISTORIA 8 (Vinos Espumosos)
INSERT INTO comentario (titulo, mensaje, calificacion, fecha, activo, usuario_id, historia_id) VALUES
('Celebración en cada burbuja', 'La descripción de los métodos de elaboración es tan elegante como la bebida misma. Ahora aprecio cada burbuja con nuevo respeto.', 5, '2024-02-10', true, 4, 8),
('Clasificación por dulzor útil', 'La escala de dulce resolvió años de confusión sobre qué champagne elegir para diferentes ocasiones. Información práctica inmediata.', 4, '2024-03-28', true, 5, 8),
('Técnica de apertura esencial', 'Siempre abría el champagne incorrectamente. Esta técnica preserva las burbujas y hace la diferencia en la experiencia completa.', 5, '2024-05-05', true, 6, 8),
('Completo y accesible', 'Desde métodos hasta servicio, todo cubierto de manera comprensible. Perfecto para quien quiere sonar como experto en reuniones sociales.', 4, '2024-06-15', true, 2, 8),
('Eleva cualquier ocasión', 'Aplicando estos conocimientos, he convertido ocasiones ordinarias en momentos especiales. El poder del champagne bien entendido.', 5, '2024-08-01', true, 3, 8);

-- COMENTARIOS PARA HISTORIA 9 (Licores de Hierbas)
INSERT INTO comentario (titulo, mensaje, calificacion, fecha, activo, usuario_id, historia_id) VALUES
('Sabiduría ancestral destilada', 'La conexión entre tradición herbaria y licores modernos está magistralmente explicada. Da profundidad histórica a cada sorbo.', 5, '2024-04-08', true, 5, 9),
('Redescubrí licores olvidados', 'Gracias a este artículo, redescubrí el Benedictine que tenía olvidado en mi bar. La sección de coctelería moderna es inspiradora.', 4, '2024-05-14', true, 6, 9),
('Perfecto para mixólogos', 'Como bartender, la información sobre el uso de licores de hierbas como modificadores ha elevado la complejidad de mis creaciones.', 5, '2024-06-20', true, 2, 9),
('Fusión tradición-modernidad', 'Excelente equilibrio entre respeto a las recetas ancestrales y aplicaciones contemporáneas. Bridge perfecto entre generaciones.', 4, '2024-07-25', true, 3, 9),
('Educación sensorial', 'Más que una guía, es una educación del paladar. Ahora detecto matices en licores de hierbas que antes pasaban desapercibidos.', 5, '2024-09-05', true, 4, 9);