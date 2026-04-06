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
            e.stopPropagation();
            subMenu.classList.toggle("open__menu");
        });
        document.addEventListener("click", function (e) {
            if (!subMenu.contains(e.target) && !profileImage.contains(e.target)) {
                subMenu.classList.remove("open__menu");
            }
        });
    }

    // Notificaciones del admin
    const notifIcon = document.getElementById("notifIcon");
    const notifMenu = document.getElementById("notifMenu");
    if (notifIcon && notifMenu) {
        notifIcon.addEventListener("click", (e) => {
            e.stopPropagation();
            notifMenu.classList.toggle("open");
        });
        document.addEventListener("click", (e) => {
            if (!notifMenu.contains(e.target) && !notifIcon.contains(e.target)) {
                notifMenu.classList.remove("open");
            }
        });
    }

    // === PRECARGAR DATOS DEL PRODUCTO ===
    function precargarDatosProducto() {
        // Obtener datos del producto desde Thymeleaf
        const productoData = {
            nombre: document.getElementById('nombre')?.value || '',
            codigoProducto: document.getElementById('codigoProducto')?.value || '',
            stock: document.getElementById('stock')?.value || '',
            precioRegular: document.getElementById('precioRegular')?.value || '',
            precioVenta: document.getElementById('precioVenta')?.value || '',
            categoriaId: document.getElementById('categoriaPrincipal')?.value || '',
            subCategoriaId: document.getElementById('categoriaSecundaria')?.value || '',
            pais: document.getElementById('páises')?.value || '',
            marca: document.getElementById('marca')?.value || '',
            tipo: document.getElementById('tipoProducto')?.value || '',
            proveedorId: document.getElementById('Proveedores')?.value || '',
            activo: document.querySelector('input[name="activo"]:checked')?.value || '',
            descripcion: document.getElementById('descripcionHidden')?.value || ''
        };

        console.log('Datos del producto precargados:', productoData);

        // Formatear precios si están en formato numérico
        if (productoData.precioRegular && !isNaN(productoData.precioRegular)) {
            document.getElementById('precioRegular').value = formatCOP(productoData.precioRegular);
        }
        if (productoData.precioVenta && !isNaN(productoData.precioVenta)) {
            document.getElementById('precioVenta').value = formatCOP(productoData.precioVenta);
        }

        // Precargar subcategorías si hay categoría seleccionada
        if (productoData.categoriaId) {
            cargarSubCategorias(productoData.categoriaId, productoData.subCategoriaId);
        }

        // Precargar país si existe
        if (productoData.pais) {
            setTimeout(() => {
                const selectPais = document.getElementById('páises');
                if (selectPais) {
                    for (let i = 0; i < selectPais.options.length; i++) {
                        if (selectPais.options[i].value === productoData.pais.toLowerCase().replace(/\s+/g, "_")) {
                            selectPais.selectedIndex = i;
                            break;
                        }
                    }
                }
            }, 100);
        }

        // Precargar marca si existe
        if (productoData.marca) {
            setTimeout(() => {
                const selectMarca = document.getElementById('marca');
                if (selectMarca) {
                    for (let i = 0; i < selectMarca.options.length; i++) {
                        if (selectMarca.options[i].value === productoData.marca) {
                            selectMarca.selectedIndex = i;
                            break;
                        }
                    }
                }
            }, 500);
        }

        // Precargar descripción en Quill
        if (productoData.descripcion && quill) {
            quill.root.innerHTML = productoData.descripcion;
        }

        // Mostrar imagen actual si existe
        mostrarImagenActual();
    }

    // Función para mostrar la imagen actual del producto
    function mostrarImagenActual() {
        const previewContainer = document.getElementById('previewContainer');
        const boxImagen = document.querySelector('.formulario__boximagen');

        if (!previewContainer || !boxImagen) return;

        // Buscar la imagen existente en el previewContainer
        const imagenExistente = previewContainer.querySelector('img');

        if (imagenExistente && imagenExistente.src) {
            // Mostrar el contenedor de preview
            previewContainer.style.display = 'flex';
            boxImagen.classList.add('imagen-activa');

            // Crear botón eliminar para la imagen actual
            let removeBtn = boxImagen.querySelector('.remove-image');
            if (!removeBtn) {
                removeBtn = document.createElement('span');
                removeBtn.classList.add('remove-image');
                removeBtn.textContent = 'Eliminar imagen';
                boxImagen.appendChild(removeBtn);
            }

            // Acción eliminar
            removeBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                previewContainer.style.display = 'none';
                previewContainer.innerHTML = '';
                boxImagen.classList.remove('imagen-activa');
                removeBtn.remove();

                // Limpiar el input file
                const inputFile = document.getElementById('fileInput');
                if (inputFile) {
                    inputFile.value = '';
                }
            });
        }
    }

    //Select de pais
    const paises = [
        "Estados Unidos", "México", "Guatemala", "Puerto Rico", "República Dominicana",
        "Argentina", "Brasil", "Chile", "Colombia", "Alemania", "Bélgica", "España",
        "Francia", "Irlanda", "Países Bajos", "Reino Unido", "Suiza", "Noruega",
        "Suecia", "Italia", "Rusia", "Ucrania", "Escocia"
    ];

    const selectPaises = document.getElementById("páises");
    if (selectPaises) {
        paises.forEach(pais => {
            const option = document.createElement("option");
            option.value = pais.toLowerCase().replace(/\s+/g, "_");
            option.textContent = pais;
            selectPaises.appendChild(option);
        });
    }

    //Select de categoria principal y secundaria
    const categoriaPrincipal = document.getElementById("categoriaPrincipal");
    const categoriaSecundaria = document.getElementById("categoriaSecundaria");

    // Mapeo de IDs de categorías a nombres
    const mapeoCategorias = {};
    const mapeoSubCategorias = {};

    // Inicializar mapeo de categorías principales
    function inicializarMapeoCategorias() {
        if (!categoriaPrincipal) return;
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
        if (!categoriaSecundaria) return;
        for (const key in mapeoSubCategorias) {
            delete mapeoSubCategorias[key];
        }
        const opcionesSubCategoria = categoriaSecundaria.querySelectorAll('option');
        opcionesSubCategoria.forEach(option => {
            if (option.value && option.value !== "") {
                mapeoSubCategorias[option.value] = option.textContent;
            }
        });
    }

    // Cargar subcategorías para una categoría específica
    function cargarSubCategorias(categoriaId, subCategoriaIdSeleccionada = null) {
        if (!categoriaSecundaria) return;

        categoriaSecundaria.innerHTML = '<option disabled selected>Categoría secundaria</option>';

        fetch(`/admin/producto/subcategorias/${categoriaId}`)
            .then(response => {
                if (!response.ok) throw new Error('Error al cargar subcategorías');
                return response.json();
            })
            .then(subcategorias => {
                subcategorias.forEach(subcategoria => {
                    const option = document.createElement("option");
                    option.value = subcategoria.id;
                    option.textContent = subcategoria.nombre;
                    if (subCategoriaIdSeleccionada && subcategoria.id == subCategoriaIdSeleccionada) {
                        option.selected = true;
                    }
                    categoriaSecundaria.appendChild(option);
                });

                actualizarMapeoSubCategorias();

                // Actualizar marcas después de cargar subcategorías
                setTimeout(() => {
                    actualizarMarcas();
                }, 100);
            })
            .catch(error => {
                console.error('Error:', error);
                const errorElement = document.querySelector('.error--CategoriaSecundaria');
                if (errorElement) {
                    errorElement.textContent = 'Error al cargar las subcategorías. Intente nuevamente.';
                    errorElement.style.display = 'block';
                }
            });
    }

    // Cuando cambia la categoría principal
    if (categoriaPrincipal) {
        categoriaPrincipal.addEventListener("change", function () {
            const categoriaId = this.value;
            cargarSubCategorias(categoriaId);
        });
    }

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

    // Función para actualizar las marcas
    function actualizarMarcas() {
        if (!selectMarca || !categoriaPrincipal || !categoriaSecundaria || !selectPais) return;

        const categoriaId = categoriaPrincipal.value;
        const subCategoriaId = categoriaSecundaria.value;
        const paisSeleccionado = selectPais.options[selectPais.selectedIndex]?.text || "";

        selectMarca.innerHTML = '<option disabled selected>Marca</option>';

        if (categoriaId && subCategoriaId && paisSeleccionado) {
            const categoriaNombre = mapeoCategorias[categoriaId];
            const subCategoriaNombre = mapeoSubCategorias[subCategoriaId];

            if (categoriaNombre && subCategoriaNombre && marcas[categoriaNombre]?.[paisSeleccionado]?.[subCategoriaNombre]) {
                const lista = marcas[categoriaNombre][paisSeleccionado][subCategoriaNombre];
                lista.forEach(marca => {
                    const option = document.createElement("option");
                    option.value = marca;
                    option.textContent = marca;
                    // Seleccionar la marca actual del producto si coincide
                    const marcaActual = document.getElementById('marca')?.dataset?.marcaActual;
                    if (marcaActual && marca === marcaActual) {
                        option.selected = true;
                    }
                    selectMarca.appendChild(option);
                });
            }
        }
    }

    // Escuchar cambios en los selects
    if (categoriaPrincipal) categoriaPrincipal.addEventListener("change", actualizarMarcas);
    if (selectPais) selectPais.addEventListener("change", actualizarMarcas);
    if (categoriaSecundaria) categoriaSecundaria.addEventListener("change", actualizarMarcas);

    // === FUNCIONES PARA PRECIOS ===
    function formatCOP(value) {
        const number = Number(value.replace(/\D/g, ''));
        if (isNaN(number)) return '';
        return number.toLocaleString('es-CO', {
            style: 'currency',
            currency: 'COP',
            maximumFractionDigits: 0
        });
    }

    function setupMoneyInput(id) {
        const input = document.getElementById(id);
        if (!input) return;

        input.addEventListener('input', e => {
            const value = e.target.value.replace(/\D/g, '');
            if (value) {
                e.target.value = formatCOP(value);
            } else {
                e.target.value = '';
            }
        });

        input.addEventListener('keydown', e => {
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

    // Aplicar a inputs de precio
    setupMoneyInput('precioRegular');
    setupMoneyInput('precioVenta');

    // === FUNCIONES PARA LIMPIAR PRECIOS AL ENVIAR ===
    function limpiarFormatoPrecio(valorFormateado) {
        if (!valorFormateado) return "0";
        const valorLimpio = valorFormateado
            .replace(/[^\d,]/g, '')
            .replace(',', '.');
        return valorLimpio || "0";
    }

    function prepararDatosParaEnvio() {
        const precioRegularInput = document.getElementById('precioRegular');
        const precioVentaInput = document.getElementById('precioVenta');
        const descripcionHidden = document.getElementById('descripcionHidden');

        if (precioRegularInput) {
            const valorLimpio = limpiarFormatoPrecio(precioRegularInput.value);
            precioRegularInput.value = valorLimpio;
        }

        if (precioVentaInput) {
            const valorLimpio = limpiarFormatoPrecio(precioVentaInput.value);
            precioVentaInput.value = valorLimpio;
        }

        if (descripcionHidden && quill) {
            descripcionHidden.value = quill.root.innerHTML;
        }
    }

    // === MANEJO DE IMÁGENES ===
    const boxImagen = document.querySelector('.formulario__boximagen');
    const inputFile = document.getElementById('fileInput');
    const previewContainer = document.getElementById('previewContainer');
    const errorFormato = document.querySelector('.error--formato');
    const errorVacio = document.querySelector('.error--vacio');

    const formatosPermitidos = ["image/jpeg", "image/png", "image/webp"];

    // Al hacer clic en el contenedor, abrir explorador
    if (boxImagen && inputFile) {
        boxImagen.addEventListener('click', () => inputFile.click());
    }

    // Escuchar cambio del input de imagen
    if (inputFile) {
        inputFile.addEventListener('change', (event) => {
            const file = event.target.files[0];
            if (!file) return;

            // Validar formato
            if (!formatosPermitidos.includes(file.type)) {
                if (errorFormato) errorFormato.style.display = 'block';
                if (errorVacio) errorVacio.style.display = 'none';
                if (previewContainer) {
                    previewContainer.innerHTML = '';
                    previewContainer.style.display = 'none';
                }
                if (boxImagen) boxImagen.classList.remove('imagen-activa');
                return;
            }

            // Si el formato es válido → ocultar errores
            if (errorFormato) errorFormato.style.display = 'none';
            if (errorVacio) errorVacio.style.display = 'none';
            if (boxImagen) boxImagen.style.border = '1px solid #ddd';

            // Crear barra de carga
            if (previewContainer) {
                previewContainer.innerHTML = `<div class="progress-bar"></div>`;
                previewContainer.style.display = 'flex';
            }
            if (boxImagen) boxImagen.classList.add('imagen-activa');

            // Simular carga
            setTimeout(() => {
                const img = document.createElement('img');
                img.src = URL.createObjectURL(file);

                if (previewContainer) {
                    previewContainer.innerHTML = '';
                    previewContainer.appendChild(img);
                }

                // Crear botón eliminar (solo si no existe)
                let removeBtn = boxImagen?.querySelector('.remove-image');
                if (!removeBtn && boxImagen) {
                    removeBtn = document.createElement('span');
                    removeBtn.classList.add('remove-image');
                    removeBtn.textContent = 'Eliminar imagen';
                    boxImagen.appendChild(removeBtn);
                }

                // Acción eliminar
                if (removeBtn) {
                    removeBtn.addEventListener('click', (e) => {
                        e.stopPropagation();
                        if (previewContainer) {
                            previewContainer.style.display = 'none';
                            previewContainer.innerHTML = '';
                        }
                        if (boxImagen) boxImagen.classList.remove('imagen-activa');
                        removeBtn.remove();
                        if (inputFile) inputFile.value = '';
                    });
                }
            }, 1000);
        });
    }

    // === EDITOR DE DESCRIPCIÓN ===
    let quill;
    const editorContainer = document.getElementById('editor-container');
    if (editorContainer) {
        const toolbarOptions = [
            [{ 'font': [] }, { 'size': [] }],
            ['bold', 'italic', 'underline', 'strike'],
            [{ 'color': [] }, { 'background': [] }],
            [{ 'script': 'sub' }, { 'script': 'super' }],
            [{ 'header': '1' }, { 'header': '2' }, 'blockquote', 'code-block'],
            [{ 'list': 'ordered' }, { 'list': 'bullet' }],
            [{ 'indent': '-1' }, { 'indent': '+1' }],
            [{ 'align': [] }],
            ['link', 'image', 'video'],
            ['clean']
        ];

        quill = new Quill('#editor-container', {
            modules: { toolbar: toolbarOptions },
            theme: 'snow'
        });

        const descripcionHidden = document.getElementById('descripcionHidden');
        if (descripcionHidden) {
            quill.on('text-change', function() {
                descripcionHidden.value = quill.root.innerHTML;
            });
        }
    }

    // === VALIDACIONES ===
    const fieldsProducto = {
        nombre: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,}$/, errorMessage: "El nombre debe tener al menos 3 letras." },
        codigoProducto: { regex: /^#[A-Za-z0-9]+$/, errorMessage: "El código del producto debe iniciar con el símbolo # y contener solo letras o números después." },
        stock: { regex: /^[0-9][0-9]*$/, errorMessage: "Por favor, ingresa una cantidad de stock válida." },
        precioRegular: { regex: /^(?!0+(?:[.,]0+)?$)\d+(?:[.,]\d{1,2})?$/, errorMessage: "Por favor, ingresa un precio válido mayor que cero." },
        precioVenta: { regex: /^(?!0+(?:[.,]0+)?$)\d+(?:[.,]\d{1,2})?$/, errorMessage: "Por favor, ingresa un precio válido mayor que cero." }
    };

    Object.keys(fieldsProducto).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        if (!input) return;

        const inputBox = input.closest(".formulario__box");
        const checkIcon = inputBox?.querySelector(".ri-check-line");
        const errorIcon = inputBox?.querySelector(".ri-close-line");
        const errorMessage = inputBox?.nextElementSibling;

        input.addEventListener("input", () => {
            let value = input.value.trim();

            if (fieldId === "precioRegular" || fieldId === "precioVenta") {
                value = value.replace(/[^\d]/g, '');
            }

            if (!inputBox || !checkIcon || !errorIcon || !errorMessage) return;

            if (value === "") {
                inputBox.classList.remove("input-error");
                checkIcon.style.display = "none";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "";
            } else if (fieldsProducto[fieldId].regex.test(value)) {
                checkIcon.style.display = "inline-block";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "2px solid #0034de";
                inputBox.classList.remove("input-error");
            } else {
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.style.display = "block";
                input.style.border = "2px solid #fd1f1f";
                inputBox.classList.add("input-error");
            }
        });
    });

    // === ENVÍO DEL FORMULARIO ===
    const addform = document.getElementById("formularioProducto");
    if (addform) {
        addform.addEventListener("submit", function (e) {
            prepararDatosParaEnvio();

            let formularioValido = true;

            // Validar campos
            Object.keys(fieldsProducto).forEach(fieldId => {
                const input = document.getElementById(fieldId);
                if (!input) return;

                const regex = fieldsProducto[fieldId].regex;
                let value = input.value.trim();

                if (fieldId === "precioRegular" || fieldId === "precioVenta") {
                    value = value.replace(/[^\d.]/g, '');
                }

                if (!regex.test(value)) {
                    formularioValido = false;
                }
            });

            // Validar imagen (no es obligatoria en actualización)
            const tieneImagenActual = document.querySelector('#previewContainer img') !== null;
            const tieneNuevaImagen = inputFile && inputFile.files.length > 0;

            if (!tieneImagenActual && !tieneNuevaImagen) {
                // En actualización, la imagen no es obligatoria si ya existe una
                // Solo mostrar error si no hay imagen actual ni nueva
                if (errorVacio) errorVacio.style.display = 'block';
                formularioValido = false;
            } else {
                if (errorVacio) errorVacio.style.display = 'none';
            }

            if (!formularioValido) {
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

            sessionStorage.setItem("loginSuccess", "true");
        });
    }

    // === INICIALIZACIÓN ===
    inicializarMapeoCategorias();
    precargarDatosProducto();
});