package com.projects.coffee.security;

import com.projects.coffee.repository.LoginRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class DatabaseValidatingSecurityContextRepository implements SecurityContextRepository {

    private final LoginRepository loginRepository;
    private final HttpSessionSecurityContextRepository delegate;

    public DatabaseValidatingSecurityContextRepository(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.delegate = new HttpSessionSecurityContextRepository();
    }

    @Override
    @SuppressWarnings("deprecation")
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        SecurityContext context = delegate.loadContext(requestResponseHolder);
        return validate(context, request);
    }

    private SecurityContext validate(SecurityContext context, HttpServletRequest request) {
        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            String username = context.getAuthentication().getName();
            System.out.println("=== Validating session for user: " + username + " ===");

            if (loginRepository.findByUsername(username) == null) {
                System.out.println("=== User not found in database, invalidating session ===");
                SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                return emptyContext;
            } else {
                System.out.println("=== User found in database, session valid ===");
            }
        }

        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        delegate.saveContext(context, request, response);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return delegate.containsContext(request);
    }
}
