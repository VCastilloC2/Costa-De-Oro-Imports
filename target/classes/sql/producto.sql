
-- Insertar categorías de bebidas alcohólicas
INSERT INTO `categoria` (`activo`, `nombre`, `descripcion`, `imagen`) VALUES
(1, 'Vino', 'Descubre la elegancia en cada copa. Nuestra selección de vinos te transporta a los mejores viñedos del mundo, con aromas y sabores que celebran los momentos especiales. Perfectos para compartir o disfrutar en una noche tranquila.', 'gestion-ventas/imagen-categoria/CP1'),
(1, 'Whisky', 'Elixir dorado de tradición y carácter. Cada sorbo de nuestro whisky cuenta una historia de añejamiento perfecto y sabores robustos. Ideal para los paladares exigentes que buscan sofisticación y profundidad.', 'gestion-ventas/imagen-categoria/CP2'),
(1, 'Ron', 'Dulce caribeño en estado puro. Nuestro ron captura el espíritu festivo de las islas, con notas de caña de azúcar y especias que evocan brisas tropicales. Perfecto para cocktails o disfrutar solo.', 'gestion-ventas/imagen-categoria/CP3'),
(1, 'Vodka', 'Pureza y versatilidad en cada trago. La cristalinidad de nuestro vodka ofrece una experiencia suave y refrescente, ideal como base para cocktails creativos o para disfrutar en su forma más pura.', 'gestion-ventas/imagen-categoria/CP4'),
(1, 'Tequila', 'El espíritu vibrante de México. Desde el corazón del agave azul, nuestro tequila ofrece notas terrosas y cítricas que despiertan los sentidos. ¡Vive la fiesta en cada sorbo!', 'gestion-ventas/imagen-categoria/CP5'),
(1, 'Ginebra', 'Botánica y sofisticación en armonía. Nuestra ginebra combina enebro con una selección de hierbas y especias que crean un perfil complejo y aromático. La base perfecta para un gin-tonic excepcional.', 'gestion-ventas/imagen-categoria/CP6'),
(1, 'Mezcal', 'Fuego y tradición ancestral. El humo seductor de nuestro mezcal te transporta a Oaxaca, con notas ahumadas que revelan el arte centenario de su producción. Para los aventureros del sabor.', 'gestion-ventas/imagen-categoria/CP7'),
(1, 'Cerveza', 'La reina de las reuniones. Desde rubias refrescantes hasta artesanales intensas, nuestra selección de cervezas ofrece variedad y calidad para cada ocasión. ¡El mejor compañero para la camaradería!', 'gestion-ventas/imagen-categoria/CP8'),
(1, 'Aguardiente', 'Sabor tradicional que une generaciones. Nuestro aguardiente mantiene la esencia de anís y la suavidad que caracteriza esta bebida emblemática. Ideal para celebrar la amistad y la tradición.', 'gestion-ventas/imagen-categoria/CP9'),
(1, 'Brandy', 'Elegancia y suavidad envejecida. El brandy ofrece notas de fruta madura y roble que se funden en un abrazo cálido y reconfortante. Perfecto para momentos de reflexión y placer.', 'gestion-ventas/imagen-categoria/CP10');

-- Insertar subcategorías para Vino (categoria_id = 1)
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(1, 'Vino Tinto'),
(1, 'Vino Blanco'),
(1, 'Vino Rosado'),
(1, 'Vino Espumoso'),
(1, 'Champagne'),
(1, 'Cava'),
(1, 'Prosecco'),
(1, 'Lambruscos');

-- Insertar subcategorías para Whisky (categoria_id = 2)
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(2, 'Single Malt'),
(2, 'Blended Malt'),
(2, 'Single Grain'),
(2, 'Blended Grain'),
(2, 'Blended Scotch'),
(2, 'Bourbon'),
(2, 'Whisky de Centeno'),
(2, 'Whisky de Trigo'),
(2, 'Tennessee Whisky');

-- Insertar subcategorías para Ron (categoria_id = 3)
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(3, 'Ron Blanco'),
(3, 'Ron Dorado'),
(3, 'Ron Añejo'),
(3, 'Ron Oscuro'),
(3, 'Ron Especiado');

-- Insertar subcategorías para Vodka (categoria_id = 4)
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(4, 'Vodka Clásico'),
(4, 'Vodka Saborizado');

-- Insertar subcategorías para Tequila (categoria_id = 5)
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(5, 'Blanco'),
(5, 'Reposado'),
(5, 'Añejo'),
(5, 'Extra Añejo');

-- Insertar subcategorías para Ginebra (categoria_id = 6)
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(6, 'London Dry'),
(6, 'Old Tom'),
(6, 'Ginebra de Sabor');

-- Insertar subcategorías para Mezcal (categoria_id = 7)
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(7, 'Blanco'),
(7, 'Madurado en Vidrio');

-- Insertar subcategorías para Cerveza (categoria_id = 8)
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(8, 'Nacional'),
(8, 'Importada');

-- Insertar subcategorías para Brandy (categoria_id = 10)
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(10, 'Cognac'),
(10, 'Armagnac'),
(10, 'Brandy Español');

-- Aguardiente (categoria_id = 9) no tiene subcategorías
INSERT INTO `sub_categorias` (`categoria_id`, `nombre`) VALUES
(9, 'Nacional');

