document.addEventListener("DOMContentLoaded", () => {
    // Efecto glassmorphism solo al hacer scroll
    const header = document.querySelector('.content__header');

    window.addEventListener('scroll', () => {
        if (window.scrollY > 10) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
    });

    //Menú desplegable del perfil
    const subMenu = document.getElementById("SubMenu");
    const profileImage = document.getElementById("user__admin");

    if (subMenu && profileImage) {
        profileImage.addEventListener("click", function (e) {
            e.stopPropagation(); // Evita que el click cierre el menú inmediatamente
            subMenu.classList.toggle("open__menu");
        });

        // Cerrar menú al hacer clic fuera
        document.addEventListener("click", function (e) {
            if (!subMenu.contains(e.target) && !profileImage.contains(e.target)) {
                subMenu.classList.remove("open__menu");
            }
        });
    }

    //abrir la notificaciones del admin
    const notifIcon = document.getElementById("notifIcon");
    const notifMenu = document.getElementById("notifMenu");

    notifIcon.addEventListener("click", (e) => {
        e.stopPropagation();
        notifMenu.classList.toggle("open");
    });

    // cerrar al hacer clic fuera
    document.addEventListener("click", (e) => {
        if (!notifMenu.contains(e.target) && !notifIcon.contains(e.target)) {
            notifMenu.classList.remove("open");
        }
    });

    //Select de pais
    const paises = [
        // América del Norte
        "Estados Unidos", "México",

        // América Central y el Caribe
        "Guatemala", "Puerto Rico", "República Dominicana",

        // América del Sur
        "Argentina", "Brasil", "Chile", "Colombia",

        // Europa
        "Alemania", "Bélgica", "España",
        "Francia", "Irlanda", "Países Bajos", "Reino Unido", "Suiza",
        "Noruega", "Suecia", "Italia", "Rusia", "Ucrania", "Escocia"
    ];

    // Obtener el elemento select
    const selectPaises = document.getElementById("páises");

    // Agregar los países al select
    paises.forEach(pais => {
        const option = document.createElement("option");
        option.value = pais.toLowerCase().replace(/\s+/g, "_");
        option.textContent = pais;
        selectPaises.appendChild(option);
    });

    //Select de categoria principal y secundaria
    const categoriaPrincipal = document.getElementById("categoriaPrincipal");
    const categoriaSecundaria = document.getElementById("categoriaSecundaria");

    // Mapeo de IDs de categorías a nombres
    const mapeoCategorias = {};
    const mapeoSubCategorias = {};

    // Inicializar mapeo de categorías principales
    function inicializarMapeoCategorias() {
        const opcionesCategoria = categoriaPrincipal.querySelectorAll('option');
        opcionesCategoria.forEach(option => {
            if (option.value && option.value !== "") {
                mapeoCategorias[option.value] = option.textContent;
            }
        });
        console.log('Mapeo de categorías:', mapeoCategorias);
    }

    // Actualizar mapeo de subcategorías
    function actualizarMapeoSubCategorias() {
        // Limpiar mapeo anterior
        for (const key in mapeoSubCategorias) {
            delete mapeoSubCategorias[key];
        }

        const opcionesSubCategoria = categoriaSecundaria.querySelectorAll('option');
        opcionesSubCategoria.forEach(option => {
            if (option.value && option.value !== "") {
                mapeoSubCategorias[option.value] = option.textContent;
            }
        });
        console.log('Mapeo de subcategorías:', mapeoSubCategorias);
    }

    // Inicializar cuando cargue la página
    inicializarMapeoCategorias();

    // Cuando cambia la categoría principal
    categoriaPrincipal.addEventListener("change", function () {
        const categoriaId = this.value;

        // Limpiar las opciones anteriores
        categoriaSecundaria.innerHTML = '<option disabled selected>Categoría secundaria</option>';

        if (categoriaId) {
            // Hacer petición al servidor para obtener las subcategorías
            fetch(`/admin/producto/subcategorias/${categoriaId}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error al cargar subcategorías');
                    }
                    return response.json();
                })
                .then(subcategorias => {
                    // Agregar las nuevas opciones
                    subcategorias.forEach(subcategoria => {
                        const option = document.createElement("option");
                        option.value = subcategoria.id;
                        option.textContent = subcategoria.nombre;
                        categoriaSecundaria.appendChild(option);
                    });

                    // Actualizar el mapeo de subcategorías
                    actualizarMapeoSubCategorias();

                    // Si solo hay una opción, seleccionarla automáticamente
                    if (subcategorias.length === 1) {
                        categoriaSecundaria.selectedIndex = 1;
                    }

                    // Actualizar marcas después de cargar subcategorías
                    actualizarMarcas();
                })
                .catch(error => {
                    console.error('Error:', error);
                    const errorElement = document.querySelector('.error--CategoriaSecundaria');
                    errorElement.textContent = 'Error al cargar las subcategorías. Intente nuevamente.';
                    errorElement.style.display = 'block';
                });
        }
    });

    // === MARCAS DEPENDIENTES ===
    const selectPais = document.getElementById("páises");
    const selectMarca = document.getElementById("marca");

    // Diccionario de marcas según categoría principal, país y secundaria
    const marcas = {
        Vino: {
            Italia: {
                "Vino Tinto": ["Antinori", "Tignanello", "Frescobaldi", "Gaja", "Barolo", "Brunello di Montalcino"],
                "Vino Blanco": ["Santa Margherita", "Masi", "Ruffino", "Pinot Grigio", "Soave"],
                "Vino Rosado": ["Bolla", "Castello Banfi", "Lamberti", "Casal Thaulero"],
                "Vino Espumoso": ["Prosecco", "Franciacorta", "Asti Spumante", "Lambrusco"],
                "Champagne": ["Ferrari", "Bellavista", "Ca' del Bosco"],
                "Cava": ["Freixenet Italia", "Codorníu Italia"],
                "Prosecco": ["Mionetto", "Zonin", "Bisol", "Ruggeri"],
                "Lambruscos": ["Riunite", "Cavicchioli", "Medici Ermete"]
            },
            España: {
                "Vino Tinto": ["Marqués de Riscal", "Torres", "Vega Sicilia", "Protos", "Rioja", "Ribera del Duero"],
                "Vino Blanco": ["Faustino", "Campo Viejo", "Albariño", "Verdejo"],
                "Vino Rosado": ["Codorníu", "Freixenet", "Marqués de Cáceres", "Navarra"],
                "Vino Espumoso": ["Juvé & Camps", "Codorníu", "Freixenet", "Gramona"],
                "Champagne": ["Raventós i Blanc", "Parxet", "Agustí Torelló"],
                "Cava": ["Codorníu", "Freixenet", "Gramona", "Recaredo"],
                "Prosecco": ["Marqués de Monistrol", "Raimat"],
                "Lambruscos": ["Marqués de Gelida", "Castellroig"]
            },
            Francia: {
                "Vino Tinto": ["Château Margaux", "Château Lafite Rothschild", "Louis Jadot", "Bordeaux", "Burgundy"],
                "Vino Blanco": ["Joseph Drouhin", "Château d'Yquem", "Sancerre", "Chablis"],
                "Vino Rosado": ["Moët & Chandon", "Dom Pérignon", "Whispering Angel", "Miraval"],
                "Vino Espumoso": ["Veuve Clicquot", "Perrier-Jouët", "Laurent-Perrier", "Taittinger"],
                "Champagne": ["Dom Pérignon", "Krug", "Bollinger", "Ruinart"],
                "Cava": ["Charles de Fère", "Jaillance"],
                "Prosecco": ["Bouchard Aîné", "Louis Bouillot"],
                "Lambruscos": ["Barton & Guestier", "Calvet"]
            },
            Chile: {
                "Vino Tinto": ["Concha y Toro", "Casillero del Diablo", "Montes", "Carmen", "Santa Rita"],
                "Vino Blanco": ["Santa Rita", "Undurraga", "Errazuriz", "Los Vascos"],
                "Vino Rosado": ["Tarapacá", "Cousiño Macul", "San Pedro", "Emiliana"],
                "Vino Espumoso": ["Valdivieso", "Undurraga Brut", "Cono Sur", "Tabalí"],
                "Champagne": ["Santa Digna", "TerraMater"],
                "Cava": ["Miguel Torres", "Leyda"],
                "Prosecco": ["Casas del Bosque", "Amayna"],
                "Lambruscos": ["Viña Maipo", "Viña San Esteban"]
            },
            Argentina: {
                "Vino Tinto": ["Catena Zapata", "Luigi Bosca", "Rutini", "Trapiche", "Norton"],
                "Vino Blanco": ["Norton", "Bianchi", "Trumpeter", "Alamos"],
                "Vino Rosado": ["El Enemigo", "Navarro Correas", "Finca Las Moras", "Zuccardi"],
                "Vino Espumoso": ["Chandon", "Baron B", "Finca Sophenia", "Finca La Celia"],
                "Champagne": ["Mendoza", "Andeluna", "Rutini"],
                "Cava": ["Bodega Norton", "Bodega Salentein"],
                "Prosecco": ["Bodega Vistalba", "Bodega Atamisque"],
                "Lambruscos": ["Bodega Colomé", "Bodega El Esteco"]
            }
        },

        Whisky: {
            Escocia: {
                "Single Malt": ["Glenfiddich", "Macallan", "Lagavulin", "Laphroaig", "Glenlivet"],
                "Blended Malt": ["Monkey Shoulder", "Compass Box", "Johnnie Walker Green"],
                "Single Grain": ["Haig Club", "Cameron Bridge", "Girvan"],
                "Blended Grain": ["Hedges & Butler", "Black Barrel"],
                "Blended Scotch": ["Johnnie Walker", "Chivas Regal", "Ballantine's", "Dewar's"],
                "Bourbon": ["Glen Scotia", "Auchentoshan"],
                "Whisky de Centeno": ["Bruichladdich", "Ardbeg"],
                "Whisky de Trigo": ["Glenmorangie", "Balvenie"],
                "Tennessee Whisky": ["Jack Daniel's Single Malt"]
            },
            "Estados Unidos": {
                "Single Malt": ["Woodford Reserve", "Stranahan's", "Westland"],
                "Blended Malt": ["Bulleit", "High West", "WhistlePig"],
                "Single Grain": ["Buffalo Trace", "Heaven Hill"],
                "Blended Grain": ["Jim Beam", "Evan Williams"],
                "Blended Scotch": ["Jack Daniel's", "Seagram's"],
                "Bourbon": ["Maker's Mark", "Wild Turkey", "Four Roses"],
                "Whisky de Centeno": ["Rittenhouse", "Old Overholt"],
                "Whisky de Trigo": ["Bernheim", "W.L. Weller"],
                "Tennessee Whisky": ["Jack Daniel's", "George Dickel"]
            },
            Irlanda: {
                "Single Malt": ["Bushmills", "Teeling", "Knappogue Castle"],
                "Blended Malt": ["Jameson", "Tullamore D.E.W.", "Powers"],
                "Single Grain": ["Green Spot", "Yellow Spot"],
                "Blended Grain": ["Paddy", "Crested Ten"],
                "Blended Scotch": ["Jameson Caskmates", "Redbreast"],
                "Bourbon": ["Writer's Tears", "Method and Madness"],
                "Whisky de Centeno": ["Dingle", "West Cork"],
                "Whisky de Trigo": ["Kilbeggan", "Clontarf"],
                "Tennessee Whisky": ["Jameson Black Barrel"]
            },
            Japón: {
                "Single Malt": ["Yamazaki", "Hakushu", "Hibiki", "Nikka"],
                "Blended Malt": ["Nikka Coffey", "Suntory Toki"],
                "Single Grain": ["Chita", "Nikka Coffey Grain"],
                "Blended Grain": ["Akashi", "Eigashima"],
                "Blended Scotch": ["Suntory Royal", "Nikka Super"],
                "Bourbon": ["Yamazaki Bourbon Barrel", "Hakushu American Oak"],
                "Whisky de Centeno": ["Nikka Rye", "Mars Komagatake"],
                "Whisky de Trigo": ["Ichiro's Malt", "White Oak"],
                "Tennessee Whisky": ["Suntory Old"]
            }
        },

        Ron: {
            Cuba: {
                "Ron Blanco": ["Havana Club", "Santiago", "Caney", "Varadero"],
                "Ron Dorado": ["Havana Club Añejo", "Santiago de Cuba", "Legendario"],
                "Ron Añejo": ["Havana Club 7 años", "Santiago de Cuba 11 años", "Ron Vigía"],
                "Ron Oscuro": ["Havana Club Selección de Maestros", "Ron Mulata"],
                "Ron Especiado": ["Havana Club Especial", "Santiago de Cuba Especiado"]
            },
            "República Dominicana": {
                "Ron Blanco": ["Brugal", "Barceló", "Bermúdez", "Macorix"],
                "Ron Dorado": ["Brugal Añejo", "Barceló Dorado", "Bermúdez Gold"],
                "Ron Añejo": ["Brugal Añejo", "Barceló Añejo", "Bermúdez Añejo"],
                "Ron Oscuro": ["Brugal Extra Viejo", "Barceló Gran Añejo"],
                "Ron Especiado": ["Brugal Limón", "Barceló Coco", "Bermúdez Especiado"]
            },
            "Puerto Rico": {
                "Ron Blanco": ["Bacardi", "Don Q", "Palo Viejo", "Ron del Barrilito"],
                "Ron Dorado": ["Bacardi Gold", "Don Q Gold", "Ron del Barrilito 2 Estrellas"],
                "Ron Añejo": ["Bacardi 8", "Don Q Añejo", "Ron del Barrilito 3 Estrellas"],
                "Ron Oscuro": ["Bacardi Black", "Don Q Reserva 7", "Palo Viejo Oscuro"],
                "Ron Especiado": ["Bacardi Spiced", "Don Q Limón", "Palo Viejo Coco"]
            },
            Venezuela: {
                "Ron Blanco": ["Santa Teresa", "Cacique", "Pampero", "Diplomático"],
                "Ron Dorado": ["Santa Teresa Gran Reserva", "Cacique 500", "Pampero Aniversario"],
                "Ron Añejo": ["Diplomático Reserva Exclusiva", "Santa Teresa 1796", "Pampero Especial"],
                "Ron Oscuro": ["Diplomático Mantuano", "Santa Teresa Selecto", "Cacique Antiguo"],
                "Ron Especiado": ["Santa Teresa Naranja", "Diplomático Planas", "Cacique Especiado"]
            }
        },

        Vodka: {
            Rusia: {
                "Vodka Clásico": ["Russian Standard", "Beluga", "Moskovskaya", "Stolichnaya"],
                "Vodka Saborizado": ["Nemiroff", "Zyr", "Khortytsa", "Green Mark"]
            },
            Polonia: {
                "Vodka Clásico": ["Wyborowa", "Belvedere", "Chopin", "Żubrówka"],
                "Vodka Saborizado": ["Żubrówka", "Soplica", "Krupnik", "Polar Ice"]
            },
            Suecia: {
                "Vodka Clásico": ["Absolut", "Karlsson's", "Purity", "Explorer"],
                "Vodka Saborizado": ["Absolut Citron", "Absolut Vanilla", "Absolut Raspberri"]
            },
            "Estados Unidos": {
                "Vodka Clásico": ["Grey Goose", "Tito's", "Smirnoff", "Skyy"],
                "Vodka Saborizado": ["Smirnoff Saborizado", "UV Vodka", "Three Olives"]
            }
        },

        Tequila: {
            México: {
                "Blanco": ["Jose Cuervo", "Don Julio Blanco", "1800 Silver", "Patrón Silver", "Herradura Blanco"],
                "Reposado": ["Herradura", "Cazadores", "Patrón Reposado", "Don Julio Reposado", "El Jimador"],
                "Añejo": ["Don Julio Añejo", "Gran Centenario", "Herradura Añejo", "Patrón Añejo", "Cazadores Añejo"],
                "Extra Añejo": ["Don Julio 70", "Herradura Ultra", "Patrón Extra Añejo", "Clase Azul"]
            }
        },

        Ginebra: {
            "Reino Unido": {
                "London Dry": ["Beefeater", "Tanqueray", "Gordon's", "Bombay Sapphire"],
                "Old Tom": ["Hayman's", "Jensen's", "Ransom"],
                "Ginebra de Sabor": ["Hendrick's", "Bloom", "The Botanist"]
            },
            "Países Bajos": {
                "London Dry": ["Ketel One", "Bols", "Geneva", "Nolet's"],
                "Old Tom": ["Bobby's", "Van Wees", "De Kuyper"],
                "Ginebra de Sabor": ["Bols Genever", "Filliers", "Rutte"]
            },
            España: {
                "London Dry": ["Larios", "Nordés", "Gin Mare", "Puerto de Indias"],
                "Old Tom": ["Siderit", "Ginraw", "Magellan"],
                "Ginebra de Sabor": ["Puerto de Indias Fresa", "Larios 12", "Gin Eva"]
            }
        },

        Mezcal: {
            México: {
                "Blanco": ["Monte Alban", "Zignum", "Ilegal", "Alipus"],
                "Madurado en Vidrio": ["Del Maguey", "Los Danzantes", "Rey Campero", "Montelobos"]
            }
        },

        Cerveza: {
            Colombia: {
                "Nacional": ["Aguila", "Poker", "Club Colombia", "Costeña", "Pilsen"],
                "Importada": ["Heineken", "Budweiser", "Corona", "Stella Artois"]
            },
            México: {
                "Nacional": ["Corona", "Modelo", "Pacifico", "Tecate", "Sol"],
                "Importada": ["Heineken", "Budweiser", "Stella Artois", "Guinness"]
            },
            Alemania: {
                "Nacional": ["Paulaner", "Beck's", "Bitburger", "Warsteiner", "Krombacher"],
                "Importada": ["Guinness", "Stella Artois", "Corona", "Heineken"]
            },
            Bélgica: {
                "Nacional": ["Stella Artois", "Leffe", "Hoegaarden", "Duvel", "Chimay"],
                "Importada": ["Heineken", "Corona", "Budweiser", "Guinness"]
            }
        },

        Aguardiente: {
            Colombia: {
                Nacional: ["Aguardiente Antioqueño", "Cristal", "Néctar", "Blanco del Valle", "Tapa Roja"]
            }
        },

        Brandy: {
            Francia: {
                "Cognac": ["Hennessy", "Remy Martin", "Courvoisier", "Martell", "Camus"],
                "Armagnac": ["Darroze", "Janneau", "Marquis de Montesquiou", "Baron de Sigognac"]
            },
            España: {
                "Brandy Español": ["Fundador", "Terry", "Cardenal Mendoza", "Magno", "Soberano"],
                "Cognac": ["Torres", "Mascaró", "Gran Duque de Alba"],
                "Armagnac": ["Bodegas Williams", "Bodegas Osborne"]
            }
        }
    };

    // Función para actualizar las marcas (SOLUCIÓN 2 IMPLEMENTADA)
    function actualizarMarcas() {
        const categoriaId = categoriaPrincipal.value;
        const subCategoriaId = categoriaSecundaria.value;
        const paisSeleccionado = selectPais.options[selectPais.selectedIndex]?.text || "";

        // Limpiar el select de marcas
        selectMarca.innerHTML = '<option disabled selected>Marca</option>';

        // Verificar que haya selección en los tres
        if (categoriaId && subCategoriaId && paisSeleccionado) {
            const categoriaNombre = mapeoCategorias[categoriaId];
            const subCategoriaNombre = mapeoSubCategorias[subCategoriaId];

            console.log("Buscando marcas para:", {
                categoriaId: categoriaId,
                categoriaNombre: categoriaNombre,
                subCategoriaId: subCategoriaId,
                subCategoriaNombre: subCategoriaNombre,
                pais: paisSeleccionado
            });

            if (categoriaNombre && subCategoriaNombre) {
                const lista = marcas[categoriaNombre]?.[paisSeleccionado]?.[subCategoriaNombre];

                if (lista) {
                    lista.forEach(marca => {
                        const option = document.createElement("option");
                        option.value = marca;
                        option.textContent = marca;
                        selectMarca.appendChild(option);
                    });
                    console.log("Marcas encontradas:", lista);
                } else {
                    const option = document.createElement("option");
                    option.disabled = true;
                    option.textContent = "No hay marcas disponibles para esta combinación";
                    selectMarca.appendChild(option);
                    console.log("No se encontraron marcas para:", categoriaNombre, paisSeleccionado, subCategoriaNombre);
                }
            } else {
                console.log("No se pudo encontrar el nombre para los IDs:", {
                    categoriaId: categoriaId,
                    subCategoriaId: subCategoriaId
                });
            }
        }
    }

    // Escuchar cambios en los tres selects
    categoriaPrincipal.addEventListener("change", actualizarMarcas);
    selectPais.addEventListener("change", actualizarMarcas);
    categoriaSecundaria.addEventListener("change", actualizarMarcas);

    // === SOLUCIÓN 2: FUNCIONES PARA LIMPIAR PRECIOS ===

    // Función para limpiar formato de precios antes de enviar
    function limpiarFormatoPrecio(valorFormateado) {
        if (!valorFormateado) return "0";

        // Eliminar símbolos de moneda, puntos, espacios y comas
        const valorLimpio = valorFormateado
            .replace(/[^\d,]/g, '')  // Mantener solo números y comas
            .replace(',', '.');      // Convertir coma decimal a punto

        return valorLimpio || "0";
    }

    // Función para preparar los datos antes del envío
    function prepararDatosParaEnvio() {
        // Limpiar precio regular
        const precioRegularInput = document.getElementById('precioRegular');
        if (precioRegularInput) {
            const valorLimpio = limpiarFormatoPrecio(precioRegularInput.value);
            // Actualizar el campo original con el valor limpio
            precioRegularInput.value = valorLimpio;
            console.log("Precio regular limpio:", valorLimpio);
        }

        // Limpiar precio de venta
        const precioVentaInput = document.getElementById('precioVenta');
        if (precioVentaInput) {
            const valorLimpio = limpiarFormatoPrecio(precioVentaInput.value);
            // Actualizar el campo original con el valor limpio
            precioVentaInput.value = valorLimpio;
            console.log("Precio venta limpio:", valorLimpio);
        }

        // Actualizar campo oculto de descripción
        const descripcionHidden = document.getElementById('descripcionHidden');
        if (descripcionHidden && quill) {
            descripcionHidden.value = quill.root.innerHTML;
        }
    }

    //input de precios
    function formatCOP(value) {
        const number = Number(value.replace(/\D/g, '')); // eliminar caracteres no numéricos
        if (isNaN(number)) return '';
        return number.toLocaleString('es-CO', {
            style: 'currency',
            currency: 'COP',
            maximumFractionDigits: 0
        });
    }

    function setupMoneyInput(id) {
        const input = document.getElementById(id);

        input.addEventListener('input', e => {
            const value = e.target.value.replace(/\D/g, '');
            if (value) {
                e.target.value = formatCOP(value);
            } else {
                e.target.value = '';
            }
        });

        input.addEventListener('keydown', e => {
            // Permitir subir o bajar de mil en mil con flechas ↑ ↓
            const currentValue = Number(input.value.replace(/\D/g, '')) || 0;
            if (e.key === 'ArrowUp') {
                e.preventDefault();
                input.value = formatCOP(currentValue + 1000);
            }
            if (e.key === 'ArrowDown') {
                e.preventDefault();
                input.value = formatCOP(Math.max(0, currentValue - 1000));
            }
        });
    }

    // aplicar a ambos inputs
    setupMoneyInput('precioRegular');
    setupMoneyInput('precioVenta');

    // Imagen del producto
    const boxImagen = document.querySelector('.formulario__boximagen');
    const textoBox = document.querySelector('.boximagen__texto');
    const errorFormato = document.querySelector('.error--formato');
    const errorVacio = document.querySelector('.error--vacio');
    const inputFile = document.getElementById('fileInput');
    const previewContainer = document.getElementById('previewContainer');

    const formatosPermitidos = ["image/jpeg", "image/png", "image/webp"];

    // Al hacer clic en el contenedor, abrir explorador
    boxImagen.addEventListener('click', () => inputFile.click());

    // Escuchar cambio del input
    inputFile.addEventListener('change', (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // Validar formato
        if (!formatosPermitidos.includes(file.type)) {
            errorFormato.style.display = 'block';
            errorVacio.style.display = 'none';
            previewContainer.innerHTML = '';
            previewContainer.style.display = 'none';
            boxImagen.classList.remove('imagen-activa');
            return;
        }

        // Si el formato es válido → ocultar errores
        errorFormato.style.display = 'none';
        errorVacio.style.display = 'none';
        boxImagen.style.border = '1px solid #ddd';

        // Crear barra de carga
        previewContainer.innerHTML = `<div class="progress-bar"></div>`;
        previewContainer.style.display = 'flex';
        boxImagen.classList.add('imagen-activa');

        const progressBar = previewContainer.querySelector('.progress-bar');

        // Simular carga
        setTimeout(() => {
            const img = document.createElement('img');
            img.src = URL.createObjectURL(file);

            previewContainer.innerHTML = '';
            previewContainer.appendChild(img);

            // Crear botón eliminar (solo si no existe)
            let removeBtn = boxImagen.querySelector('.remove-image');
            if (!removeBtn) {
                removeBtn = document.createElement('span');
                removeBtn.classList.add('remove-image');
                removeBtn.textContent = 'Eliminar imagen';
                boxImagen.appendChild(removeBtn);
            }

            // Acción eliminar
            removeBtn.addEventListener('click', (e) => {
                e.stopPropagation(); // evita que abra el explorador

                // Eliminar imagen y botón
                previewContainer.style.display = 'none';
                previewContainer.innerHTML = '';
                boxImagen.classList.remove('imagen-activa');
                removeBtn.remove();
                inputFile.value = ''; // limpiar el input
            });
        }, 2000);
    });

    //Descripcion de un producto
    const toolbarOptions = [
        [{ 'font': [] }, { 'size': [] }],           // Tipografía y tamaño
        ['bold', 'italic', 'underline', 'strike'],  // Estilo de texto
        [{ 'color': [] }, { 'background': [] }],    // Color del texto y fondo
        [{ 'script': 'sub' }, { 'script': 'super' }],// Sub/superíndice
        [{ 'header': '1' }, { 'header': '2' }, 'blockquote', 'code-block'],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'indent': '-1' }, { 'indent': '+1' }],
        [{ 'align': [] }],
        ['link', 'image', 'video'],                 // Enlaces, imágenes, videos
        ['clean']                                   // Limpiar formato
    ];

    const quill = new Quill('#editor-container', {
        modules: { toolbar: toolbarOptions },
        theme: 'snow'
    });

    // Actualizar campo oculto con el contenido HTML de Quill
    const descripcionHidden = document.getElementById('descripcionHidden');

    quill.on('text-change', function() {
        // Obtener el contenido HTML del editor
        const contenidoHTML = quill.root.innerHTML;

        // Actualizar el campo oculto
        if (descripcionHidden) {
            descripcionHidden.value = contenidoHTML;
        }
    });

    // Inicializar el campo oculto con contenido vacío
    if (descripcionHidden) {
        descripcionHidden.value = quill.root.innerHTML;
    }

    //Validaciones del formulario de agregar producto
    const fieldsProducto = {
        nombre: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,}$/, errorMessage: "El nombre debe tener al menos 3 letras." },
        codigoProducto: { regex: /^#[A-Za-z0-9]+$/, errorMessage: "El código del producto debe iniciar con el símbolo # y contener solo letras o números después." },
        stock: { regex: /^[0-9][0-9]*$/, errorMessage: "Por favor, ingresa una cantidad de stock válida." }, // ACTUALIZADO: acepta 0
        precioRegular: { regex: /^(?!0+(?:[.,]0+)?$)\d+(?:[.,]\d{1,2})?$/, errorMessage: "Por favor, ingresa un precio válido mayor que cero." },
        precioVenta: { regex: /^(?!0+(?:[.,]0+)?$)\d+(?:[.,]\d{1,2})?$/, errorMessage: "Por favor, ingresa un precio válido mayor que cero." }
    };

    // obtenemos los select
    const selectCategoriaPrincipal = document.getElementById("categoriaPrincipal");
    const errorCategoriaPrincipal = document.querySelector(".error--CategoriaPrincipal");
    const selectpáises = document.getElementById("páises");
    const errorpáises = document.querySelector(".error--páises");
    const selectCategoriaSecundaria = document.getElementById("categoriaSecundaria");
    const errorCategoriaSecundaria = document.querySelector(".error--CategoriaSecundaria");
    const selectmarca = document.getElementById("marca");
    const errormarca = document.querySelector(".error--marca");
    const selecttipoProducto = document.getElementById("tipoProducto");
    const errortipoProducto = document.querySelector(".error--tipoProducto");
    const selecttipoProveedores = document.getElementById("Proveedores");
    const errortipoProveedores = document.querySelector(".error--Proveedores");

    Object.keys(fieldsProducto).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        const inputBox = input.closest(".formulario__box");
        const checkIcon = inputBox.querySelector(".ri-check-line");
        const errorIcon = inputBox.querySelector(".ri-close-line");
        const errorMessage = inputBox.nextElementSibling;

        input.addEventListener("input", () => {
            let value = input.value.trim();

            if (fieldId === "precioRegular" || fieldId === "precioVenta") {
                value = value.replace(/[^\d]/g, ''); // elimina $, puntos, etc.
            }

            const label = inputBox.querySelector("box__label");

            if (value === "") {
                inputBox.classList.remove("input-error");
                checkIcon.style.display = "none";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "";
                if (label) label.classList.remove("error"); //lo reseteas
            } else if (fieldsProducto[fieldId].regex.test(value)) {
                checkIcon.style.display = "inline-block";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "2px solid #0034de";
                inputBox.classList.remove("input-error");
                if (label) label.classList.remove("error"); //quitar rojo cuando es válido
            } else {
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.style.display = "block";
                input.style.border = "2px solid #fd1f1f";
                inputBox.classList.add("input-error");
                if (label) label.classList.add("error"); // marcar rojo cuando es inváli
            }
        });
    });

    // Ocultar advertencias y errores de select al interactuar
    [selectCategoriaPrincipal, selectpáises, selectCategoriaSecundaria, selectmarca, selecttipoProducto, selecttipoProveedores].forEach(select => {
        select.addEventListener("change", () => {

            if (select.selectedIndex > 0) {
                select.style.border = "2px solid #0034de";
            } else {
                select.style.border = "";
            }

            if (select === selectCategoriaPrincipal && select.selectedIndex > 0) {
                errorCategoriaPrincipal.style.display = "none";
            }

            if (select === selectpáises && select.selectedIndex > 0) {
                errorpáises.style.display = "none";
            }

            if (select === selectCategoriaSecundaria && select.selectedIndex > 0) {
                errorCategoriaSecundaria.style.display = "none";
            }

            if (select === selectmarca && select.selectedIndex > 0) {
                errormarca.style.display = "none";
            }

            if (select === selecttipoProducto && select.selectedIndex > 0) {
                errortipoProducto.style.display = "none";
            }

            if (select === selecttipoProveedores && select.selectedIndex > 0) {
                errortipoProveedores.style.display = "none";
            }

        });
    });

    // Obtener radios y mensaje de error
    const radiosEstado = document.querySelectorAll('input[name="activo"]'); // ACTUALIZADO: nombre cambiado a "activo"
    const errorEstado = document.querySelector('.error--estado');
    const radiosCustom = document.querySelectorAll('.radio__custom');

    // Quitar el error cuando el usuario selecciona un estado
    radiosEstado.forEach(radio => {
        radio.addEventListener('change', () => {
            errorEstado.style.display = 'none';
            radiosCustom.forEach(r => r.classList.remove('error')); // quita borde rojo
        });
    });

    //Validacion de la imagen
    function validarImagenes() {
        const file = inputFile.files[0];

        // Reiniciar errores y borde
        errorFormato.style.display = "none";
        errorVacio.style.display = "none";
        boxImagen.style.border = "1px solid #ddd"; // borde por defecto

        // Si no hay archivo seleccionado
        if (!file) {
            errorVacio.style.display = "block";
            boxImagen.style.border = "2px solid #e53935"; // borde rojo
            return false;
        }

        // Si el formato no es permitido
        if (!formatosPermitidos.includes(file.type)) {
            errorFormato.style.display = "block";
            boxImagen.style.border = "2px solid #e53935"; // borde rojo
            return false;
        }

        // Si todo está correcto → volver al borde normal
        boxImagen.style.border = "1px solid #ddd";
        return true;
    }

    // Descripción del producto (validación)
    const boxDescripcion = document.querySelector('.formulario__boxdescripcion');
    const errorDescripcion = document.querySelector('.error--descripcion');

    // Función de validación
    function validarDescripcion() {
        const contenido = quill.getText().trim(); // Obtiene solo texto plano (sin formato)

        // Reiniciar estado
        errorDescripcion.style.display = 'none';
        boxDescripcion.classList.remove('error-border');

        // Actualizar campo oculto antes de validar
        if (descripcionHidden) {
            descripcionHidden.value = quill.root.innerHTML;
        }

        if (contenido === '' || contenido.length === 0) {
            errorDescripcion.style.display = 'block';
            boxDescripcion.classList.add('error-border');
            return false;
        }

        return true;
    }

    // Ocultar el error al escribir
    quill.on('text-change', () => {
        const contenido = quill.getText().trim();

        // Actualizar campo oculto continuamente
        if (descripcionHidden) {
            descripcionHidden.value = quill.root.innerHTML;
        }

        if (contenido.length > 0) {
            errorDescripcion.style.display = 'none';
            boxDescripcion.classList.remove('error-border');
        }
    });

    const addform = document.getElementById("formularioProducto");

    addform.addEventListener("submit", function (e) {
        // === SOLUCIÓN 2: PREPARAR DATOS ANTES DE VALIDAR ===
        prepararDatosParaEnvio();

        let formularioValido = true;
        let selectsValidos = true;

        // Actualizar campo oculto de descripción antes de enviar
        if (descripcionHidden) {
            descripcionHidden.value = quill.root.innerHTML;
        }

        // 2. Validar inputs (solo si el checkbox está marcado)
        Object.keys(fieldsProducto).forEach(fieldId => {
            const input = document.getElementById(fieldId);
            const regex = fieldsProducto[fieldId].regex;

            const inputBox = input.closest(".formulario__box");
            const checkIcon = inputBox.querySelector(".ri-check-line");
            const errorIcon = inputBox.querySelector(".ri-close-line");
            const errorMessage = inputBox.nextElementSibling;
            let value = input.value.trim();

            // Para precios, ya están limpios por prepararDatosParaEnvio()
            if (fieldId === "precioRegular" || fieldId === "precioVenta") {
                // Ya están limpios, no necesitan limpieza adicional
                value = value.replace(/[^\d.]/g, ''); // Solo mantener números y punto decimal
            }

            if (!regex.test(value)) {
                formularioValido = false;
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.style.display = "block";
                input.style.border = "2px solid #fd1f1f";
                const label = inputBox.querySelector("box__label");
                if (label) label.classList.add("error"); // marcar error
                inputBox.classList.add("input-error");
            } else {
                const label = inputBox.querySelector("box__label");
                if (label) label.classList.remove("error"); // quitar error si es válido
            }
        });

        const CategoriaPrincipalSeleccionada = selectCategoriaPrincipal.selectedIndex > 0;
        const paisSeleccionada = selectpáises.selectedIndex > 0;
        const categoriaSecundariaSeleccionada = selectCategoriaSecundaria.selectedIndex > 0;
        const marcaSeleccionada = selectmarca.selectedIndex > 0;
        const tipoProductoSeleccionada = selecttipoProducto.selectedIndex > 0;
        const tipoProveedoresSeleccionada = selecttipoProveedores.selectedIndex > 0;

        if (!CategoriaPrincipalSeleccionada) {
            selectsValidos = false;
            errorCategoriaPrincipal.style.display = "block";
            selectCategoriaPrincipal.style.border = "2px solid #fd1f1f";
        }

        if (!paisSeleccionada) {
            selectsValidos = false;
            errorpáises.style.display = "block";
            selectpáises.style.border = "2px solid #fd1f1f";
        }

        if (!categoriaSecundariaSeleccionada) {
            selectsValidos = false;
            errorCategoriaSecundaria.style.display = "block";
            selectCategoriaSecundaria.style.border = "2px solid #fd1f1f";
        }

        if (!marcaSeleccionada) {
            selectsValidos = false;
            errormarca.style.display = "block";
            selectmarca.style.border = "2px solid #fd1f1f";
        }

        if (!tipoProductoSeleccionada) {
            selectsValidos = false;
            errortipoProducto.style.display = "block";
            selecttipoProducto.style.border = "2px solid #fd1f1f";
        }

        if (!tipoProveedoresSeleccionada) {
            selectsValidos = false;
            errortipoProveedores.style.display = "block";
            selecttipoProveedores.style.border = "2px solid #fd1f1f";
        }

        // Validar estado (radio buttons)
        const estadoSeleccionado = Array.from(radiosEstado).some(radio => radio.checked);

        if (!estadoSeleccionado) {
            formularioValido = false;
            errorEstado.style.display = 'block';
            radiosCustom.forEach(r => r.classList.add('error')); // agrega borde rojo
        } else {
            radiosCustom.forEach(r => r.classList.remove('error'));
        }

        // Validar imagen antes de enviar
        if (!validarImagenes()) {
            formularioValido = false;
        }

        if (!validarDescripcion()) {
            formularioValido = false;
        }

        // Mostrar error si algo está mal
        if (!formularioValido || !selectsValidos) {
            Swal.fire({
                icon: "error",
                title: "Oops...",
                text: "Por favor rellene el formulario correctamente",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });
            e.preventDefault();
            return;
        }

        // Caso éxito
        sessionStorage.setItem("loginSuccess", "true");
    });

});