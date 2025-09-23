package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.auth.ForgotPasswordRequest;
import com.mine.hardware_pro.auth.PasswordResetRequest;
import com.mine.hardware_pro.auth.RegisterRequest;
import com.mine.hardware_pro.service.AuthenticationService;
import com.mine.hardware_pro.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final AuthenticationService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthenticationService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/auth-login";
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "pages/home";
    }

    // Ruta para mostrar el formulario de registro
    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/auth-register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterRequest registerRequest) {
        authService.register(registerRequest);

        return "redirect:/login?registerSuccess";
    }

    // Ruta para mostrar el formulario de "olvidé mi contraseña"
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/auth-forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@ModelAttribute ForgotPasswordRequest request) {
        boolean emailFound = passwordResetService.sendPasswordResetEmail(request.getEmail());

        if (emailFound) {
            return "redirect:/forgot-password?success";
        } else {
            return "redirect:/forgot-password?error";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        // Verifica si el token existe y no ha expirado
        boolean isValidToken = passwordResetService.validatePasswordResetToken(token);

        if (isValidToken) {
            model.addAttribute("token", token);
            return "auth/auth-reset-password";
        } else {
            model.addAttribute("error", "El token es inválido o ha expirado.");
            return "auth/auth-login";
        }
    }

    @PostMapping("/reset-password")
    public String handlePasswordReset(@ModelAttribute PasswordResetRequest request, Model model) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "auth/auth-reset-password";
        }

        boolean isReset = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

        if (isReset) {
            // Redirige al login con un mensaje de éxito
            return "redirect:/login?resetSuccess";
        } else {
            // Redirige a la página de olvido de contraseña con un error
            return "redirect:/forgot-password?resetError";
        }
    }

}