-- Insertar productos de Whisky
INSERT INTO `producto` (`activo`, `precio_regular`, `precio`, `stock`, `categoria_id`, `subcategoria_id`, `usuario_id`, `descripcion`, `codigo_producto`, `imagen`, `marca`, `nombre`, `pais`, `tipo`) VALUES
(1, 450000, 227000, 75, 2, 13, 2105, 'Buchanan\s 18 Años Special Reserve es la expresión premium de esta renombrada marca. Con 18 años de añejamiento en barricas de roble, ofrece notas de miel, frutos secos y un suave ahumado. Perfecto para ocasiones especiales, su finish largo y complejo demuestra por qué es considerado uno de los mejores blended scotch del mundo.', 'WHI-0001', 'gestion-ventas/imagen-producto/W1', 'Buchanan\s', 'Whisky Buchanan\s 18 Años Special Reserve 750 ml', 'Escocia', 'UNIDAD'),
(1, 320000, 182000, 40, 2, 10, 2105, 'Buchanan\s Malt Edition combina lo mejor de los single malts escoceses en una mezcla excepcional. Con notas de caramelo, vainilla y un toque de especias, este blended malt ofrece una experiencia suave pero compleja. Ideal para quienes buscan explorar el mundo de los maltas sin comprometer calidad.', 'WHI-0002', 'gestion-ventas/imagen-producto/W2', 'Buchanan\s', 'Whisky Buchanan\s Malt Edition 750 ml', 'Escocia', 'UNIDAD'),
(1, 380000, 215000, 50, 2, 13, 2105, 'Johnnie Walker Gold Reserve es un blended scotch de lujo creado con whiskies de las destilerías más exclusivas de Escocia. Con notas cremosas de miel, vainilla y frutos tropicales, ofrece una textura sedosa y un finish excepcionalmente suave. La elección perfecta para celebrar logros importantes.', 'WHI-0003', 'gestion-ventas/imagen-producto/W3', 'Johnnie Walker', 'Whisky Johnnie Walker Gold Reserve 700 ml', 'Escocia', 'UNIDAD'),
(1, 95000, 52000, 37, 2, 13, 2105, 'The Famous Grouse es un blended scotch clásico conocido por su excelente relación calidad-precio. Con notas de frutas frescas, miel y un sutil ahumado, ofrece un perfil balanceado y accesible. Perfecto para disfrutar diariamente o en reuniones informales con amigos.', 'WHI-0004', 'gestion-ventas/imagen-producto/W4', 'The Famous Grouse', 'Whisky Blended Scotch The Famous Grouse 700 ml', 'Escocia', 'UNIDAD'),
(1, 280000, 163000, 19, 2, 13, 2105, 'Johnnie Walker Double Black intensifica la experiencia del Black Label con un carácter más ahumado y robusto. Madurado en barricas carbonizadas, ofrece notas de frutas oscuras, especias y un distintivo humo costero. Para quienes buscan un blended scotch con carácter y profundidad.', 'WHI-0005', 'gestion-ventas/imagen-producto/W5', 'Johnnie Walker', 'Whisky Johnnie Walker Double Black 700 ml', 'Escocia', 'UNIDAD'),
(1, 520000, 312000, 43, 2, 9, 2105, 'Glenfiddich 15 Year Old es un single malt excepcional madurado en un único sistema solera. Con notas de miel, especias y frutas horneadas, ofrece una complejidad única. Su proceso de maduración crea un whisky increíblemente suave con un carácter distintivo y memorable.', 'WHI-0006', 'gestion-ventas/imagen-producto/W6', 'Glenfiddich', 'Glenfiddich 15 Year Old Single Malt Scotch Whisky 750 ml', 'Escocia', 'UNIDAD'),
(1, 280000, 159000, 22, 2, 9, 2105, 'Glenfiddich 12 Year Old es el single malt escocés más premiado del mundo. Madurado en barricas de roble americano y europeo, ofrece notas frescas de pera, roble dulce y un sutil carácter frutal. La puerta de entrada perfecta al mundo de los single malt de calidad.', 'WHI-0007', 'gestion-ventas/imagen-producto/W7', 'Glenfiddich', 'Glenfiddich 12 Year Old Single Malt Scotch Whisky 750 ml', 'Escocia', 'UNIDAD'),
(1, 220000, 112000, 61, 2, 13, 2105, 'Johnnie Walker Black Label es un ícono entre los blended scotch. Con 12 años de añejamiento, combina hasta 40 whiskies diferentes. Ofrece notas de frutas secas, vainilla y humo costero. Un whisky confiable y sofisticado para cualquier ocasión.', 'WHI-0008', 'gestion-ventas/imagen-producto/W8', 'Johnnie Walker', 'Whisky Johnnie Walker Black Label 700 ml', 'Escocia', 'UNIDAD');

