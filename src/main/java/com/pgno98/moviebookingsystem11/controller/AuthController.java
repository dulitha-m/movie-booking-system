package com.pgno98.moviebookingsystem11.controller;

import com.pgno98.moviebookingsystem11.entity.User;
import com.pgno98.moviebookingsystem11.service.UserService;
import com.pgno98.moviebookingsystem11.service.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private LogoutService logoutService;
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Validated User user, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Check for validation errors
            if (bindingResult.hasErrors()) {
                model.addAttribute("user", user);
                return "register";
            }
            
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Email already exists");
                return "register";
            }
            
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "profile";
    }
    
    @PostMapping("/profile")
    public String updateProfile(User user, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            User existingUser = userService.findByEmail(email).orElse(null);
            if (existingUser == null) {
                return "redirect:/login";
            }
            
            // Update only non-sensitive fields
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            
            userService.updateUser(existingUser);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Profile update failed: " + e.getMessage());
            return "redirect:/profile";
        }
    }
    
    @PostMapping("/profile/delete")
    public String deleteProfile(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found.");
                return "redirect:/profile";
            }
            
            System.out.println("Attempting to delete user: " + email);
            
            // Delete the user profile (this will cascade delete bookings and reviews)
            userService.deleteUserByEmail(email);
            
            System.out.println("User deleted successfully: " + email);
            
            // Properly logout the user using our custom service
            logoutService.performLogout(request, response);
            
            redirectAttributes.addFlashAttribute("success", "Your profile has been deleted successfully. We're sorry to see you go!");
            return "redirect:/home?deleted=true";
            
        } catch (Exception e) {
            System.err.println("Error deleting profile: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete profile: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}
