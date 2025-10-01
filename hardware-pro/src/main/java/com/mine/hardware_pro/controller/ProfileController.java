package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.model.User;
import com.mine.hardware_pro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showProfileForm(Principal principal, Model model) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("user", user);
        return "pages/profile/profile-form";
    }

    @PostMapping("/save")
    public String saveProfile(@ModelAttribute User userFormData, Principal principal, RedirectAttributes ra) {
        try {
            String currentUsername = principal.getName();
            User currentUser = userRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            currentUser.setFirstName(userFormData.getFirstName());
            currentUser.setLastName(userFormData.getLastName());

            userRepository.save(currentUser);

            ra.addFlashAttribute("success", "Perfil actualizado exitosamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al actualizar el perfil.");
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "pages/profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(Principal principal,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes ra) {
        String username = principal.getName();
        User currentUser = userRepository.findByEmail(username).orElseThrow();

        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            ra.addFlashAttribute("error", "La contraseña actual es incorrecta.");
            return "redirect:/profile/change-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Las nuevas contraseñas no coinciden.");
            return "redirect:/profile/change-password";
        }
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        ra.addFlashAttribute("success", "Contraseña actualizada exitosamente.");
        return "redirect:/dashboard";
    }
}
