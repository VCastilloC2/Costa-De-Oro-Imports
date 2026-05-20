const EPS = 1e-7;

function solveLP(c, A, b) {
    const m = A.length;
    const n = c.length;
    const totalVars = n + m;
    let tableau = Array(m + 1).fill().map(() => Array(totalVars + 1).fill(0));

    for (let j = 0; j < n; j++) tableau[0][j] = -c[j];
    for (let i = 0; i < m; i++) {
        for (let j = 0; j < n; j++) tableau[i + 1][j] = A[i][j];
        tableau[i + 1][n + i] = 1;
        tableau[i + 1][totalVars] = b[i];
    }

    let basicVars = Array(m);
    for (let i = 0; i < m; i++) basicVars[i] = n + i;

    let iteration = 0;
    const maxIter = 1000;

    while (iteration < maxIter) {
        let enterCol = -1;
        let minCoeff = 0;
        for (let j = 0; j < totalVars; j++) {
            if (tableau[0][j] < minCoeff - EPS) {
                minCoeff = tableau[0][j];
                enterCol = j;
            }
        }
        if (enterCol === -1) break;

        let minRatio = Infinity;
        let leaveRow = -1;
        for (let i = 1; i <= m; i++) {
            if (tableau[i][enterCol] > EPS) {
                let ratio = tableau[i][totalVars] / tableau[i][enterCol];
                if (ratio < minRatio - EPS) {
                    minRatio = ratio;
                    leaveRow = i;
                }
            }
        }
        if (leaveRow === -1) return { status: 'unbounded', message: 'Problema no acotado' };

        const pivotVal = tableau[leaveRow][enterCol];
        for (let j = 0; j <= totalVars; j++) tableau[leaveRow][j] /= pivotVal;
        for (let i = 0; i <= m; i++) {
            if (i !== leaveRow && Math.abs(tableau[i][enterCol]) > EPS) {
                const factor = tableau[i][enterCol];
                for (let j = 0; j <= totalVars; j++) tableau[i][j] -= factor * tableau[leaveRow][j];
            }
        }
        basicVars[leaveRow - 1] = enterCol;
        iteration++;
    }

    if (iteration >= maxIter) return { status: 'max_iterations', message: 'No converge' };

    let solution = new Array(n).fill(0);
    for (let i = 0; i < m; i++) {
        if (basicVars[i] < n) solution[basicVars[i]] = tableau[i + 1][totalVars];
    }
    for (let i = 0; i < n; i++) if (Math.abs(solution[i]) < EPS) solution[i] = 0;

    let objectiveValue = tableau[0][totalVars];
    if (Math.abs(objectiveValue) < EPS) objectiveValue = 0;

    let slacks = new Array(m);
    for (let i = 0; i < m; i++) {
        let sum = 0;
        for (let j = 0; j < n; j++) sum += A[i][j] * solution[j];
        slacks[i] = b[i] - sum;
        if (Math.abs(slacks[i]) < EPS) slacks[i] = 0;
    }

    let activeConstraints = [];
    for (let i = 0; i < m; i++) if (slacks[i] <= EPS) activeConstraints.push(i);

    return {
        status: 'optimal',
        solution, objective: objectiveValue,
        slacks, activeConstraints,
        iterations: iteration
    };
}

function formatNumber(value, isMoney = false, decimals = 2) {
    if (value === undefined || value === null) return '0';
    let rounded = Math.round(value * Math.pow(10, decimals)) / Math.pow(10, decimals);
    if (Math.abs(rounded) < EPS) rounded = 0;
    if (isMoney) {
        return '$' + rounded.toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
    }
    return rounded.toLocaleString('es-CO', { minimumFractionDigits: 0, maximumFractionDigits: decimals });
}

