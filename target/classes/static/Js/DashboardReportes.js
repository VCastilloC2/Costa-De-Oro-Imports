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

    new ReportesExcelExporter();

});

// reportes-excel.js
class ReportesExcelExporter {
    constructor() {
        this.baseURL = '/api/reportes';
        this.initEventListeners();
    }

    initEventListeners() {
        const exportButtons = document.querySelectorAll('.export-btn');
        exportButtons.forEach((button, index) => {
            button.addEventListener('click', () => {
                this.exportReport(index);
            });
        });
    }

    async exportReport(reportIndex) {
        const reportTypes = [
            {
                endpoint: 'ventas-clientes',
                filename: 'ventas_por_clientes',
                title: 'VENTAS POR CLIENTES',
                subtitle: 'AÑO 2025',
                columns: [
                    'Identificación', 'Cliente', 'Número de Comprobantes',
                    'Valor Bruto', 'Impuesto Cargo', 'Total'
                ],
                company: 'COSTA DE ORO IMPORTS',
                companyId: '123456789-0',
                period: 'De Enero 01 2025 a Noviembre 25 2025'
            },
            {
                endpoint: 'ventas-producto',
                filename: 'ventas_por_producto',
                title: 'VENTAS POR PRODUCTO',
                subtitle: 'AÑO 2025',
                columns: [
                    'Código Producto', 'Nombre Producto', 'Referencia Fábrica',
                    'Cantidad Vendida', 'Valor Bruto', 'Impuesto Cargo', 'Total'
                ],
                company: 'COSTA DE ORO IMPORTS',
                companyId: '123456789-0',
                period: 'De Enero 01 2025 a Noviembre 25 2025'
            },
            {
                endpoint: 'ventas-cliente-producto',
                filename: 'ventas_cliente_producto',
                title: 'VENTAS POR CLIENTE Y PRODUCTO',
                subtitle: 'AÑO 2025',
                columns: [
                    'Identificación', 'Cliente', 'Código Producto', 'Nombre Producto',
                    'Referencia Fábrica', 'Cantidad Vendida', 'Valor Bruto', 'Impuesto Cargo', 'Total'
                ],
                company: 'COSTA DE ORO IMPORTS',
                companyId: '123456789-0',
                period: 'De Enero 01 2025 a Noviembre 25 2025'
            },
            {
                endpoint: 'cuentas-pagar',
                filename: 'cuentas_por_pagar',
                title: 'CUENTAS POR PAGAR',
                subtitle: 'AÑO 2025',
                columns: [
                    'Identificación', 'Proveedor', 'Ciudad', 'Sucursal',
                    'Productos Suministrados', 'Facturas Registradas', 'Total Facturado', 'Compras Realizadas'
                ],
                company: 'COSTA DE ORO IMPORTS',
                companyId: '123456789-0',
                period: 'De Enero 01 2025 a Noviembre 25 2025'
            },
            {
                endpoint: 'reporte-compras',
                filename: 'reporte_compras',
                title: 'REPORTE DE COMPRAS',
                subtitle: 'AÑO 2025',
                columns: [
                    'Identificación', 'Cliente', 'Ciudad', 'Sucursal',
                    'Total Compras', 'Total Subtotal', 'Total IVA', 'Total Comprado',
                    'Promedio Compra', 'Última Compra', 'Método Pago Frecuente'
                ],
                company: 'COSTA DE ORO IMPORTS',
                companyId: '123456789-0',
                period: 'De Enero 01 2025 a Noviembre 25 2025'
            },
            {
                endpoint: 'rentabilidad-producto',
                filename: 'rentabilidad_producto',
                title: 'RENTABILIDAD POR PRODUCTO',
                subtitle: 'AÑO 2025',
                columns: [
                    'Tipo', 'Código Producto', 'Nombre Producto', 'Referencia Fábrica',
                    'Categoría', 'Cantidad Vendida', 'Ventas Brutas Totales', 'Costos Totales',
                    'Utilidad en Pesos', 'Porcentaje de Utilidad', 'Porcentaje de Rentabilidad'
                ],
                company: 'COSTA DE ORO IMPORTS',
                companyId: '123456789-0',
                period: 'De Enero 01 2025 a Noviembre 25 2025'
            }
        ];

        const report = reportTypes[reportIndex];
        if (!report) return;

        try {
            // Mostrar loading
            this.showLoading(report.title);

            // Obtener datos del API
            const response = await fetch(`${this.baseURL}/${report.endpoint}`);
            const data = await response.json();

            if (!data || !data[Object.keys(data)[0]]) {
                throw new Error('No hay datos para exportar');
            }

            // Crear Excel
            this.createProfessionalExcelFile(data, report);

            // Ocultar loading
            this.hideLoading();

        } catch (error) {
            console.error('Error al exportar:', error);
            this.hideLoading();
            alert('Error al exportar el reporte: ' + error.message);
        }
    }

