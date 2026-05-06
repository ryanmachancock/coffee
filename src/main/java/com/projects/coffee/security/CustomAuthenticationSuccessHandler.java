package com.projects.coffee.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        HttpSession session = request.getSession();
        System.out.println("=== Authentication Success ===");
        System.out.println("=== User: " + authentication.getName() + " ===");
        System.out.println("=== Session ID: " + session.getId() + " ===");
        System.out.println("=== Session Created: " + session.getCreationTime() + " ===");
        System.out.println("=== Session Max Inactive: " + session.getMaxInactiveInterval() + " ===");

        response.sendRedirect("/dashboard.html");
    }
}
