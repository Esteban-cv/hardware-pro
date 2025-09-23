document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const errorAlert = document.getElementById('error-alert');
    const logoutAlert = document.getElementById('logout-alert');
    const resetSuccessAlert = document.getElementById('resetsuccess-alert');

    function hideAfterDelay(alertElement) {
        setTimeout(() => {
            alertElement.classList.add('d-none');
        }, 5000);
    }

    function clearUrlParams() {
        const url = new URL(window.location);
        url.search = ''; // borra todos los query params
        window.history.replaceState({}, document.title, url.toString());
    }

    if (urlParams.has('error') && errorAlert) {
        errorAlert.classList.remove('d-none');
        hideAfterDelay(errorAlert);
        clearUrlParams();
    }

    if (urlParams.has('logout') && logoutAlert) {
        logoutAlert.classList.remove('d-none');
        hideAfterDelay(logoutAlert);
        clearUrlParams();
    }

    if (urlParams.has('resetSuccess') && resetSuccessAlert) {
        resetSuccessAlert.classList.remove('d-none');
        hideAfterDelay(resetSuccessAlert);
        clearUrlParams();
    }
});