    createProfessionalExcelFile(data, report) {
        const workbook = XLSX.utils.book_new();

        // Obtener datos principales
        const mainDataKey = Object.keys(data).find(key => key !== 'totalGeneral' && key !== 'anio');
        const rows = data[mainDataKey] || [];
        const totalGeneral = data.totalGeneral || {};

        // Crear hoja principal
        const worksheet = XLSX.utils.aoa_to_sheet([]);

        // Agregar información de la empresa - CENTRADA
        XLSX.utils.sheet_add_aoa(worksheet, [[report.title]], { origin: 'A1' });
        XLSX.utils.sheet_add_aoa(worksheet, [[report.company]], { origin: 'A2' });
        XLSX.utils.sheet_add_aoa(worksheet, [[report.companyId]], { origin: 'A3' });
        XLSX.utils.sheet_add_aoa(worksheet, [[report.period]], { origin: 'A4' });

        // Espacio
        XLSX.utils.sheet_add_aoa(worksheet, [['']], { origin: 'A5' });

        // Agregar encabezados de columnas
        XLSX.utils.sheet_add_aoa(worksheet, [report.columns], { origin: 'A6' });

        // Agregar datos
        if (rows.length > 0) {
            const formattedData = rows.map(row => {
                return report.columns.map((col, colIndex) => {
                    const key = this.getKeyFromColumnName(col, row);
                    const value = row[key];

                    if (value === undefined || value === null) return '';

                    // Formatear según el tipo de dato y la columna
                    if (typeof value === 'number') {
                        if (col.includes('Porcentaje') || col.includes('Utilidad') || col.includes('Rentabilidad')) {
                            return this.formatPercentage(value);
                        } else if (col.includes('Cantidad') || col.includes('Número')) {
                            return this.formatNumber(value);
                        } else {
                            return this.formatCurrency(value);
                        }
                    } else if (col === 'Código Producto' || col === 'Identificación') {
                        // Para códigos de producto e identificaciones, forzar como texto
                        return this.formatAsText(value);
                    } else if (this.isValidDate(value) && !this.looksLikeCode(value)) {
                        // Solo formatear como fecha si realmente es una fecha válida y no parece un código
                        return this.formatDate(value);
                    } else {
                        // Para cualquier otro texto
                        return this.formatAsText(value);
                    }
                });
            });

            XLSX.utils.sheet_add_aoa(worksheet, formattedData, { origin: 'A7' });
        }

        // Agregar totales generales
        const dataStartRow = 7;
        const dataEndRow = dataStartRow + rows.length;

        if (Object.keys(totalGeneral).length > 0) {
            XLSX.utils.sheet_add_aoa(worksheet, [['']], { origin: `A${dataEndRow + 1}` });

            const totalRow = this.createProfessionalTotalRow(totalGeneral, report.columns);
            XLSX.utils.sheet_add_aoa(worksheet, [totalRow], { origin: `A${dataEndRow + 2}` });
        }

        // Aplicar estilos y formatos
        this.applyProfessionalStyles(worksheet, report.columns.length, dataEndRow + (Object.keys(totalGeneral).length > 0 ? 3 : 1));

        // Agregar hoja al workbook
        XLSX.utils.book_append_sheet(workbook, worksheet, 'Reporte');

        // Generar archivo y descargar
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        this.saveAsExcelFile(excelBuffer, `${report.filename}_${new Date().toISOString().split('T')[0]}.xlsx`);
    }

