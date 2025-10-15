// Este código se ejecuta una vez que todo el contenido del HTML ha cargado
document.addEventListener('DOMContentLoaded', function() {

    // =================================================================================
    // PARTE 1: NOTIFICACIONES PARA PÁGINAS DE LOGIN/REGISTRO (LEYENDO URL PARAMS)
    // =================================================================================
    const urlParams = new URLSearchParams(window.location.search);

    // Función para limpiar los parámetros de la URL después de mostrar la alerta
    function clearUrlParams() {
        // Verifica si hay algún parámetro antes de modificar el historial
        if (window.location.search !== '') {
            const url = new URL(window.location);
            url.search = ''; // Borra todos los query params
            window.history.replaceState({}, document.title, url.toString());
        }
    }

    // Alerta para credenciales incorrectas en el login
    if (urlParams.has('error')) {
        Swal.fire({
            icon: 'error',
            title: 'Credenciales Incorrectas',
            text: 'Por favor, verifica tu correo y contraseña.',
        });
        clearUrlParams();
    }

    // Alerta para cierre de sesión exitoso
    if (urlParams.has('logout')) {
        Swal.fire({
            icon: 'success',
            title: '¡Sesión Cerrada!',
            text: 'Has cerrado tu sesión exitosamente.',
            timer: 2500,
            showConfirmButton: false
        });
        clearUrlParams();
    }

    // Alerta para registro exitoso
    if (urlParams.has('registerSuccess')) {
        Swal.fire({
            icon: 'success',
            title: '¡Registro Exitoso!',
            text: 'Tu cuenta ha sido creada. Ahora puedes iniciar sesión.',
        });
        clearUrlParams();
    }

    // Alerta para reseteo de contraseña exitoso
    if (urlParams.has('resetSuccess')) {
        Swal.fire({
            icon: 'success',
            title: '¡Contraseña Restablecida!',
            text: 'Tu contraseña ha sido cambiada con éxito.',
        });
        clearUrlParams();
    }

    // =================================================================================
    // PARTE 2: NOTIFICACIONES PARA CRUD (LEYENDO ATRIBUTOS data-*)
    // =================================================================================
    const body = document.body;
    const successMessage = body.dataset.successMessage;
    const errorMessage = body.dataset.errorMessage;

    if (successMessage) {
        Swal.fire({
            icon: 'success',
            title: '¡Éxito!',
            text: successMessage,
            timer: 2500,
            showConfirmButton: false
        });
    }

    if (errorMessage) {
        Swal.fire({
            icon: 'error',
            title: '¡Error!',
            text: errorMessage
        });
    }

    // ===============================================================
    // PARTE 3: CONFIRMACIÓN DE BORRADO
    // ===============================================================
    const deleteForms = document.querySelectorAll('.form-delete');
    const actionForms = document.querySelectorAll('.form-employee-inactivate');
    const inactivateSupplierForm = document.querySelectorAll('.form-supplier-inactivate');
    const inactivateClientForm = document.querySelectorAll('.form-client-inactivate');

    actionForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            // Obtenemos el texto del título del botón (ej. "Inactivar Empleado")
            const buttonTitle = event.submitter.getAttribute('title');
            const actionText = buttonTitle || 'ejecutar esta acción'; // Texto por defecto

            Swal.fire({
                title: `¿Estás seguro de que quieres ${actionText.toLowerCase()}?`,
                text: "Esta acción cambiará el estado del empleado.",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, ¡continuar!',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    this.submit();
                }
            });
        });
    });

    inactivateSupplierForm.forEach(form => {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const buttonTitle = event.submitter.getAttribute('title');
            const actionText = buttonTitle || 'ejecutar esta acción';

            Swal.fire({
                title: `¿Estás seguro de que quieres ${actionText.toLowerCase()}?`,
                text: "Esta acción cambiará el estado del proveedor.",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, ¡continuar!',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    this.submit();
                }
            });
        });
    });

    inactivateClientForm.forEach(form => {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            const buttonTitle = event.submitter.getAttribute('title');
            const actionText = buttonTitle || 'ejecutar esta acción';

            Swal.fire({
                title: `¿Estás seguro de que quieres ${actionText.toLowerCase()}?`,
                text: "Esta acción cambiará el estado del cliente.",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, ¡continuar!',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    this.submit();
                }
            });
        });
    });

    deleteForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            event.preventDefault();

            Swal.fire({
                title: '¿Estás seguro?',
                text: "¡No podrás revertir esta acción!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Sí, ¡eliminar!',
                cancelButtonText: 'Cancelar'
            }).then((result) => {
                if (result.isConfirmed) {
                    this.submit();
                }
            });
        });
    });
});