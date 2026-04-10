package com.pgno98.moviebookingsystem11.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    public void performLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            // Use Spring Security's logout handler
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.setInvalidateHttpSession(true);
            logoutHandler.setClearAuthentication(true);
            logoutHandler.logout(request, response, auth);
        }
        
        // Additional cleanup
        SecurityContextHolder.clearContext();
        
        // Invalidate session
        if (request.getSession() != null) {
            request.getSession().invalidate();
        }
    }
}
