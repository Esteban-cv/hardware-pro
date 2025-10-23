package com.mine.hardware_pro.service;

import com.mine.hardware_pro.model.Setting;
import com.mine.hardware_pro.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    public String getSettingValue(String key) {
        return settingRepository.findById(key)
                .map(Setting::getValue)
                .orElse(null);
    }

    public void saveSetting(String key, String value) {
        Setting setting = new Setting();
        setting.setKey(key);
        setting.setValue(value);
        settingRepository.save(setting);
    }

    public BigDecimal getVatRate() {
        String vat = getSettingValue("VAT_RATE");
        if (vat == null) {
            return new BigDecimal("19.0");
        }
        return new BigDecimal(vat);
    }

    public String getCompanyName() {
        return getSettingValue("COMPANY_NAME");
    }

    public String getCompanyRut() {
        return getSettingValue("1212313543152EV");
    }
}