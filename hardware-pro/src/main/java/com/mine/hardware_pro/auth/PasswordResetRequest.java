package com.mine.hardware_pro.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
