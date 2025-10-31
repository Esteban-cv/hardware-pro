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
        String companyName = settingService.getSettingValue("COMPANY_NAME");
        String logoPath = settingService.getSettingValue("COMPANY_LOGO");
        String sidebarCollapsed = settingService.getSettingValue("SIDEBAR_COLLAPSED");
        String currencySymbol = settingService.getSettingValue("CURRENCY_SYMBOL");


        model.addAttribute("globalCompanyName", (companyName != null && !companyName.isBlank()) ? companyName : "Tuerca Dorada");

        if (logoPath != null && !logoPath.isBlank()) {
            model.addAttribute("globalCompanyLogo", "/uploads/" + logoPath);
        } else {
            model.addAttribute("globalCompanyLogo", "/img/logo-rezisable.png");
        }

        model.addAttribute("globalSidebarCollapsed", (sidebarCollapsed != null) ? sidebarCollapsed : "false");

        model.addAttribute("globalCurrencySymbol", (currencySymbol != null && !currencySymbol.isBlank()) ? currencySymbol : "$");
    }
}