// ==================== MODELOS ====================
const ModeloCerveza = {
    nombre: 'Cerveza',
    descripcion: 'Águila (Nacional) vs Heineken (Importada)',
    variables: ['X1 - Águila (330 ml)', 'X2 - Heineken (330 ml)'],
    nombresCortos: ['Águila', 'Heineken'],
    c: [1700, 3500],
    A: [[1800, 4500], [1, 1], [1, 0], [0, 1]],
    b: [500000, 200, 130, 90],
    constraintsDesc: [
        'Presupuesto: 1.800·X1 + 4.500·X2 ≤ 500.000 COP',
        'Espacio: X1 + X2 ≤ 200 unidades',
        'Demanda Águila: X1 ≤ 130',
        'Demanda Heineken: X2 ≤ 90'
    ],
    parametrosEconomicos: {
        preciosVenta: [3500, 8000], costosUnitarios: [1800, 4500],
        margenes: [1700, 3500], demandasMax: [130, 90],
        espacioTotal: 200, presupuestoTotal: 500000
    },
    valoresEsperadosSolver: [130, 59.11111111111111],
    objetivoEsperado: 130*1700 + 59.11111111111111*3500
};

const ModeloWhisky = {
    nombre: 'Whisky',
    descripcion: 'Johnnie Walker Red Label vs Jack Daniel\'s Old No.7',
    variables: ['X1 - JW Red Label (750 ml)', 'X2 - Jack Daniel\'s (750 ml)'],
    nombresCortos: ['JW Red Label', 'Jack Daniel\'s'],
    c: [35000, 45000],
    A: [[55000, 75000], [1, 1], [1, 0], [0, 1]],
    b: [3000000, 40, 25, 20],
    constraintsDesc: [
        'Presupuesto: 55.000·X1 + 75.000·X2 ≤ 3.000.000 COP',
        'Espacio: X1 + X2 ≤ 40 botellas',
        'Demanda JW: X1 ≤ 25',
        'Demanda Jack Daniel\'s: X2 ≤ 20'
    ],
    parametrosEconomicos: {
        preciosVenta: [90000, 120000], costosUnitarios: [55000, 75000],
        margenes: [35000, 45000], demandasMax: [25, 20],
        espacioTotal: 40, presupuestoTotal: 3000000
    },
    valoresEsperadosSolver: [20, 20],
    objetivoEsperado: 20*35000 + 20*45000
};

const ModeloRon = {
    nombre: 'Ron',
    descripcion: 'Ron Medellín Añejo vs Ron Zacapa 23',
    variables: ['X1 - Ron Medellín (750 ml)', 'X2 - Ron Zacapa 23 (750 ml)'],
    nombresCortos: ['Ron Medellín', 'Ron Zacapa 23'],
    c: [22000, 55000],
    A: [[28000, 95000], [1, 1], [1, 0], [0, 1]],
    b: [2500000, 50, 35, 18],
    constraintsDesc: [
        'Presupuesto: 28.000·X1 + 95.000·X2 ≤ 2.500.000 COP',
        'Espacio: X1 + X2 ≤ 50 botellas',
        'Demanda Ron Medellín: X1 ≤ 35',
        'Demanda Ron Zacapa 23: X2 ≤ 18'
    ],
    parametrosEconomicos: {
        preciosVenta: [50000, 150000], costosUnitarios: [28000, 95000],
        margenes: [22000, 55000], demandasMax: [35, 18],
        espacioTotal: 50, presupuestoTotal: 2500000
    },
    valoresEsperadosSolver: [33.582089552238806, 16.417910447761194],
    objetivoEsperado: 33.582089552238806*22000 + 16.417910447761194*55000
};

const ModeloVino = {
    nombre: 'Vino',
    descripcion: 'Gato Negro vs Santa Rita Reserva',
    variables: ['X1 - Gato Negro (750 ml)', 'X2 - Santa Rita Reserva (750 ml)'],
    nombresCortos: ['Gato Negro', 'Santa Rita'],
    c: [18000, 37000],
    A: [[22000, 48000], [1, 1], [1, 0], [0, 1]],
    b: [1800000, 60, 40, 22],
    constraintsDesc: [
        'Presupuesto: 22.000·X1 + 48.000·X2 ≤ 1.800.000 COP',
        'Espacio: X1 + X2 ≤ 60 botellas',
        'Demanda Gato Negro: X1 ≤ 40',
        'Demanda Santa Rita: X2 ≤ 22'
    ],
    parametrosEconomicos: {
        preciosVenta: [40000, 85000], costosUnitarios: [22000, 48000],
        margenes: [18000, 37000], demandasMax: [40, 22],
        espacioTotal: 60, presupuestoTotal: 1800000
    },
    valoresEsperadosSolver: [40, 19.166666666666664],
    objetivoEsperado: 40*18000 + 19.166666666666664*37000
};