-- Insertar productos de Tequila (Categoría 5)
INSERT INTO `producto` (`activo`, `precio_regular`, `precio`, `stock`, `categoria_id`, `subcategoria_id`, `usuario_id`, `descripcion`, `codigo_producto`, `imagen`, `marca`, `nombre`, `pais`, `tipo`) VALUES
(1, 85000, 45000, 30, 5, 25, 2108, 'Jose Cuervo Especial Silver es el tequila más vendido del mundo. Elaborado con agave azul, ofrece un perfil suave con notas cítricas y herbales. Perfecto para margaritas o disfrutar en shots, representa la esencia auténtica de México en cada sorbo.', 'TEQ-0001', 'gestion-ventas/imagen-producto/T1', 'Jose Cuervo', 'Tequila Jose Cuervo Especial Silver 750 ml', 'México', 'UNIDAD'),
(1, 320000, 185000, 28, 5, 25, 2108, 'Patrón Silver es sinónimo de lujo y calidad superior. Destilado en pequeñas cantidades con 100% agave azul, ofrece notas cítricas y pimienta con un finish excepcionalmente suave. La elección perfecta para los conocedores que buscan lo mejor.', 'TEQ-0002', 'gestion-ventas/imagen-producto/T2', 'Patrón', 'Tequila Patrón Silver 100% Agave Azul 700 ml', 'México', 'UNIDAD'),
(1, 280000, 149000, 10, 5, 27, 2108, 'Don Julio Añejo representa la maestría en el arte del tequila. Añejado por 18 meses en barricas de roble americano, desarrolla notas de vainilla, caramelo y agave cocido. Un tequila excepcional para saborear lentamente en ocasiones especiales.', 'TEQ-0003', 'gestion-ventas/imagen-producto/T3', 'Don Julio', 'Tequila Don Julio Añejo 100% Agave Azul 700 ml', 'México', 'UNIDAD'),
(1, 75000, 45000, 21, 5, 25, 2108, 'El Jimador Blanco captura la esencia pura del agave azul. Con un proceso de cocción lento, ofrece notas herbales y cítricas con un finish refrescante. Ideal para cocktails o disfrutar solo, representa la tradición tequilera mexicana.', 'TEQ-0004', 'gestion-ventas/imagen-producto/T4', 'El Jimador', 'Tequila El Jimador Blanco 100% Agave Azul 700 ml', 'México', 'UNIDAD'),
(1, 650000, 292500, 11, 5, 27, 2108, 'Don Julio 1942 es un tequila de ultra lujo creado para celebrar el año de fundación de la destilería. Añejado por 2.5 años, ofrece notas de caramelo, chocolate amargo y especias. Una experiencia sensorial inolvidable para momentos únicos.', 'TEQ-0005', 'gestion-ventas/imagen-producto/T5', 'Don Julio', 'Tequila Don Julio 1942 Añejo 100% Agave Azul 750 ml', 'México', 'UNIDAD'),
(1, 95000, 39900, 32, 5, 25, 2108, 'Gran Centenario Plata es un tequila 100% agave azul con un carácter fresco y vibrante. Con notas cítricas y de agave dulce, ofrece una experiencia suave y versátil. Perfecto para crear cocktails premium o disfrutar en las rocas.', 'TEQ-0006', 'gestion-ventas/imagen-producto/T6', 'Gran Centenario', 'Tequila Gran Centenario Plata 100% Agave Azul 700 ml', 'México', 'UNIDAD'),
(1, 110000, 45000, 14, 5, 26, 2108, 'Jose Cuervo Tradicional Reposado se madura por al menos 2 meses en barricas de roble. Desarrolla notas suaves de vainilla y caramelo mientras mantiene el carácter del agave. El equilibrio perfecto entre frescura y complejidad.', 'TEQ-0007', 'gestion-ventas/imagen-producto/T7', 'Jose Cuervo', 'Tequila Jose Cuervo Tradicional Reposado 100% Agave Azul 750 ml', 'México', 'UNIDAD'),
(1, 135000, 78000, 38, 5, 27, 2108, 'Gran Centenario Añejo se madura por 12 meses en barricas de roble, desarrollando un perfil rico y complejo. Con notas de frutos secos, caramelo y especias, ofrece un finish largo y sedoso. Ideal para después de la cena.', 'TEQ-0008', 'gestion-ventas/imagen-producto/T8', 'Gran Centenario', 'Tequila Gran Centenario Añejo 100% Agave Azul 695 ml', 'México', 'UNIDAD'),
(1, 350000, 175000, 40, 5, 27, 2108, 'Don Julio 70 es el primer tequila Añejo Claro del mundo. Añejado por 18 meses y luego filtrado para mantener su transparencia, combina la suavidad de un añejo con la frescura de un blanco. Una innovación revolucionaria en el mundo del tequila.', 'TEQ-0009', 'gestion-ventas/imagen-producto/T9', 'Don Julio', 'Tequila Don Julio 70 Añejo Claro 700 ml', 'México', 'UNIDAD');

