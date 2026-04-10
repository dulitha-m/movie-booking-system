package com.pgno98.moviebookingsystem11.controller;

import com.pgno98.moviebookingsystem11.entity.Promotion;
import com.pgno98.moviebookingsystem11.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping("/promotions")
    public String viewPromotions(Model model) {
        try {
            System.out.println("Loading promotions for public page...");
            
            // Get all active promotions from the database
            List<Promotion> allPromotions = promotionService.getAllPromotions();
            System.out.println("Found " + (allPromotions != null ? allPromotions.size() : 0) + " total promotions");
            
            // Filter only active and valid promotions
            List<Promotion> activePromotions = allPromotions.stream()
                    .filter(Promotion::getIsActive)
                    .filter(Promotion::isValid)
                    .toList();
            
            System.out.println("Found " + activePromotions.size() + " active and valid promotions");
            
            // Add promotions to model
            model.addAttribute("allPromotions", activePromotions);
            
            // For now, use simple template to ensure it works
            // TODO: Fix the main promotions template
            return "promotions-simple";
            
        } catch (Exception e) {
            System.err.println("Error loading promotions: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to simple template if there's any error
            return "promotions-simple";
        }
    }
    
    @GetMapping("/promotions/debug")
    @ResponseBody
    public String debugPromotions(Model model) {
        try {
            List<Promotion> allPromotions = promotionService.getAllPromotions();
            StringBuilder result = new StringBuilder("Debug Info:\n");
            result.append("Total promotions: ").append(allPromotions.size()).append("\n");
            
            for (Promotion p : allPromotions) {
                result.append("- ").append(p.getCode()).append(": ").append(p.getName())
                      .append(" [Active: ").append(p.getIsActive()).append(", Valid: ").append(p.isValid()).append("]\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    @GetMapping("/promotions/test")
    @ResponseBody
    public String testPromotions() {
        try {
            List<Promotion> promotions = promotionService.getAllPromotions();
            if (promotions != null && !promotions.isEmpty()) {
                StringBuilder result = new StringBuilder("Found " + promotions.size() + " promotions:\n");
                for (Promotion p : promotions) {
                    result.append("- ").append(p.getCode()).append(": ").append(p.getName())
                          .append(" (").append(p.getDiscountValue()).append("% off)")
                          .append(" [Active: ").append(p.getIsActive()).append("]\n");
                }
                return result.toString();
            } else {
                return "No promotions found in database";
            }
        } catch (Exception e) {
            return "Error loading promotions: " + e.getMessage();
        }
    }
    
    @GetMapping("/promotions/db-test")
    @ResponseBody
    public String testDatabaseConnection() {
        try {
            // Simple database connectivity test
            List<Promotion> promotions = promotionService.getAllPromotions();
            return "Database connection successful! Found " + (promotions != null ? promotions.size() : 0) + " promotions.";
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage() + "\nStack trace: " + e.getStackTrace()[0];
        }
    }
}
