package com.mine.hardware_pro.service;

import com.mine.hardware_pro.auth.RegisterRequest;
import com.mine.hardware_pro.model.Role;
import com.mine.hardware_pro.model.User;
import com.mine.hardware_pro.repository.RoleRepository;
import com.mine.hardware_pro.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean register(RegisterRequest request) {
        // 1. Verificamos si el email ya existe en la base de datos
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            // Si el Optional contiene un usuario, significa que el email ya está en uso.
            return false; // Devolvemos 'false' para indicar que el registro falló.
        }

        // Si el correo no existe, continuamos con el proceso de registro normal...
        Role defaultRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Rol por defecto 'USER' no encontrado"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(defaultRole)
                .build();

        userRepository.save(user);

        // 2. Si todo sale bien y el usuario se guarda, se devuelve true.
        return true;
    }
}