-- Insertar productos de Vino (Categoría 1)
INSERT INTO `producto` (`activo`, `precio_regular`, `precio`, `stock`, `categoria_id`, `subcategoria_id`, `usuario_id`, `descripcion`, `codigo_producto`, `imagen`, `marca`, `nombre`, `pais`, `tipo`) VALUES
(1, 65000, 39000, 50, 1, 4, 2104, 'Moelia Sparkling Rosé Demisec es un espumoso italiano que combina elegancia y frescura. Con burbujas finas y persistentes, ofrece notas de frutos rojos, flores blancas y un toque cítrico. Perfecto para brindis especiales o acompañar postres.', 'VIN-0001', 'gestion-ventas/imagen-producto/V1', 'Moelia', 'Vino Espumoso Moelia Sparkling Rose Demisec 750 ml', 'Italia', 'UNIDAD'),
(1, 185000, 92500, 42, 1, 1, 2104, 'El Enemigo Malbec es un vino argentino de alta gama que representa la pasión por el terroir mendocino. Con intensos aromas a ciruela, violetas y especias, ofrece un paladar estructurado con taninos sedosos. Un Malbec que desafía expectativas.', 'VIN-0002', 'gestion-ventas/imagen-producto/V2', 'El Enemigo', 'Vino Tinto El Enemigo Malbec 750 ml', 'Argentina', 'UNIDAD'),
(1, 120000, 72000, 37, 1, 1, 2104, 'Catena Malbec es el emblema de la bodega Catena Zapata. Elaborado con uvas de altitud, ofrece complejidad aromática con notas de frutas negras, chocolate y un toque mineral. Un vino que expresa la elegancia del Malbec argentino.', 'VIN-0003', 'gestion-ventas/imagen-producto/V3', 'Catena', 'Vino Tinto Catena Malbec 750 ml', 'Argentina', 'UNIDAD'),
(1, 55000, 35000, 21, 1, 4, 2104, 'Nuit Dorée Blanco Sec Black Ice Edition combina estilo y calidad. Este espumoso francés ofrece aromas a manzana verde, pera y almendras, con un finish seco y refrescante. Ideal para celebraciones elegantes y momentos especiales.', 'VIN-0004', 'gestion-ventas/imagen-producto/V4', 'Nuit Dorée', 'Vino Espumoso Nuit Dorée Blanco Sec Black Ice Edition 750 ml', 'Francia', 'UNIDAD'),
(1, 45000, 35000, 26, 1, 2, 2104, 'Terra Vega Sauvignon Blanc captura la esencia fresca del valle chileno. Con intensos aromas cítricos y herbales, ofrece un paladar vibrante y refrescante. Perfecto para mariscos, ensaladas o disfrutar en días cálidos.', 'VIN-0005', 'gestion-ventas/imagen-producto/V5', 'Terra Vega', 'Vino Blanco Terra Vega Sauvignon 750 ml', 'Chile', 'UNIDAD'),
(1, 48000, 32000, 31, 1, 1, 2104, 'Terra Vega Malbec ofrece excelente relación calidad-precio. Con aromas a moras y especias suaves, presenta un paladar redondo y afrutado. Un Malbec accesible que complementa perfectamente carnes rojas y pastas.', 'VIN-0006', 'gestion-ventas/imagen-producto/V6', 'Terra Vega', 'Vino Tinto Terra Vega Malbec 750 ml', 'Chile', 'UNIDAD'),
(1, 52000, 28000, 11, 1, 1, 2104, 'Conquesta Merlot es un vino chileno suave y versátil. Con notas de ciruelas, cerezas y un toque de chocolate, ofrece taninos sedosos y un finish agradable. Ideal para comidas informales y reuniones familiares.', 'VIN-0007', 'gestion-ventas/imagen-producto/V7', 'Conquesta', 'Vino Tinto Conquesta Merlot 750 ml', 'Chile', 'UNIDAD'),
(1, 68000, 40800, 29, 1, 2, 2104, 'Oro Veneto Pinot Grigio representa la tradición vinícola del Véneto. Con aromas a durazno blanco, pera y flores, ofrece frescura y mineralidad. El compañero perfecto para pescados, mariscos y cocina italiana.', 'VIN-0008', 'gestion-ventas/imagen-producto/V8', 'Oro Veneto', 'Vino Blanco Oro Veneto Pinot Grigio Doc Delle Venezie 750 ml', 'Italia', 'UNIDAD'),
(1, 42000, 25000, 37, 1, 8, 2104, 'Botticello Rosato Dolce es un Lambrusco rosado dulce y festivo. Con burbujas alegres y aromas de frutos rojos, ofrece un paladar frutal y refrescante. Perfecto para aperitivos, picoteos y celebraciones informales.', 'VIN-0009', 'gestion-ventas/imagen-producto/V9', 'Botticello', 'Vino Lambrusco Botticello Rosato Dolce 750 ml', 'Italia', 'UNIDAD'),
(1, 42000, 25000, 16, 1, 8, 2104, 'Botticello Bianco Dolce combina la tradición Emilia-Romagna con un estilo moderno. Con notas de frutas blancas y flores, este Lambrusco blanco es dulce pero equilibrado. Ideal para acompañar postres o disfrutar como aperitivo.', 'VIN-0010', 'gestion-ventas/imagen-producto/V10', 'Botticello', 'Vino Lambrusco Botticello Bianco Dolce 750 ml', 'Italia', 'UNIDAD'),
(1, 42000, 25000, 19, 1, 8, 2104, 'Botticello Rosso Dolce es el clásico Lambrusco emiliano. Con aromas a cerezas y moras, ofrece burbujas suaves y un dulzor equilibrado. Versátil y fácil de beber, complementa desde pizzas hasta embutidos.', 'VIN-0011', 'gestion-ventas/imagen-producto/V11', 'Botticello', 'Vino Lambrusco Botticello Rosso Dolce 750 ml', 'Italia', 'UNIDAD'),
(1, 35000, 21000, 32, 1, 1, 2104, 'Casillero Reserva Especial Malbec ofrece consistencia y calidad. Con aromas a frutas maduras y especias, presenta un paladar suave y afrutado. Un Malbec confiable para el consumo diario que nunca decepciona.', 'VIN-0012', 'gestion-ventas/imagen-producto/V12', 'Casillero', 'Vino Tinto Malbec Casillero Reserva Especial 750 ml', 'Argentina', 'UNIDAD'),
(1, 46000, 23000, 32, 1, 1, 2104, 'Terra Vega Merlot destaca por su suavidad y carácter frutal. Con notas de ciruelas, vainilla y un toque de roble, ofrece un paladar redondo y accesible. Perfecto para quienes se inician en el mundo del vino tinto.', 'VIN-0013', 'gestion-ventas/imagen-producto/V13', 'Terra Vega', 'Vino Tinto Terra Vega Merlot 750 ml', 'Chile', 'UNIDAD'),
(1, 45000, 27500, 16, 1, 2, 2104, 'Terra Vega Sauvignon Blanc repite en nuestro catálogo por su popularidad. Con su perfil fresco y cítrico, sigue siendo la elección preferida para días calurosos y mariscos. Consistente en calidad y sabor temporada tras temporada.', 'VIN-0014', 'gestion-ventas/imagen-producto/V14', 'Terra Vega', 'Vino Blanco Terra Vega Sauvignon 750 ml', 'Chile', 'UNIDAD');

