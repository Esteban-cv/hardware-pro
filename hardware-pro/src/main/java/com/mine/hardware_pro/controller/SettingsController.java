package com.mine.hardware_pro.controller;

import com.mine.hardware_pro.service.FileStorageService; // Para la subida de archivos
import com.mine.hardware_pro.service.SettingService;   // Para los ajustes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    private SettingService settingService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Muestra el formulario de ajustes con los valores actuales.
     * Carga todos los ajustes desde la base de datos.
     */
    @GetMapping
    public String showSettingsForm(Model model) {
        model.addAttribute("companyName", settingService.getSettingValue("COMPANY_NAME"));
        model.addAttribute("companyRut", settingService.getSettingValue("COMPANY_RUT"));
        model.addAttribute("companyPhone", settingService.getSettingValue("COMPANY_PHONE"));
        model.addAttribute("companyEmail", settingService.getSettingValue("COMPANY_EMAIL"));
        model.addAttribute("companyAddress", settingService.getSettingValue("COMPANY_ADDRESS"));
        model.addAttribute("companyLogo", settingService.getSettingValue("COMPANY_LOGO"));
        model.addAttribute("vatRate", settingService.getVatRate());
        model.addAttribute("currencySymbol", settingService.getSettingValue("CURRENCY_SYMBOL"));
        model.addAttribute("minStockAlert", settingService.getSettingValue("MIN_STOCK_ALERT"));
        model.addAttribute("themeMode", settingService.getSettingValue("THEME_MODE"));
        model.addAttribute("sidebarCollapsed", settingService.getSettingValue("SIDEBAR_COLLAPSED"));
        return "pages/settings/settings-form";
    }

    /**
     * Procesa la solicitud para guardar los ajustes.
     * Recibe los parámetros del formulario y el archivo del logo.
     */
    @PostMapping("/save")
    public String saveSettings(

            @RequestParam("companyName") String companyName,
            @RequestParam("companyRut") String companyRut,
            @RequestParam("companyPhone") String companyPhone,
            @RequestParam("companyEmail") String companyEmail,
            @RequestParam("companyAddress") String companyAddress,
            @RequestParam(value = "companyLogoFile", required = false) MultipartFile companyLogoFile,

            @RequestParam("vatRate") BigDecimal vatRate,
            @RequestParam("currencySymbol") String currencySymbol,
            @RequestParam("minStockAlert") String minStockAlert,

            @RequestParam("themeMode") String themeMode,
            @RequestParam(value = "sidebarCollapsed", required = false) String sidebarCollapsed,

            RedirectAttributes ra) {

        try {
            settingService.saveSetting("COMPANY_NAME", companyName);
            settingService.saveSetting("COMPANY_RUT", companyRut);
            settingService.saveSetting("COMPANY_PHONE", companyPhone);
            settingService.saveSetting("COMPANY_EMAIL", companyEmail);
            settingService.saveSetting("COMPANY_ADDRESS", companyAddress);

            if (companyLogoFile != null && !companyLogoFile.isEmpty()) {
                String logoPath = fileStorageService.storeFile(companyLogoFile, "logos");
                settingService.saveSetting("COMPANY_LOGO", logoPath);
            }

            settingService.saveSetting("VAT_RATE", vatRate.toPlainString());
            settingService.saveSetting("CURRENCY_SYMBOL", currencySymbol);
            settingService.saveSetting("MIN_STOCK_ALERT", minStockAlert);

            settingService.saveSetting("THEME_MODE", themeMode);
            settingService.saveSetting("SIDEBAR_COLLAPSED", "on".equals(sidebarCollapsed) ? "true" : "false");


            ra.addFlashAttribute("success", "¡Ajustes guardados exitosamente!");
            return "redirect:/dashboard";

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al guardar los ajustes: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/settings";
        }
    }
}