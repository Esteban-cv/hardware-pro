package com.mine.hardware_pro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {
    @GetMapping("/login")
    public String showLoginForm() {
        return "auth_login"; // templates/auth_login.html
    }
}
