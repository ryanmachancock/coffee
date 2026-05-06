package com.projects.coffee.controller;

import com.projects.coffee.entity.Login;
import com.projects.coffee.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AuthService authService;

    public ApiController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> apiLogin(@RequestBody Login loginRequest) {
        System.out.println("API Login called with username: " + loginRequest.getUsername());
        String token = authService.authenticateUser(loginRequest);
        if ("Invalid credentials".equals(token)) {
            System.out.println("Authentication failed for user: " + loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Collections.singletonMap("error", "Invalid credentials")
            );
        }

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Login successful");
        response.put("tokenType", "Bearer");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("username", authentication.getName());
            profile.put("authorities", authentication.getAuthorities());
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Collections.singletonMap("error", "User not authenticated")
        );
    }
}
