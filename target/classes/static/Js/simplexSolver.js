/**
 * simplexSolver.js
 * Implementación del método Simplex para programación lineal
 * Resuelve problemas de maximización con restricciones <= y variables >= 0
 */

// Tolerancia para comparaciones numéricas
const EPSILON = 1e-7;

/**
 * Resuelve un problema de programación lineal usando el método Simplex
 * @param {number[]} c - Coeficientes de la función objetivo (maximizar)
 * @param {number[][]} A - Matriz de coeficientes de restricciones (cada fila es una restricción)
 * @param {number[]} b - Lado derecho de las restricciones
 * @returns {Object} - Resultado con estado, solución y valor óptimo
 */
function solveLP(c, A, b) {
    const m = A.length;      // número de restricciones
    const n = c.length;      // número de variables originales

    // Crear tabla inicial: (m+1) filas, (n + m + 1) columnas
    // Última columna es RHS
    let tableau = Array(m + 1).fill().map(() => Array(n + m + 1).fill(0));

    // Fila 0: coeficientes de la función objetivo (negativos para maximización)
    for (let j = 0; j < n; j++) {
        tableau[0][j] = -c[j];
    }

    // Restricciones y variables de holgura
    for (let i = 0; i < m; i++) {
        for (let j = 0; j < n; j++) {
            tableau[i + 1][j] = A[i][j];
        }
        // Variable de holgura
        tableau[i + 1][n + i] = 1;
        // RHS
        tableau[i + 1][n + m] = b[i];
    }

    // Almacenar qué variable básica está en cada fila (por índice)
    let basicVars = new Array(m);
    for (let i = 0; i < m; i++) {
        basicVars[i] = n + i;  // inicialmente las holguras
    }

    // Iteraciones Simplex
    let iteration = 0;
    const maxIterations = 1000;

    while (iteration < maxIterations) {
        // Encontrar columna pivote (variable entrante): coeficiente más negativo en fila 0
        let enterCol = -1;
        let minCoeff = 0;
        for (let j = 0; j < n + m; j++) {
            if (tableau[0][j] < minCoeff - EPSILON) {
                minCoeff = tableau[0][j];
                enterCol = j;
            }
        }

        // Si no hay coeficiente negativo, solución óptima encontrada
        if (enterCol === -1) break;

        // Prueba de razón mínima para encontrar fila pivote (variable saliente)
        let minRatio = Infinity;
        let leaveRow = -1;
        for (let i = 1; i <= m; i++) {
            if (tableau[i][enterCol] > EPSILON) {
                const ratio = tableau[i][n + m] / tableau[i][enterCol];
                if (ratio < minRatio - EPSILON) {
                    minRatio = ratio;
                    leaveRow = i;
                }
            }
        }

        // Si no se encuentra fila, el problema es no acotado
        if (leaveRow === -1) {
            return {
                status: 'unbounded',
                message: 'El problema es no acotado. La función objetivo puede crecer indefinidamente.'
            };
        }

        // Realizar pivoteo en (leaveRow, enterCol)
        const pivotValue = tableau[leaveRow][enterCol];
        // Normalizar fila pivote
        for (let j = 0; j <= n + m; j++) {
            tableau[leaveRow][j] /= pivotValue;
        }
        // Eliminar la columna entrante de las demás filas
        for (let i = 0; i <= m; i++) {
            if (i !== leaveRow) {
                const factor = tableau[i][enterCol];
                if (Math.abs(factor) > EPSILON) {
                    for (let j = 0; j <= n + m; j++) {
                        tableau[i][j] -= factor * tableau[leaveRow][j];
                    }
                }
            }
        }

        // Actualizar variable básica para esta fila
        basicVars[leaveRow - 1] = enterCol;
        iteration++;
    }

    if (iteration >= maxIterations) {
        return {
            status: 'max_iterations',
            message: 'Se alcanzó el máximo de iteraciones sin converger.'
        };
    }

    // Extraer solución óptima
    const solution = new Array(n).fill(0);
    for (let i = 0; i < m; i++) {
        const basicVar = basicVars[i];
        if (basicVar < n) {
            // Es una variable original
            solution[basicVar] = tableau[i + 1][n + m];
        }
    }

    // Valor de la función objetivo (tomar de tableau[0][n+m], pero con signo cambiado)
    let objectiveValue = tableau[0][n + m];
    // Ajustar por si hay redondeo
    objectiveValue = Math.abs(objectiveValue) < EPSILON ? 0 : objectiveValue;

    // Verificar que los valores no sean negativos por error numérico
    for (let i = 0; i < n; i++) {
        if (solution[i] < 0 && solution[i] > -EPSILON) solution[i] = 0;
    }

    // Calcular holguras (valores de las variables de holgura)
    const slacks = new Array(m);
    for (let i = 0; i < m; i++) {
        const basicVar = basicVars[i];
        if (basicVar >= n && basicVar < n + m) {
            slacks[basicVar - n] = tableau[i + 1][n + m];
        } else {
            // Si la holgura no es básica, su valor es 0
            slacks[i] = 0;
        }
    }

    return {
        status: 'optimal',
        solution: solution,
        objective: objectiveValue,
        slacks: slacks,
        tableau: tableau,
        basicVars: basicVars
    };
}

