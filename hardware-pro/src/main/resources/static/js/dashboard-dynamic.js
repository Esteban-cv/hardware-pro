// dashboard-dynamic.js - Dashboard dinámico para Ferretería con datos reales

// Configuración de colores
var chartColors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    info: '#41B1F9',
    blue: '#3245D1',
    purple: 'rgb(153, 102, 255)',
    grey: '#EBEFF6'
};

// Variables globales para gráficos
var myBar;
var radialBars;

// ========== CARGAR DATOS DE TOP CLIENTES ==========
async function loadTopClients() {
    try {
        const response = await fetch('/api/dashboard/top-clients');
        const clients = await response.json();

        updateClientsTable(clients);
    } catch (error) {
        console.error('Error cargando top clientes:', error);
    }
}

// Actualizar tabla de top clientes
function updateClientsTable(clients) {
    const tbody = document.querySelector('#table1 tbody');
    if (!tbody) return;

    tbody.innerHTML = '';

    clients.forEach((client, index) => {
        const row = tbody.insertRow();
        row.innerHTML = `
      <td>
        <strong>#${index + 1}</strong> ${client.clientName}
      </td>
      <td>${client.clientEmail || 'N/A'}</td>
      <td>${client.clientPhone || 'N/A'}</td>
      <td>
        <span class="badge ${client.clientActive ? 'bg-success' : 'bg-danger'}">
          ${client.status}
        </span>
        <small class="d-block text-muted">${currencySymbol}${parseFloat(client.totalSpent).toFixed(2)}</small>
      </td>
    `;
    });
}

// ========== CARGAR VENTAS MENSUALES ==========
async function loadMonthlySales() {
    try {
        const response = await fetch('/api/dashboard/monthly-sales');
        const data = await response.json();

        const labels = data.map(item => item.month);
        const values = data.map(item => parseFloat(item.total));

        updateBarChart(labels, values);
    } catch (error) {
        console.error('Error cargando ventas mensuales:', error);
    }
}

// Actualizar gráfico de barras con datos reales
function updateBarChart(labels, values) {
    var ctxBar = document.getElementById("bar");
    if (!ctxBar) return;

    const ctx = ctxBar.getContext("2d");

    // Destruir gráfico anterior si existe
    if (myBar) {
        myBar.destroy();
    }

    // Calcular el total y porcentaje
    const currentMonthValue = values[values.length - 1] || 0;
    const lastMonthValue = values[values.length - 2] || 0;
    let percentageChange = 0;

    if (lastMonthValue > 0) {
        percentageChange = ((currentMonthValue - lastMonthValue) / lastMonthValue * 100).toFixed(1);
    }

    // Actualizar el texto del total
    const totalElement = document.querySelector('.card-body h1');
    if (totalElement) {
        totalElement.textContent = `${currencySymbol}${currentMonthValue.toFixed(0).toLocaleString()}`;
    }

    // Actualizar el porcentaje
    const percentageElement = document.querySelector('.text-xs span');
    if (percentageElement) {
        const isPositive = percentageChange >= 0;
        percentageElement.className = isPositive ? 'text-green' : 'text-red';
        percentageElement.innerHTML = `<i data-feather="bar-chart" width="15"></i> ${isPositive ? '+' : ''}${percentageChange}%`;

        // Re-inicializar feather icons
        if (typeof feather !== 'undefined') {
            feather.replace();
        }
    }

    // Determinar el índice del mes actual
    const currentMonthIndex = new Date().getMonth();

    myBar = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: `Ventas (${currencySymbol})`,
                backgroundColor: values.map((v, i) =>
                    i === currentMonthIndex ? chartColors.blue : chartColors.grey
                ),
                data: values
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            barRoundness: 1,
            title: {
                display: false
            },
            legend: {
                display: false
            },
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true,
                        padding: 10,
                        callback: function(value) {
                            return `${currencySymbol}` + value.toLocaleString();
                        }
                    },
                    gridLines: {
                        drawBorder: false,
                    }
                }],
                xAxes: [{
                    gridLines: {
                        display: false,
                        drawBorder: false
                    }
                }]
            },
            tooltips: {
                callbacks: {
                    label: function(tooltipItem) {
                        return `Ventas (${currencySymbol})` + tooltipItem.yLabel.toLocaleString();
                    }
                }
            }
        }
    });
}

// ========== CARGAR EARNINGS (GANANCIAS) ==========
async function loadEarnings() {
    try {
        const response = await fetch('/api/dashboard/earnings');
        const earnings = await response.json();

        updateEarningsChart(earnings);
    } catch (error) {
        console.error('Error cargando earnings:', error);
    }
}

