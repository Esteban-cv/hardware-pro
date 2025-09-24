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

    public void register(RegisterRequest request) {
        // Encontrar el rol por defecto (por ejemplo, "USER")
        Role defaultRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Rol por defecto 'USER' no encontrado"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encriptar la contraseña
                .role(defaultRole) // Asignar el rol por defecto
                .build();

        userRepository.save(user);
    }
}
