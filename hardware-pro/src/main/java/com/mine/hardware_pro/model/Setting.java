package com.mine.hardware_pro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "app_settings")
public class Setting {

    @Id
    @Column(name = "setting_key", length = 50)
    private String key;

    @Column(name = "setting_value", length = 255)
    private String value;
}