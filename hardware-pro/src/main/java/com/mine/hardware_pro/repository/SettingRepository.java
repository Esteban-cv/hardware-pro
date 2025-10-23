package com.mine.hardware_pro.repository;

import com.mine.hardware_pro.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, String> {
}