/**
 * Formatea un número para mostrar (2 decimales si es necesario)
 */
function formatNumber(value, isMoney = false) {
    if (value === undefined || value === null) return '0';
    let rounded = Math.round(value * 100) / 100;
    if (Math.abs(rounded) < EPSILON) rounded = 0;
    if (isMoney) {
        return '$' + rounded.toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
    }
    return rounded.toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
}

// ==================== DEFINICIÓN DE MODELOS DESDE EL EXCEL ====================

const ModeloCerveza = {
    nombre: 'Cerveza',
    descripcion: 'Modelo: Águila (Nacional) vs Heineken (Importada)',
    variables: ['X1 - Águila (unidades/día)', 'X2 - Heineken (unidades/día)'],
    c: [1700, 3500],  // Margen por unidad
    A: [
        [1800, 4500],   // Presupuesto de compra
        [1, 1],         // Espacio en bodega
        [1, 0],         // Demanda máxima nacional
        [0, 1]          // Demanda máxima importada
    ],
    b: [500000, 200, 130, 90],
    constraintsDesc: [
        'Presupuesto: 1.800·X1 + 4.500·X2 ≤ 500.000',
        'Espacio: X1 + X2 ≤ 200',
        'Demanda Águila: X1 ≤ 130',
        'Demanda Heineken: X2 ≤ 90'
    ]
};

const ModeloWhisky = {
    nombre: 'Whisky',
    descripcion: 'Modelo: Johnnie Walker Red Label vs Jack Daniel\'s Old No.7',
    variables: ['X1 - JW Red Label (botellas/día)', 'X2 - Jack Daniel\'s (botellas/día)'],
    c: [35000, 45000], // Margen por botella
    A: [
        [55000, 75000], // Presupuesto de compra
        [1, 1],         // Espacio en bodega
        [1, 0],         // Demanda máxima JW
        [0, 1]          // Demanda máxima Jack Daniel's
    ],
    b: [3000000, 40, 25, 20],
    constraintsDesc: [
        'Presupuesto: 55.000·X1 + 75.000·X2 ≤ 3.000.000',
        'Espacio: X1 + X2 ≤ 40',
        'Demanda JW: X1 ≤ 25',
        'Demanda Jack Daniel\'s: X2 ≤ 20'
    ]
};

