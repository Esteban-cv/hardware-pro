$(document).ready(function() {
    const table = $('#myTable');
    if (table.length === 0) {
        return;
    }
    let dataTableOptions = {
        language: {
            url: 'https://cdn.datatables.net/plug-ins/2.0.8/i18n/es-ES.json'
        },
        responsive: true,
        columnDefs: [{
            orderable: false,
            targets: -1
        }]
    };

    if (table.hasClass('table-suppliers')) {
        dataTableOptions.order = [[ 4, 'asc' ]];
    } else if(table.hasClass('table-employees')) {
        dataTableOptions.order = [[ 4, 'asc' ]];
    }
    table.DataTable(dataTableOptions);
});