-- Insertar productos de Cerveza (Categoría 8)
INSERT INTO `producto` (`activo`, `precio_regular`, `precio`, `stock`, `categoria_id`, `subcategoria_id`, `usuario_id`, `descripcion`, `codigo_producto`, `imagen`, `marca`, `nombre`, `pais`, `tipo`) VALUES
(1, 25000, 13000, 10, 8, 35, 2111, 'Paulaner Dunkel es una cerveza oscura alemana con un carácter maltoso excepcional. Con notas de caramelo, chocolate y un toque de frutos secos, ofrece un perfil complejo pero equilibrado. La tradición bávara en cada sorbo.', 'CER-0001', 'gestion-ventas/imagen-producto/C1', 'Paulaner', 'Cerveza Paulaner Dunkel Botella 500 ml', 'Alemania', 'UNIDAD'),
(1, 25000, 13000, 10, 8, 35, 2111, 'Paulaner Rubia Naturtrüb es una weissbier sin filtrar que conserva su turbiedad natural. Con aromas cítricos, de clavo de olor y plátano, ofrece una experiencia auténtica de la cerveza de trigo bávara.', 'CER-0002', 'gestion-ventas/imagen-producto/C2', 'Paulaner', 'Cerveza Paulaner Rubia Naturtrueb Botella 500 ml', 'Alemania', 'UNIDAD'),
(1, 0, 3500, 20, 8, 34, 2111, 'Águila Original es la cerveza colombiana por excelencia. Refrescante, suave y con un balance perfecto entre lúpulo y malta. La compañera ideal para cualquier momento de compartir con amigos y familia.', 'CER-0003', 'gestion-ventas/imagen-producto/C3', 'Águila', 'Cerveza Aguila Original Botella 300 ml', 'Colombia', 'UNIDAD'),
(1, 0, 4500, 28, 8, 34, 2111, 'Club Colombia Dorada es premium entre las cervezas nacionales. Con un proceso de fermentación especial, ofrece un sabor suave y un finish limpio. La elección perfecta para quienes buscan calidad y tradición colombiana.', 'CER-0004', 'gestion-ventas/imagen-producto/C4', 'Club Colombia', 'Cerveza Club Colombia Dorada Botella 330 ml', 'Colombia', 'UNIDAD'),
(1, 0, 6000, 19, 8, 35, 2111, 'Corona Extra es el símbolo de la playa y el relax. Con su característico sabor suave y la tradición de servirse con limón, transporta a momentos de descanso y disfrute bajo el sol.', 'CER-0005', 'gestion-ventas/imagen-producto/C5', 'Corona', 'Cerveza Corona Extra Botella 330 ml', 'México', 'UNIDAD'),
(1, 0, 7000, 36, 8, 35, 2111, 'Estrella Galicia Especial Lager representa la pasión gallega por la cerveza. Con un equilibrio perfecto entre maltas y lúpulos, ofrece un sabor redondo y un carácter refrescante que la ha hecho famosa mundialmente.', 'CER-0006', 'gestion-ventas/imagen-producto/C6', 'Estrella Galicia', 'Cerveza Estrella Galicia Especial Lager Botella 330 ml', 'España', 'UNIDAD'),
(1, 0, 4000, 21, 8, 34, 2111, 'Club Colombia Dorada en formato lata mantiene todo su sabor premium con la practicidad del aluminio. Perfecta para picnics, playa o cualquier ocasión donde la comodidad es importante.', 'CER-0007', 'gestion-ventas/imagen-producto/C7', 'Club Colombia', 'Cerveza Club Colombia Dorada Lata 330 ml', 'Colombia', 'UNIDAD'),
(1, 0, 5000, 32, 8, 35, 2111, 'Miller Lite Light Lager es ligera pero llena de sabor. Con menos calorías pero manteniendo el carácter cervecero, es la elección inteligente para quienes cuidan su consumo sin sacrificar calidad.', 'CER-0008', 'gestion-ventas/imagen-producto/C8', 'Miller', 'Cerveza Miller Lite Light Lager Botella 300 ml', 'Estados Unidos', 'UNIDAD'),
(1, 0, 4500, 13, 8, 35, 2111, 'Budweiser Lager es la cerveza de América. Con más de 140 años de tradición, ofrece un sabor limpio y refrescante que la ha convertido en un ícono mundial de la cerveza lager.', 'CER-0009', 'gestion-ventas/imagen-producto/C9', 'Budweiser', 'Cerveza Budweiser Lager Botella 250 ml', 'Estados Unidos', 'UNIDAD'),
(1, 0, 6500, 18, 8, 35, 2111, 'Heineken Lager es reconocida mundialmente por su sabor distintivo y su característico color verde. Con un proceso de fermentación único que le da su sabor inconfundible.', 'CER-0010', 'gestion-ventas/imagen-producto/C10', 'Heineken', 'Cerveza Heineken Lager Botella 330 ml', 'Países Bajos', 'UNIDAD'),
(1, 0, 8000, 17, 8, 35, 2111, 'Guinness Draught es la stout irlandesa más famosa del mundo. Con su característico color negro, espesa crema y notas a café y chocolate, ofrece una experiencia cervecera única e inolvidable.', 'CER-0011', 'gestion-ventas/imagen-producto/C11', 'Guinness', 'Cerveza Guinness Draught Botella 330 ml', 'Irlanda', 'UNIDAD'),
(1, 0, 5500, 12, 8, 35, 2111, 'Stella Artois combina 600 años de tradición belga con un sabor refinado y elegante. Con su proceso de saaz hopping, ofrece un perfil limpio y un finish suave que la distingue.', 'CER-0012', 'gestion-ventas/imagen-producto/C12', 'Stella Artois', 'Cerveza Stella Artois Botella 300 ml', 'Bélgica', 'UNIDAD'),
(1, 0, 3800, 25, 8, 34, 2111, 'Águila Cero ofrece toda la experiencia Águila pero sin alcohol. Perfecta para quienes buscan disfrutar del sabor cervecero en cualquier momento del día, sin compromisos.', 'CER-0013', 'gestion-ventas/imagen-producto/C13', 'Águila', 'Cerveza Águila Cero Sin Alcohol Lata 330 ml', 'Colombia', 'UNIDAD'),
(1, 0, 3500, 23, 8, 34, 2111, 'Poker Tradicional es la cerveza del corazón colombiano. Con más de 70 años de historia, mantiene su receta clásica que ha acompañado generaciones de colombianos.', 'CER-0014', 'gestion-ventas/imagen-producto/C14', 'Poker', 'Cerveza Poker Tradicional Colombiana en Lata 330 ml', 'Colombia', 'UNIDAD'),
(1, 22000, 10000, 19, 8, 34, 2111, 'Six Pack Club Colombia Dorada ofrece la cerveza premium colombiana en formato económico. Perfecta para reuniones, parches o para asegurar tu provisión de la cerveza de calidad que prefieres.', 'CER-0015', 'gestion-ventas/imagen-producto/C15', 'Club Colombia', 'Six Pack Cerveza Club Colombia Dorada Lata 330 ml', 'Colombia', 'PACK');