// Función para resolver y mostrar resultados en el panel
function resolverModelo(modelo) {
    const resultContainer = document.getElementById('simplexResult');
    if (!resultContainer) return;

    // Mostrar estado de carga
    resultContainer.innerHTML = `
        <div style="text-align: center; padding: 20px;">
            <i class="ri-loader-4-line" style="font-size: 24px; animation: spin 1s linear infinite;"></i>
            <p>Resolviendo modelo ${modelo.nombre} con Simplex...</p>
        </div>
    `;

    // Simular un pequeño retraso para mostrar el loader (opcional)
    setTimeout(() => {
        const result = solveLP(modelo.c, modelo.A, modelo.b);

        if (result.status !== 'optimal') {
            resultContainer.innerHTML = `
                <div style="background: #fff5f5; border-left: 4px solid #dc3545; padding: 15px; border-radius: 8px;">
                    <h4 style="color: #dc3545; margin: 0 0 10px 0;">Error en la optimización</h4>
                    <p>${result.message || 'No se pudo encontrar una solución óptima.'}</p>
                </div>
            `;
            return;
        }

        // Construir tabla de resultados
        let variablesHtml = '';
        modelo.variables.forEach((varName, idx) => {
            const value = result.solution[idx];
            variablesHtml += `
                <div style="display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #eee;">
                    <span style="font-weight: 600;">${varName}:</span>
                    <span>${formatNumber(value)} unidades</span>
                </div>
            `;
        });

        // Verificar restricciones (lado izquierdo vs derecho)
        let constraintsHtml = '';
        for (let i = 0; i < modelo.A.length; i++) {
            const lhs = modelo.A[i].reduce((sum, coeff, j) => sum + coeff * result.solution[j], 0);
            const rhs = modelo.b[i];
            const slack = result.slacks[i];
            const isSatisfied = lhs <= rhs + EPSILON;
            constraintsHtml += `
                <div style="display: flex; justify-content: space-between; padding: 6px 0; font-size: 0.9rem; border-bottom: 1px solid #f0f0f0;">
                    <span>${modelo.constraintsDesc[i]}</span>
                    <span style="color: ${isSatisfied ? '#28a745' : '#dc3545'}">
                        LI = ${formatNumber(lhs)} ≤ ${formatNumber(rhs)} ${isSatisfied ? '✓' : '✗'}
                        ${slack > EPSILON ? `<br><small>Holgura: ${formatNumber(slack)}</small>` : ''}
                    </span>
                </div>
            `;
        }

        resultContainer.innerHTML = `
            <div style="background: #fff; border-radius: 12px; overflow: hidden;">
                <div style="background: linear-gradient(135deg, #5a4636 0%, #8e705c 100%); padding: 15px; color: white;">
                    <h3 style="margin: 0; font-family: 'Geist', sans-serif;"> Resultados - ${modelo.nombre}</h3>
                    <p style="margin: 5px 0 0; opacity: 0.9; font-size: 0.85rem;">${modelo.descripcion}</p>
                </div>
                <div style="padding: 20px;">
                    <div style="margin-bottom: 20px;">
                        <h4 style="margin: 0 0 10px 0; color: #5a4636;"> Solución Óptima</h4>
                        ${variablesHtml}
                    </div>
                    <div style="margin-bottom: 20px; background: #f8f9fa; padding: 12px; border-radius: 8px;">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <span style="font-weight: 700; font-size: 1.1rem;"> Beneficio Máximo:</span>
                            <span style="font-size: 1.3rem; font-weight: 800; color: #28a745;">${formatNumber(result.objective, true)}</span>
                        </div>
                        <div style="margin-top: 8px; font-size: 0.8rem; color: #666;">
                            <i class="ri-information-line"></i> Valor de Z = ${formatNumber(result.objective, true)}
                        </div>
                    </div>
                    <div>
                        <h4 style="margin: 0 0 10px 0; color: #5a4636;"> Verificación de Restricciones</h4>
                        ${constraintsHtml}
                    </div>
                    <div style="margin-top: 15px; font-size: 0.75rem; color: #999; text-align: center; border-top: 1px solid #eee; padding-top: 10px;">
                        Resuelto con método Simplex (Álgebra lineal) | Iteraciones: ${result.tableau ? result.tableau.length - 1 : 'N/A'}
                    </div>
                </div>
            </div>
        `;
    }, 50);
}

// Exportar funciones globales
window.simplexSolver = {
    solveLP,
    resolverModelo,
    ModeloCerveza,
    ModeloWhisky,
    formatNumber
};