    getKeyFromColumnName(columnName, row) {
        const keyMap = {
            'Identificación': 'identificacion',
            'Cliente': 'cliente',
            'Número de Comprobantes': 'numeroComprobantes',
            'Valor Bruto': 'valorBruto',
            'Impuesto Cargo': 'impuestoCargo',
            'Total': 'total',
            'Código Producto': 'codigoProducto',
            'Nombre Producto': 'nombreProducto',
            'Referencia Fábrica': 'referenciaFabrica',
            'Cantidad Vendida': 'cantidadVendida',
            'Proveedor': 'proveedor',
            'Ciudad': 'ciudad',
            'Sucursal': 'sucursal',
            'Productos Suministrados': 'productosSuministrados',
            'Facturas Registradas': 'facturasRegistradas',
            'Total Facturado': 'totalFacturado',
            'Compras Realizadas': 'comprasRealizadas',
            'Total Compras': 'totalCompras',
            'Total Subtotal': 'totalSubtotal',
            'Total IVA': 'totalIva',
            'Total Comprado': 'totalComprado',
            'Promedio Compra': 'promedioCompra',
            'Última Compra': 'ultimaCompra',
            'Método Pago Frecuente': 'metodoPagoFrecuente',
            'Tipo': 'tipo',
            'Categoría': 'categoria',
            'Ventas Brutas Totales': 'ventasBrutasTotales',
            'Costos Totales': 'costosTotales',
            'Utilidad en Pesos': 'utilidadPesos',
            'Porcentaje de Utilidad': 'porcentajeUtilidad',
            'Porcentaje de Rentabilidad': 'porcentajeRentabilidad'
        };

        return keyMap[columnName] || columnName.toLowerCase();
    }

    createProfessionalTotalRow(totalGeneral, columns) {
        const totalRow = ['TOTAL GENERAL'];

        // Rellenar con espacios hasta las columnas de valores
        for (let i = 1; i < columns.length; i++) {
            totalRow.push('');
        }

        // Colocar los totales en las columnas correctas
        if (totalGeneral.valorBrutoTotal !== undefined) {
            totalRow[3] = this.formatCurrency(totalGeneral.valorBrutoTotal);
            totalRow[4] = this.formatCurrency(totalGeneral.impuestoCargoTotal);
            totalRow[5] = this.formatCurrency(totalGeneral.totalGeneral);
        } else if (totalGeneral.ventasBrutasTotales !== undefined && totalGeneral.costosTotales !== undefined) {
            totalRow[6] = this.formatCurrency(totalGeneral.ventasBrutasTotales);
            totalRow[7] = this.formatCurrency(totalGeneral.costosTotales);
        } else if (totalGeneral.totalCompras !== undefined) {
            totalRow[4] = this.formatCurrency(totalGeneral.totalSubtotal);
            totalRow[5] = this.formatCurrency(totalGeneral.totalIva);
            totalRow[6] = this.formatCurrency(totalGeneral.totalComprado);
        } else if (totalGeneral.totalFacturado !== undefined) {
            totalRow[6] = this.formatCurrency(totalGeneral.totalFacturado);
        }

        return totalRow;
    }

    // NUEVO: Función mejorada para detectar si un valor parece ser un código
    looksLikeCode(value) {
        if (typeof value !== 'string') return false;

        const str = value.toString().trim();

        // Patrones que indican que es un código (no una fecha)
        const codePatterns = [
            /^[A-Z]{3}-\d{4}$/, // Ejemplo: GIN-0003, VIN-0014
            /^\d{2}\/\d{2}\/\d{4}$/, // Ejemplo: 01/03/2001 (pero que sea código, no fecha)
            /^[A-Z0-9]+-\d+$/, // Códigos con formato letras-números
            /^[A-Z]{2,}\d+$/, // Ejemplo: WHI0003
        ];

        // Si coincide con algún patrón de código, tratar como código
        return codePatterns.some(pattern => pattern.test(str));
    }

    // NUEVO: Función mejorada para validar fechas
    isValidDate(value) {
        if (typeof value !== 'string') return false;

        const date = new Date(value);
        return !isNaN(date.getTime()) && date.toString() !== 'Invalid Date';
    }

    // NUEVO: Función para forzar formato de texto
    formatAsText(value) {
        // Forzar como texto agregando un apóstrofe al inicio (para Excel)
        // Esto previene que Excel convierta automáticamente a fecha
        if (value === null || value === undefined) return '';
        return `'${value.toString()}`;
    }