-- Insertar productos de Ron (Categoría 3)
INSERT INTO `producto` (`activo`, `precio_regular`, `precio`, `stock`, `categoria_id`, `subcategoria_id`, `usuario_id`, `descripcion`, `codigo_producto`, `imagen`, `marca`, `nombre`, `pais`, `tipo`) VALUES
(1, 200000, 97000, 50, 3, 20, 2106, 'Ron La Hechicera Reserva Familiar es una joya colombiana con más de 50 años de tradición. Elaborado con mieles de caña seleccionadas y añejado en barricas de roble americano, ofrece notas de vainilla, caramelo y frutos secos. Un ron de extraordinaria suavidad y complejidad que honra la herencia ronera colombiana.', 'RON-0001', 'gestion-ventas/imagen-producto/R1', 'La Hechicera', 'Ron La Hechicera Reserva Familiar 700 ml', 'Colombia', 'UNIDAD'),
(1, 220000, 115000, 40, 3, 20, 2106, 'Ron Zacapa Ámbar 12 Años es un ron guatemalteco de ultra premium, madurado en el sistema solera a 2,300 metros sobre el nivel del mar. Con notas de miel, frutas tropicales y especias dulces, ofrece una textura sedosa y un finish excepcionalmente largo. Considerado uno de los mejores rones del mundo.', 'RON-0002', 'gestion-ventas/imagen-producto/R2', 'Zacapa', 'Ron Zacapa Ámbar 12 Años Añejo Premium 750 ml', 'Guatemala', 'UNIDAD'),
(1, 45000, 27000, 21, 3, 20, 2106, 'Ron Medellín Añejo 8 Años representa la tradición antioqueña en cada botella. Añejado por 8 años en barricas de roble, desarrolla notas de caramelo, roble tostado y un sutil toque de especias. Un ron suave y accesible que captura el espíritu paisa.', 'RON-0003', 'gestion-ventas/imagen-producto/R3', 'Medellín', 'Ron Medellín Añejo 8 Años Botella 750 ml', 'Colombia', 'UNIDAD'),
(1, 35000, 21000, 18, 3, 20, 2106, 'Ron Medellín Añejo Tradicional es el ron colombiano por excelencia. Con un proceso de añejamiento que respeta las recetas ancestrales, ofrece un perfil balanceado con notas dulces y un finish cálido. La elección perfecta para disfrutar en compañía.', 'RON-0004', 'gestion-ventas/imagen-producto/R4', 'Medellín', 'Ron Medellín Añejo Tradicional Colombiano Botella de 750 ml', 'Colombia', 'UNIDAD'),
(1, 95000, 53000, 10, 3, 20, 2106, 'Ron Medellín Gran Solera 19 Años es la expresión premium de la marca. Con 19 años de añejamiento en sistema solera, desarrolla complejas notas de frutos secos, chocolate oscuro y especias exóticas. Un ron para paladares exigentes que buscan sofisticación.', 'RON-0005', 'gestion-ventas/imagen-producto/R5', 'Medellín', 'Ron Medellín Gran Solera 19 Años 750 ml', 'Colombia', 'UNIDAD'),
(1, 75000, 45000, 25, 3, 20, 2106, 'Ron Dominicano Barceló Añejo lleva la pasión caribeña en cada sorbo. Elaborado en República Dominicana con mieles de caña premium y añejado en barricas de roble, ofrece notas de vainilla, coco y frutas tropicales. Un ron suave con carácter dominicano.', 'RON-0006', 'gestion-ventas/imagen-producto/R6', 'Barceló', 'Ron Dominicano Barceló Añejo Botella 750 ml', 'República Dominicana', 'UNIDAD'),
(1, 55000, 33000, 55, 3, 22, 2106, 'Ron Bacardí Mandarina combina el ron blanco premium con el vibrante sabor de la mandarina natural. Refrescante y versátil, es perfecto para cocktails innovadores o para disfrutar con hielo. Lleva la mixología a un nivel de frescura cítrica.', 'RON-0007', 'gestion-ventas/imagen-producto/R7', 'Bacardí', 'Ron Bacardí Mandarina Saborizado Botella 750 ml', 'Puerto Rico', 'UNIDAD');