// Actualizar gráfico radial de earnings
function updateEarningsChart(earnings) {
    const radialElement = document.querySelector("#radialBars");
    if (!radialElement) return;

    // Calcular porcentajes relativos al año
    const yearTotal = parseFloat(earnings.thisYear);
    const monthPercentage = yearTotal > 0 ? (parseFloat(earnings.thisMonth) / yearTotal * 100).toFixed(1) : 0;
    const todayPercentage = yearTotal > 0 ? (parseFloat(earnings.today) / yearTotal * 100).toFixed(1) : 0;
    const remainingPercentage = (100 - monthPercentage - todayPercentage).toFixed(1);

    // Destruir gráfico anterior si existe
    if (radialBars) {
        radialBars.destroy();
    }

    var radialBarsOptions = {
        series: [parseFloat(monthPercentage), parseFloat(todayPercentage), parseFloat(remainingPercentage)],
        chart: {
            height: 350,
            type: "radialBar",
        },
        theme: {
            mode: "light",
            palette: "palette1",
            monochrome: {
                enabled: true,
                color: "#3245D1",
                shadeTo: "light",
                shadeIntensity: 0.65,
            },
        },
        plotOptions: {
            radialBar: {
                dataLabels: {
                    name: {
                        offsetY: -15,
                        fontSize: "22px",
                    },
                    value: {
                        fontSize: "2.5rem",
                    },
                    total: {
                        show: true,
                        label: "Total Año",
                        color: "#25A6F1",
                        fontSize: "16px",
                        formatter: function(w) {
                            return "€" + yearTotal.toFixed(0).toLocaleString();
                        },
                    },
                },
            },
        },
        labels: ["Este Mes", "Hoy", "Resto del Año"],
    };

    radialBars = new ApexCharts(radialElement, radialBarsOptions);
    radialBars.render();

    // Actualizar texto de earnings debajo del gráfico
    const earningsTextElement = document.querySelector('.text-center h1.text-green');
    if (earningsTextElement) {
        const change = parseFloat(earnings.percentageChange).toFixed(1);
        const isPositive = change >= 0;
        earningsTextElement.className = isPositive ? 'text-green' : 'text-red';
        earningsTextElement.textContent = `${isPositive ? '+' : ''}€${parseFloat(earnings.thisMonth).toFixed(0).toLocaleString()}`;
    }
}

// ========== CONFIGURACIÓN DE GRÁFICOS PEQUEÑOS ==========
var lineChartConfig = {
    type: "line",
    data: {
        labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"],
        datasets: [{
            backgroundColor: "#fff",
            borderColor: "#fff",
            data: [20, 40, 20, 70, 10, 50, 20],
            fill: false,
            pointBorderWidth: 100,
            pointBorderColor: "transparent",
            pointRadius: 3,
            pointBackgroundColor: "transparent",
            pointHoverBackgroundColor: "rgba(63,82,227,1)",
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        layout: {
            padding: {
                left: -10,
                top: 10,
            },
        },
        legend: {
            display: false,
        },
        title: {
            display: false,
        },
        tooltips: {
            mode: "index",
            intersect: false,
        },
        hover: {
            mode: "nearest",
            intersect: true,
        },
        scales: {
            xAxes: [{
                gridLines: {
                    drawBorder: false,
                    display: false,
                },
                ticks: {
                    display: false,
                },
            }],
            yAxes: [{
                gridLines: {
                    display: false,
                    drawBorder: false,
                },
                ticks: {
                    display: false,
                },
            }],
        },
    },
};

// Inicializar gráficos pequeños
function initSmallCharts() {
    if (document.getElementById("canvas1")) {
        let ctx1 = document.getElementById("canvas1").getContext("2d");
        new Chart(ctx1, lineChartConfig);
    }

    if (document.getElementById("canvas2")) {
        let ctx2 = document.getElementById("canvas2").getContext("2d");
        new Chart(ctx2, lineChartConfig);
    }

    if (document.getElementById("canvas3")) {
        let ctx3 = document.getElementById("canvas3").getContext("2d");
        new Chart(ctx3, lineChartConfig);
    }

    if (document.getElementById("canvas4")) {
        let ctx4 = document.getElementById("canvas4").getContext("2d");
        new Chart(ctx4, lineChartConfig);
    }
}

// ========== INICIALIZACIÓN ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('Inicializando dashboard...');

    // Inicializar gráficos estáticos pequeños
    initSmallCharts();

    // Cargar datos dinámicos
    loadMonthlySales();
    loadEarnings();
    loadTopClients();

    // Actualizar cada 5 minutos
    setInterval(() => {
        loadMonthlySales();
        loadEarnings();
        loadTopClients();
    }, 300000); // 5 minutos
});