    applyProfessionalStyles(worksheet, numColumns, lastRow) {
        // Definir rangos para estilos
        if (!worksheet['!merges']) worksheet['!merges'] = [];

        // MERGES PARA EL ENCABEZADO - Centrar respecto al ancho de la tabla
        worksheet['!merges'].push(
            { s: { r: 0, c: 0 }, e: { r: 0, c: numColumns - 1 } }, // Título principal
            { s: { r: 1, c: 0 }, e: { r: 1, c: numColumns - 1 } }, // Empresa
            { s: { r: 2, c: 0 }, e: { r: 2, c: numColumns - 1 } }, // NIT
            { s: { r: 3, c: 0 }, e: { r: 3, c: numColumns - 1 } }  // Periodo
        );

        // ESTILOS PARA EL ENCABEZADO (filas 1-4)
        const headerRange = XLSX.utils.encode_range({ s: { r: 0, c: 0 }, e: { r: 3, c: numColumns - 1 } });
        this.setRangeStyle(worksheet, headerRange, {
            fill: { fgColor: { rgb: "2C3E50" } }, // Azul oscuro
            font: { bold: true, color: { rgb: "FFFFFF" } },
            alignment: { horizontal: "center", vertical: "center" }
        });

        // Estilo específico para el título principal (fila 1)
        this.setCellStyle(worksheet, 'A1', {
            font: { bold: true, color: { rgb: "FFFFFF" }, sz: 30 }
        });

        // Estilos para empresa, NIT y periodo (filas 2-4)
        const companyRange = XLSX.utils.encode_range({ s: { r: 1, c: 0 }, e: { r: 3, c: numColumns - 1 } });
        this.setRangeStyle(worksheet, companyRange, {
            font: { bold: true, color: { rgb: "FFFFFF" }, sz: 14 }
        });

        // Estilos para encabezados de columnas (fila 6)
        const columnHeaderRange = XLSX.utils.encode_range({ s: { r: 5, c: 0 }, e: { r: 5, c: numColumns - 1 } });
        this.setRangeStyle(worksheet, columnHeaderRange, {
            fill: { fgColor: { rgb: "F8F9FA" } }, // Gris suave
            font: { bold: true, color: { rgb: "2C3E50" }, sz: 11 },
            border: {
                bottom: { style: "medium", color: { rgb: "DEE2E6" } }
            },
            alignment: { horizontal: "center", vertical: "center" }
        });

        // Estilos para datos
        if (lastRow > 7) {
            const dataRange = XLSX.utils.encode_range({ s: { r: 6, c: 0 }, e: { r: lastRow - 1, c: numColumns - 1 } });
            this.setRangeStyle(worksheet, dataRange, {
                border: {
                    top: { style: "thin", color: { rgb: "ECF0F1" } },
                    left: { style: "thin", color: { rgb: "ECF0F1" } },
                    bottom: { style: "thin", color: { rgb: "ECF0F1" } },
                    right: { style: "thin", color: { rgb: "ECF0F1" } }
                },
                alignment: { vertical: "center" }
            });

            // Alternar colores de filas para mejor legibilidad
            for (let r = 6; r < lastRow; r++) {
                if (r % 2 === 0) {
                    const rowRange = XLSX.utils.encode_range({ s: { r: r, c: 0 }, e: { r: r, c: numColumns - 1 } });
                    this.setRangeStyle(worksheet, rowRange, {
                        fill: { fgColor: { rgb: "F8F9FA" } }
                    });
                }
            }

            // Alineación específica para columnas numéricas
            const numericColumns = this.getNumericColumnIndices(worksheet, numColumns);
            numericColumns.forEach(colIndex => {
                const numericRange = XLSX.utils.encode_range({ s: { r: 6, c: colIndex }, e: { r: lastRow - 1, c: colIndex } });
                this.setRangeStyle(worksheet, numericRange, {
                    alignment: { horizontal: "right", vertical: "center" }
                });
            });

            // Alineación específica para columnas de texto (códigos, identificaciones)
            const textColumns = this.getTextColumnIndices(worksheet, numColumns);
            textColumns.forEach(colIndex => {
                const textRange = XLSX.utils.encode_range({ s: { r: 6, c: colIndex }, e: { r: lastRow - 1, c: colIndex } });
                this.setRangeStyle(worksheet, textRange, {
                    alignment: { horizontal: "left", vertical: "center" }
                });
            });
        }

        // Estilos para total general
        if (worksheet[`A${lastRow}`] && worksheet[`A${lastRow}`].v === 'TOTAL GENERAL') {
            const totalRange = XLSX.utils.encode_range({ s: { r: lastRow - 1, c: 0 }, e: { r: lastRow - 1, c: numColumns - 1 } });
            this.setRangeStyle(worksheet, totalRange, {
                fill: { fgColor: { rgb: "E9ECEF" } },
                font: { bold: true, color: { rgb: "2C3E50" }, sz: 12 },
                border: {
                    top: { style: "double", color: { rgb: "2C3E50" } },
                    bottom: { style: "double", color: { rgb: "2C3E50" } }
                }
            });

            // Merge para la celda de "TOTAL GENERAL"
            worksheet['!merges'].push(
                { s: { r: lastRow - 1, c: 0 }, e: { r: lastRow - 1, c: 1 } }
            );
        }

        // Ajustar anchos de columnas automáticamente
        worksheet['!cols'] = this.calculateProfessionalColumnWidths(worksheet, numColumns);

        // Ajustar altos de filas
        if (!worksheet['!rows']) worksheet['!rows'] = [];
        // Encabezado principal más alto
        worksheet['!rows'][0] = { hpt: 40 };
        worksheet['!rows'][1] = { hpt: 25 };
        worksheet['!rows'][2] = { hpt: 25 };
        worksheet['!rows'][3] = { hpt: 25 };
        // Encabezados de columnas
        worksheet['!rows'][5] = { hpt: 30 };
    }