// Función para renderizar gráfica de barras
function renderChart(modelo, solution, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const labels = [];
    const optimal = [];
    const limits = [];

    for (let i = 0; i < modelo.nombresCortos.length; i++) {
        labels.push(modelo.nombresCortos[i]);
        optimal.push(solution[i]);
        limits.push(modelo.parametrosEconomicos.demandasMax[i]);
    }
    labels.push('Espacio total');
    const totalSpace = solution.reduce((a,b) => a+b, 0);
    optimal.push(totalSpace);
    limits.push(modelo.parametrosEconomicos.espacioTotal);

    const options = {
        chart: { type: 'bar', height: 350, toolbar: { show: false }, fontFamily: 'Geist, sans-serif' },
        plotOptions: { bar: { horizontal: false, columnWidth: '55%', borderRadius: 8 } },
        series: [
            { name: 'Producción óptima', data: optimal, color: '#2e7d32' },
            { name: 'Límite máximo', data: limits, color: '#c62828' }
        ],
        xaxis: { categories: labels, labels: { style: { fontSize: '12px' } } },
        yaxis: { title: { text: 'Unidades' }, labels: { formatter: val => formatNumber(val) } },
        tooltip: { y: { formatter: val => formatNumber(val) + ' unidades' } },
        legend: { position: 'top' },
        grid: { borderColor: '#e9ecef' }
    };
    if (window.ApexCharts) {
        if (window.chartInstance) window.chartInstance.destroy();
        window.chartInstance = new ApexCharts(container, options);
        window.chartInstance.render();
    }
}

