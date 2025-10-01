$(document).ready(function() {
    $('#moduleSearch').select2({
        placeholder: "Buscar una sección...",
        theme: "bootstrap-5"
    });

    $('#moduleSearch').on('select2:select', function (e) {
        var data = e.params.data;
        if (data.id) {
            window.location.href = data.id;
        }
    });
});