-- Insertar productos de Aguardiente (Categoría 9) - Sin subcategoría
INSERT INTO `producto` (`activo`, `precio_regular`, `precio`, `stock`, `categoria_id`, `subcategoria_id`, `usuario_id`, `descripcion`, `codigo_producto`, `imagen`, `marca`, `nombre`, `pais`, `tipo`) VALUES
(1, 45000, 23000, 27, 9, 39, 2112, 'Aguardiente Antioqueño Sin Azúcar 24 Grados es la esencia pura de la tradición paisa. Con su graduación alcohólica perfectamente balanceada y sin dulce añadido, permite apreciar las auténticas notas de anís y hierbas naturales. Ideal para quienes prefieren un aguardiente más seco y auténtico.', 'AGU-0001', 'gestion-ventas/imagen-producto/A1', 'Aguardiente Antioqueño', 'Aguardiente Antioqueño Sin Azucar 24 Grados 750 ml', 'Colombia', 'UNIDAD'),
(1, 42000, 25200, 15, 9, 39, 2112, 'Aguardiente Antioqueño Sin Azúcar mantiene el carácter tradicional pero sin el dulzor característico. Perfecto para paladares que buscan la esencia del aguardiente en su forma más pura, conservando el distintivo sabor a anís que ha hecho famosa esta marca.', 'AGU-0002', 'gestion-ventas/imagen-producto/A2', 'Aguardiente Antioqueño', 'Aguardiente Antioqueño Sin Azucar 750 ml', 'Colombia', 'UNIDAD'),
(1, 38000, 27500, 20, 9, 39, 2112, 'Aguardiente Amarillo Sin Azúcar ofrece una variación única en el mundo de los aguardientes. Con su característico color amarillo natural y sin adición de dulce, presenta un perfil más suave pero igualmente aromático. Una alternativa diferente para los conocedores.', 'AGU-0003', 'gestion-ventas/imagen-producto/A3', 'Aguardiente Antioqueño', 'Aguardiente Amarillo Sin Azucar 750 ml', 'Colombia', 'UNIDAD'),
(1, 55000, 33000, 25, 9, 39, 2112, 'Aguardiente Antioqueño Sin Azúcar en presentación familiar de 1000 ml. La misma calidad y sabor auténtico en un formato económico ideal para reuniones grandes y celebraciones. Lleva la tradición paisa a tus eventos especiales con el mejor precio por volumen.', 'AGU-0004', 'gestion-ventas/imagen-producto/A4', 'Aguardiente Antioqueño', 'Aguardiente Antioqueño Sin Azucar 1000 ml', 'Colombia', 'UNIDAD'),
(1, 48000, 24000, 30, 9, 39, 2112, 'Aguardiente Antioqueño Real Sin Azúcar representa la gama premium de los aguardientes sin dulce. Con un proceso de destilación refinado y un blend especial de hierbas, ofrece una experiencia más suave y compleja. Para quienes buscan lo mejor en aguardientes secos.', 'AGU-0005', 'gestion-ventas/imagen-producto/A5', 'Aguardiente Antioqueño', 'Aguardiente Antioqueño Real Sin Azucar 750 ml', 'Colombia', 'UNIDAD');

