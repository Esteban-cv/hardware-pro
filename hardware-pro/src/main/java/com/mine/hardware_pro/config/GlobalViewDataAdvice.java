package com.mine.hardware_pro.config;

import com.mine.hardware_pro.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalViewDataAdvice {

    @Autowired
    private SettingService settingService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("globalCompanyName", settingService.getSettingValue("COMPANY_NAME"));
        model.addAttribute("globalCompanyLogo", settingService.getSettingValue("COMPANY_LOGO"));
        model.addAttribute("globalSidebarCollapsed", settingService.getSettingValue("SIDEBAR_COLLAPSED"));

        String logoPath = settingService.getSettingValue("COMPANY_LOGO");

        if (logoPath != null && !logoPath.isBlank()) {
            model.addAttribute("globalCompanyLogo", "/uploads/" + logoPath); // <-- Construye la ruta aquí
        } else {
            model.addAttribute("globalCompanyLogo", "/img/logo-rezisable.png"); // Tu logo por defecto del sidebar
        }

        // Establecer valores por defecto si no están en la BD
        if (model.getAttribute("globalCompanyName") == null) {
            model.addAttribute("globalCompanyName", "Tu Aplicación");
        }
        if (model.getAttribute("globalCompanyLogo") == null) {
            model.addAttribute("globalCompanyLogo", "assets/images/logo/logo.svg"); // Ruta de tu logo por defecto
        }
        if (model.getAttribute("globalSidebarCollapsed") == null) {
            model.addAttribute("globalSidebarCollapsed", "false"); // Sidebar expandido por defecto
        }
    }
}