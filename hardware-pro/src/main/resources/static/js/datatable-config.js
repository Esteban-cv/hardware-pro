$(document).ready(function() {
    $('#myTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/2.0.8/i18n/es-ES.json'
        },
        responsive: true,
        columnDefs: [
            {
                orderable: false,
                targets: -1
            }
        ]
    });
});