-- Insertar productos de Vodka (Categoría 4)
INSERT INTO `producto` (`activo`, `precio_regular`, `precio`, `stock`, `categoria_id`, `subcategoria_id`, `usuario_id`, `descripcion`, `codigo_producto`, `imagen`, `marca`, `nombre`, `pais`, `tipo`) VALUES
(1, 0, 25000, 48, 4, 24, 2107, 'Vodka Smirnoff Green Apple combina la suavidad del vodka premium con el refrescante sabor a manzana verde. Perfecto para shots energéticos o para crear cocktails frutales innovadores. Una explosión de frescura en cada sorbo.', 'VOD-0001', 'gestion-ventas/imagen-producto/VK1', 'Smirnoff', 'Vodka Smirnoff Green Apple Botella 275 ml', 'Rusia', 'UNIDAD'),
(1, 85000, 49000, 77, 4, 23, 2107, 'Vodka Absolut Original es el ícono sueco de calidad y pureza. Destilado desde 1879 con ingredientes naturales de los campos suecos, ofrece una suavidad excepcional y un perfil limpio. La base perfecta para cualquier cocktail premium.', 'VOD-0002', 'gestion-ventas/imagen-producto/VK2', 'Absolut', 'Vodka Absolut Original Premium Sueco Botella 700 ml', 'Suecia', 'UNIDAD'),
(1, 95000, 53000, 21, 4, 23, 2107, 'Vodka Tito\s Handmade es el primer vodka americano destilado en Texas. Elaborado con maíz 100% amarillo y destilado 6 veces, ofrece una excepcional suavidad y un finish limpio. La elección artesanal que revolucionó el mercado.', 'VOD-0003', 'gestion-ventas/imagen-producto/VK3', 'Tito\s', 'Vodka Tito\s Handmade Original Premium 750 ml', 'Estados Unidos', 'UNIDAD'),
(1, 32000, 19200, 13, 4, 24, 2107, 'Vodka Smirnoff X1 Lulo captura el sabor tropical y ácido del lulo colombiano. Con una combinación perfecta entre la suavidad del vodka y la frescura de esta fruta exótica, es ideal para cocktails veraniegos y momentos festivos.', 'VOD-0004', 'gestion-ventas/imagen-producto/VK4', 'Smirnoff', 'Vodka Smirnoff X1 Lulo Saborizado 375 ml', 'Rusia', 'UNIDAD'),
(1, 0, 45000, 20, 4, 24, 2107, 'Vodka Smirnoff Sabor Lulo Premium lleva la experiencia tropical a otro nivel. Con un mayor porcentaje de jugo natural de lulo, ofrece un sabor auténtico y refrescante que evoca la biodiversidad colombiana en cada trago.', 'VOD-0005', 'gestion-ventas/imagen-producto/VK5', 'Smirnoff', 'Vodka Smirnoff Sabor Lulo Premium 750 ml', 'Rusia', 'UNIDAD'),
(1, 38000, 16000, 30, 4, 23, 2107, 'Vodka Smirnoff Red Label es el clásico que definió el estándar de los vodkas a nivel mundial. Con su proceso de destilación triple y filtrado con carbón, garantiza pureza y suavidad. Confiable y versátil para cualquier ocasión.', 'VOD-0006', 'gestion-ventas/imagen-producto/VK6', 'Smirnoff', 'Vodka Smirnoff Red Label Clásico 700 ml', 'Rusia', 'UNIDAD');

-- Insertar productos de Ginebra (Categoría 6)
INSERT INTO `producto` (`activo`, `precio_regular`, `precio`, `stock`, `categoria_id`, `subcategoria_id`, `usuario_id`, `descripcion`, `codigo_producto`, `imagen`, `marca`, `nombre`, `pais`, `tipo`) VALUES
(1, 55000, 33000, 27, 6, 29, 2109, 'Gordon\s London Dry Gin es la ginebra más vendida del mundo desde 1769. Con su receta secreta que combina bayas de enebro y botánicos seleccionados, ofrece el equilibrio perfecto entre tradición y frescura. La base clásica para el gin-tonic perfecto.', 'GIN-0001', 'gestion-ventas/imagen-producto/G1', 'Gordon\s', 'Gordon\s London Dry Gin Clásica 700 ml', 'Reino Unido', 'UNIDAD'),
(1, 95000, 57000, 31, 6, 29, 2109, 'Tanqueray London Dry Gin es sinónimo de excelencia desde 1830. Destilada con cuatro botánicos clave y un proceso que respeta la tradición, ofrece un perfil nítido y complejo. La elección de los puristas del gin-tonic.', 'GIN-0002', 'gestion-ventas/imagen-producto/G2', 'Tanqueray', 'Ginebra Tanqueray London Dry Gin 700 ml', 'Reino Unido', 'UNIDAD'),
(1, 185000, 97000, 22, 6, 29, 2109, 'Hendrick\s Gin es una obra maestra escocesa que desafía convenciones. Infundida con rosa de Bulgaria y pepino, ofrece un perfil singularmente suave y aromático. Para quienes buscan una experiencia gin única e inolvidable.', 'GIN-0003', 'gestion-ventas/imagen-producto/G3', 'Hendrick\s', 'Ginebra Premium Hendrick\s Importada Escocesa 750 ml', 'Escocia', 'UNIDAD'),
(1, 220000, 106000, 18, 6, 29, 2109, 'Monkey 47 Dry Gin es la ginebra alemana de culto con 47 botánicos diferentes. Combinando tradición británica y esencia de la Selva Negra, ofrece una complejidad extraordinaria. Cada botella cuenta una historia de pasión artesanal.', 'GIN-0004', 'gestion-ventas/imagen-producto/G4', 'Monkey 47', 'Ginebra Premium Monkey 47 Dry Gin Botella 500 ml', 'Alemania', 'UNIDAD'),
(1, 120000, 78000, 39, 6, 31, 2109, 'Tanqueray Rangpur reinventa el clásico con una explosión cítrica. Infundida con rangpur, lima y otros cítricos exóticos, ofrece un perfil refrescante y vibrante. Perfecta para cocktails veraniegos y combinaciones innovadoras.', 'GIN-0005', 'gestion-ventas/imagen-producto/G5', 'Tanqueray', 'Ginebra Premium Tanqueray Rangpur con Cítricos 750 ml', 'Reino Unido', 'UNIDAD'),
(1, 75000, 48000, 15, 6, 29, 2109, 'Beefeater London Dry Gin es la esencia de Londres en cada botella. Con 9 botánicos cuidadosamente seleccionados y un proceso de destilación que data de 1863, ofrece un carácter robusto y equilibrado. La ginebra de los maestros mixólogos.', 'GIN-0006', 'gestion-ventas/imagen-producto/G6', 'Beefeater', 'Ginebra Premium Beefeater London Dry 700 ml', 'Reino Unido', 'UNIDAD');
