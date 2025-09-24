// Usamos el modo estricto de jQuery para evitar conflictos
$(document).ready(function() {
    // Busca la tabla con el ID 'myTable' y la inicializa como una DataTable
    $('#myTable').DataTable({
        // Opción para poner la tabla en español
        language: {
            url: 'https://cdn.datatables.net/plug-ins/2.0.8/i18n/es-ES.json'
        },
        // Activa el diseño responsive para que se adapte a móviles
        responsive: true,
        // Define qué columnas no se pueden ordenar (en este caso, la última)
        columnDefs: [
            {
                orderable: false, // Desactiva el ordenamiento
                targets: -1     // Se aplica a la última columna (la de Acciones)
            }
        ]
    });
});