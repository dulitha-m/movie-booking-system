package com.pgno98.moviebookingsystem11.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Public pages - no authentication required
                .requestMatchers("/", "/home", "/movies", "/movies/*", "/register", "/login", "/css/**", "/js/**", "/images/**", "/h2-console/**", "/api/promotions/validate", "/promotions", "/test/**").permitAll()
                // Allow showtime browsing without authentication
                .requestMatchers("/booking/select-showtime/**").permitAll()
                    // Allow seat selection without authentication (customers can browse seats)
                    .requestMatchers("/booking/select-seats/**").permitAll()
                    // Allow viewing reviews without authentication
                    .requestMatchers("/review/movie/**").permitAll()
                    // Allow booking confirmation page (requires authentication)
                    .requestMatchers("/booking/confirmation/**").hasAnyRole("USER", "ADMIN")
                // Admin pages - require ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                    // User-specific pages - require USER or ADMIN role
                    .requestMatchers("/user/**", "/profile").hasAnyRole("USER", "ADMIN")
                    // Booking management pages - require authentication (USER or ADMIN)
                    .requestMatchers("/booking/my-bookings", "/booking/edit/**", "/booking/update/**", "/booking/cancel/**").hasAnyRole("USER", "ADMIN")
                    // Booking checkout and processing - require authentication (USER or ADMIN)
                    .requestMatchers("/booking/checkout", "/booking/process-booking/**").hasAnyRole("USER", "ADMIN")
                // Review management pages - require authentication (USER or ADMIN)
                .requestMatchers("/review/add/**", "/review/edit/**", "/review/delete/**", "/review/my-reviews").hasAnyRole("USER", "ADMIN")
                // Allow all other requests
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
            
        return http.build();
    }
}
