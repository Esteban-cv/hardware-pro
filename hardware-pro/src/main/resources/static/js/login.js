// login.js
document.addEventListener('DOMContentLoaded', function() {
    const submitBtn = document.getElementById('submitBtn');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');

    submitBtn.addEventListener('click', async (e) => {
        e.preventDefault(); // Evita que el formulario se envíe de forma tradicional

        const email = emailInput.value;
        const password = passwordInput.value;

        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                // Manejar errores de autenticación (ej. credenciales inválidas)
                const error = await response.json();
                alert('Error de autenticación: ' + error.message);
                return;
            }

            const authResponse = await response.json();
            const token = authResponse.token;

            // Almacena el token en localStorage
            localStorage.setItem('jwtToken', token);

            // Redirige al usuario a la página principal (o a un endpoint seguro)
            window.location.href = '/api/v1/demo'; // O la ruta que quieras

        } catch (error) {
            console.error('Error:', error);
            alert('Error al intentar iniciar sesión. Inténtalo de nuevo.');
        }
    });
});