    getNumericColumnIndices(worksheet, numColumns) {
        const numericIndices = [];
        const numericKeywords = ['valor', 'total', 'costo', 'utilidad', 'precio', 'subtotal', 'iva', 'bruto', 'impuesto', 'porcentaje', 'cantidad', 'número', 'promedio'];

        for (let c = 0; c < numColumns; c++) {
            const cell = XLSX.utils.encode_cell({ r: 5, c: c }); // Fila de encabezados
            if (worksheet[cell] && worksheet[cell].v) {
                const headerText = worksheet[cell].v.toString().toLowerCase();
                if (numericKeywords.some(keyword => headerText.includes(keyword))) {
                    numericIndices.push(c);
                }
            }
        }
        return numericIndices;
    }

    getTextColumnIndices(worksheet, numColumns) {
        const textIndices = [];
        const textKeywords = ['código', 'identificación', 'nombre', 'cliente', 'proveedor', 'referencia', 'categoría', 'ciudad', 'sucursal', 'tipo', 'método'];

        for (let c = 0; c < numColumns; c++) {
            const cell = XLSX.utils.encode_cell({ r: 5, c: c }); // Fila de encabezados
            if (worksheet[cell] && worksheet[cell].v) {
                const headerText = worksheet[cell].v.toString().toLowerCase();
                if (textKeywords.some(keyword => headerText.includes(keyword))) {
                    textIndices.push(c);
                }
            }
        }
        return textIndices;
    }

    setCellStyle(worksheet, cell, style) {
        if (!worksheet[cell]) worksheet[cell] = {};
        if (!worksheet[cell].s) worksheet[cell].s = {};
        Object.assign(worksheet[cell].s, style);
    }

    setRangeStyle(worksheet, range, style) {
        const rangeObj = XLSX.utils.decode_range(range);
        for (let r = rangeObj.s.r; r <= rangeObj.e.r; r++) {
            for (let c = rangeObj.s.c; c <= rangeObj.e.c; c++) {
                const cell = XLSX.utils.encode_cell({ r, c });
                this.setCellStyle(worksheet, cell, style);
            }
        }
    }

    calculateProfessionalColumnWidths(worksheet, numColumns) {
        const colWidths = [];
        for (let c = 0; c < numColumns; c++) {
            let maxLength = 12; // Ancho mínimo

            // Revisar encabezados
            const headerCell = XLSX.utils.encode_cell({ r: 5, c: c });
            if (worksheet[headerCell] && worksheet[headerCell].v) {
                maxLength = Math.max(maxLength, worksheet[headerCell].v.toString().length);
            }

            // Revisar primeras 15 filas de datos
            for (let r = 6; r < 21; r++) {
                const cell = XLSX.utils.encode_cell({ r, c });
                if (worksheet[cell] && worksheet[cell].v) {
                    const length = worksheet[cell].v.toString().length;
                    if (length > maxLength) maxLength = length;
                }
            }

            colWidths.push({ width: Math.min(Math.max(maxLength + 3, 15), 50) });
        }
        return colWidths;
    }

    formatCurrency(value) {
        return Number(value);
    }

    formatPercentage(value) {
        return Number(value) / 100;
    }

    formatNumber(value) {
        return Number(value);
    }

    formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('es-CO', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    }

    saveAsExcelFile(buffer, fileName) {
        const data = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(data);
        link.download = fileName;
        link.click();
        setTimeout(() => {
            window.URL.revokeObjectURL(link.href);
        }, 100);
    }

    showLoading(title) {
        const loadingOverlay = document.createElement('div');
        loadingOverlay.className = 'excel-loading-overlay';
        loadingOverlay.innerHTML = `
            <div class="excel-loading-container simple">
                <div class="excel-loading-content">
                    <h3>Generando Reporte</h3>
                    <p>${title}</p>
                    <div class="excel-loading-progress">
                        <div class="excel-progress-bar">
                            <div class="excel-progress-fill"></div>
                        </div>
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(loadingOverlay);
    }

    hideLoading() {
        const loadingOverlay = document.querySelector('.excel-loading-overlay');
        if (loadingOverlay) {
            loadingOverlay.remove();
        }
    }
}
