$(document).ready(function() {
    // Busca el select por su ID y lo inicializa como un Select2
    $('#moduleSearch').select2({
        // Texto que aparece antes de seleccionar o escribir algo
        placeholder: "Buscar una sección...",
        // Tema para que se vea como el resto de tus componentes Bootstrap 5
        theme: "bootstrap-5"
    });

    // Evento que se dispara cuando el usuario selecciona una opción
    $('#moduleSearch').on('select2:select', function (e) {
        // Obtenemos los datos de la opción seleccionada
        var data = e.params.data;

        // 'data.id' contiene el valor del atributo 'value' de la opción (la URL)
        if (data.id) {
            // Redirigimos el navegador a esa URL
            window.location.href = data.id;
        }
    });
});