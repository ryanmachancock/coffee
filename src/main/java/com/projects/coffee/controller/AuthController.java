package com.projects.coffee.controller;

import com.projects.coffee.dto.RegistrationDTO;
import com.projects.coffee.entity.Login;
import com.projects.coffee.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@RequestMapping("/auth")
@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "redirect:/register.html";
    }

    @PostMapping("/register")
    public String submitRegisterForm(@ModelAttribute RegistrationDTO registrationDTO) {
        if (authService.isRegisterSuccessful(registrationDTO)) {
            return "redirect:/register_success.html";
        }
        return "redirect:/register.html?error=registrationFailed";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "redirect:/login.html";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody Login loginRequest) {
        String token = authService.authenticateUser(loginRequest);
        if ("Invalid credentials".equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}
