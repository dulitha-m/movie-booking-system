package com.pgno98.moviebookingsystem11.controller;

import com.pgno98.moviebookingsystem11.entity.Promotion;
import com.pgno98.moviebookingsystem11.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
public class PromotionRestController {
    
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "API is working");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listPromotions() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Promotion> promotions = promotionService.getAllPromotions();
            response.put("status", "success");
            response.put("count", promotions.size());
            response.put("promotions", promotions.stream().map(p -> {
                Map<String, Object> promo = new HashMap<>();
                promo.put("code", p.getCode());
                promo.put("name", p.getName());
                promo.put("isActive", p.getIsActive());
                promo.put("startDate", p.getStartDate());
                promo.put("endDate", p.getEndDate());
                promo.put("minimumAmount", p.getMinimumAmount());
                promo.put("discountValue", p.getDiscountValue());
                promo.put("discountType", p.getDiscountType());
                promo.put("maximumDiscount", p.getMaximumDiscount());
                return promo;
            }).collect(java.util.stream.Collectors.toList()));
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePromotionCode(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Received promotion validation request: " + request);
            
            String code = (String) request.get("code");
            BigDecimal orderAmount = new BigDecimal(request.get("orderAmount").toString());
            
            System.out.println("Code: " + code + ", Order Amount: " + orderAmount);
            
            if (code == null || code.trim().isEmpty()) {
                response.put("valid", false);
                response.put("message", "Promotion code is required");
                return ResponseEntity.ok(response);
            }
            
            // Check if promotion code is valid
            boolean isValid = promotionService.isValidPromotionCode(code.trim().toUpperCase(), orderAmount);
            System.out.println("Is valid: " + isValid);
            
            if (isValid) {
                // Calculate discount amount
                BigDecimal discountAmount = promotionService.calculateDiscount(code.trim().toUpperCase(), orderAmount);
                System.out.println("Discount amount: " + discountAmount);
                
                response.put("valid", true);
                response.put("discountAmount", discountAmount.doubleValue());
                response.put("message", "Promotion code applied successfully");
            } else {
                response.put("valid", false);
                response.put("message", "Invalid promotion code or does not meet minimum requirements");
            }
            
            System.out.println("Response: " + response);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error in promotion validation: " + e.getMessage());
            e.printStackTrace();
            response.put("valid", false);
            response.put("message", "Error validating promotion code: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