// Función principal
function resolverModelo(modelo) {
    const resultContainer = document.getElementById('simplexResult');
    if (!resultContainer) return;

    resultContainer.innerHTML = `<div class="simplex-loading"><div class="loader"></div><p>Resolviendo modelo ${modelo.nombre}...</p></div>`;

    setTimeout(() => {
        const result = solveLP(modelo.c, modelo.A, modelo.b);
        if (result.status !== 'optimal') {
            resultContainer.innerHTML = `<div class="error-card">Error: ${result.message}</div>`;
            return;
        }

        // Calcular porcentajes de uso
        const usoPresupuesto = modelo.A[0].reduce((sum, coeff, i) => sum + coeff * result.solution[i], 0);
        const porcPresupuesto = (usoPresupuesto / modelo.b[0]) * 100;
        const usoEspacio = result.solution.reduce((a,b) => a+b, 0);
        const porcEspacio = (usoEspacio / modelo.b[1]) * 100;

        // Tarjetas de resumen
        const summaryCards = `
            <div class="simplex-summary">
                <div class="summary-card">
                    <div class="summary-label">Beneficio máximo</div>
                    <div class="summary-value">${formatNumber(result.objective, true)}</div>
                    <div class="summary-desc">Valor óptimo de Z</div>
                </div>
                <div class="summary-card">
                    <div class="summary-label">Espacio en bodega</div>
                    <div class="summary-value">${formatNumber(usoEspacio)} / ${formatNumber(modelo.b[1])} uds</div>
                    <div class="summary-desc">Uso: ${porcEspacio.toFixed(1)}% del total</div>
                </div>
                <div class="summary-card">
                    <div class="summary-label">Presupuesto</div>
                    <div class="summary-value">${formatNumber(usoPresupuesto, true)} / ${formatNumber(modelo.b[0], true)}</div>
                    <div class="summary-desc">Uso: ${porcPresupuesto.toFixed(1)}% del total</div>
                </div>
                <div class="summary-card">
                    <div class="summary-label">Restricciones activas</div>
                    <div class="summary-value">${result.activeConstraints.length} de ${modelo.A.length}</div>
                    <div class="summary-desc">Recursos completamente utilizados</div>
                </div>
            </div>
        `;

        // Solución óptima
        let solutionHtml = '<div class="simplex-solution"><h3>Solución óptima (unidades por día)</h3><div class="solution-list">';
        for (let i = 0; i < modelo.variables.length; i++) {
            const val = result.solution[i];
            const esperado = modelo.valoresEsperadosSolver[i];
            const match = Math.abs(val - esperado) < 0.01;
            solutionHtml += `
                <div class="solution-item">
                    <span class="solution-name">${modelo.nombresCortos[i]}</span>
                    <span class="solution-value">${formatNumber(val)} uds</span>
                    ${!match ? `<span class="solution-solver">(Solver: ${formatNumber(esperado)})</span>` : '<span class="solution-check">✓</span>'}
                </div>
            `;
        }
        solutionHtml += '</div></div>';

        // Tabla económica
        const eco = modelo.parametrosEconomicos;
        let ecoHtml = `
            <div class="simplex-economics">
                <h3>Datos económicos por unidad</h3>
                <table>
                    <thead>
                        <tr><th>Producto</th><th>Costo (COP)</th><th>Precio venta (COP)</th><th>Margen (COP)</th></tr>
                    </thead>
                    <tbody>
        `;
        for (let i = 0; i < modelo.nombresCortos.length; i++) {
            ecoHtml += `
                <tr>
                    <td><strong>${modelo.nombresCortos[i]}</strong></td>
                    <td>${formatNumber(eco.costosUnitarios[i], true)}</td>
                    <td>${formatNumber(eco.preciosVenta[i], true)}</td>
                    <td class="margin-value">${formatNumber(eco.margenes[i], true)}</td>
                </tr>
            `;
        }
        ecoHtml += '</tbody></table></div>';

        // Restricciones (SIN precios sombra)
        let constraintsHtml = '<div class="simplex-constraints"><h3>Verificación de restricciones</h3>';
        for (let i = 0; i < modelo.constraintsDesc.length; i++) {
            let lhs = 0;
            for (let j = 0; j < modelo.c.length; j++) lhs += modelo.A[i][j] * result.solution[j];
            const slack = result.slacks[i];
            const isActive = slack <= EPS;
            constraintsHtml += `
                <div class="constraint-row ${isActive ? 'active' : ''}">
                    <div class="constraint-desc">${modelo.constraintsDesc[i]}</div>
                    <div class="constraint-status">
                        Lado izquierdo: ${formatNumber(lhs)} ≤ ${formatNumber(modelo.b[i])} 
                        ${isActive ? '<span style="color:#2e7d32; margin-left:8px;">[RESTRICCIÓN ACTIVA]</span>' : `<span style="color:#6c757d;">(Holgura: ${formatNumber(slack)} unidades disponibles)</span>`}
                    </div>
                </div>
            `;
        }
        constraintsHtml += '</div>';

        // Comparación con Solver
        const objMatch = Math.abs(result.objective - modelo.objetivoEsperado) < 1;

        // Montar todo
        resultContainer.innerHTML = `
            <div class="simplex-container">
                ${summaryCards}
                <div class="simplex-two-columns">
                    ${solutionHtml}
                    ${ecoHtml}
                </div>
                <div class="simplex-chart">
                    <h3>Comparativa: Producción óptima vs Límites</h3>
                    <div id="optimizationChart" style="height:350px;"></div>
                </div>
                ${constraintsHtml}
                <div class="simplex-footer">
                    Método Simplex | Iteraciones: ${result.iterations} | Tolerancia: ${EPS}
                    ${objMatch ? '| Coincide con Solver de Excel' : ''}
                </div>
            </div>
        `;

        renderChart(modelo, result.solution, 'optimizationChart');
    }, 50);
}

window.simplexSolver = {
    solveLP, resolverModelo,
    ModeloCerveza, ModeloWhisky, ModeloRon, ModeloVino,
    formatNumber
};