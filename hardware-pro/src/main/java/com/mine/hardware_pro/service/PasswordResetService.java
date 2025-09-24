package com.mine.hardware_pro.service;

import com.mine.hardware_pro.model.PasswordResetToken;
import com.mine.hardware_pro.model.User;
import com.mine.hardware_pro.repository.PasswordResetTokenRepository;
import com.mine.hardware_pro.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

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

            // La lógica para crear y guardar el token no cambia.
            Optional<PasswordResetToken> existingToken = tokenRepository.findByUser(user);
            PasswordResetToken token;
            if (existingToken.isPresent()) {
                token = existingToken.get();
                token.setToken(java.util.UUID.randomUUID().toString());
                token.setExpiryDate(LocalDateTime.now().plusHours(2));
            } else {
                token = new PasswordResetToken(user);
            }
            tokenRepository.save(token);
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                String url = "http://localhost:8080/reset-password?token=" + token.getToken();

                // Plantilla HTML para el correo.
                String htmlMsg = """
                    <html lang="es">
                    <head>
                        <style>
                            body { font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333; }
                            .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
                            .header { text-align: center; padding-bottom: 20px; border-bottom: 1px solid #eeeeee; }
                            .header h1 { color: #0056b3; }
                            .content { padding: 20px 0; }
                            .content p { line-height: 1.6; }
                            .button { display: block; width: 200px; margin: 20px auto; padding: 15px 0; background-color: #007bff; color: #ffffff !important; text-align: center; text-decoration: none; border-radius: 5px; }
                            .footer { text-align: center; font-size: 12px; color: #777; padding-top: 20px; border-top: 1px solid #eeeeee; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h1>Recuperación de Contraseña</h1>
                            </div>
                            <div class="content">
                                <p>Hola %s,</p>
                                <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta. Si no has sido tú, por favor ignora este mensaje.</p>
                                <p>Para continuar, haz clic en el siguiente botón:</p>
                                <a href="%s" class="button">Restablecer Contraseña</a>
                                <p>Este enlace de recuperación expirará en 2 horas.</p>
                                <p>¡Gracias!</p>
                            </div>
                            <div class="footer">
                                <p>&copy; %d Tuerca Dorada. Todos los derechos reservados.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                """.formatted(user.getFirstName(), url, java.time.Year.now().getValue());

                helper.setTo(user.getEmail());
                helper.setSubject("Recuperación de Contraseña");
                helper.setText(htmlMsg, true); // El 'true' indica que el contenido es HTML.

                mailSender.send(mimeMessage);
                return true;

            } catch (MessagingException e) {
                // En caso de error al enviar el correo, retornamos false.
                // Puedes agregar un log aquí si lo necesitas: e.printStackTrace();
                return false;
            }
            // --- FIN DE LA MODIFICACIÓN ---
        }
        return false;
    }

    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);
        if (tokenOptional.isPresent()) {
            PasswordResetToken resetToken = tokenOptional.get();
            return resetToken.getExpiryDate().isAfter(LocalDateTime.now());
        }
        return false;
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElse(null);

        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = resetToken.getUser();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        return true;
    }
}