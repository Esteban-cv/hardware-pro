package com.mine.hardware_pro.service;

import com.mine.hardware_pro.model.PasswordResetToken;
import com.mine.hardware_pro.model.User;
import com.mine.hardware_pro.repository.PasswordResetTokenRepository;
import com.mine.hardware_pro.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar PasswordEncoder
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder; // Inyectar PasswordEncoder

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean sendPasswordResetEmail(String userEmail) {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 1. Busca si ya existe un token para este usuario
            Optional<PasswordResetToken> existingToken = tokenRepository.findByUser(user);

            PasswordResetToken token;
            if (existingToken.isPresent()) {
                // 2. Si ya existe un token, lo actualiza con un nuevo valor y fecha
                token = existingToken.get();
                token.setToken(java.util.UUID.randomUUID().toString()); // Crea un nuevo token
                token.setExpiryDate(LocalDateTime.now().plusHours(2)); // Actualiza la fecha de expiración
            } else {
                // 3. Si no existe un token, crea uno nuevo
                token = new PasswordResetToken(user);
            }

            // 4. Guarda el token (si es nuevo) o actualiza el existente
            tokenRepository.save(token);

            // Crea el mensaje de correo
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(user.getEmail());
            email.setSubject("Recuperación de Contraseña");
            String url = "http://localhost:8080/reset-password?token=" + token.getToken();
            email.setText("Para restablecer tu contraseña, haz clic en el siguiente enlace: " + url);

            mailSender.send(email);
            return true;
        }
        return false;
    }

    // Método para validar el token que faltaba
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
        if (tokenOptional.isPresent()) {
            PasswordResetToken resetToken = tokenOptional.get();
            // Verifica si el token no ha expirado
            return resetToken.getExpiryDate().isAfter(LocalDateTime.now());
        }
        return false;
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElse(null);

        // 1. Verificar si el token existe y si no ha expirado
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false; // El token es inválido o ha expirado
        }

        // 2. Encontrar al usuario asociado al token
        User user = resetToken.getUser();

        // 3. Codificar la nueva contraseña y actualizar el usuario
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // 4. Eliminar el token para evitar que se use de nuevo
        tokenRepository.delete(resetToken);

        return true; // Contraseña actualizada con éxito
    }
}