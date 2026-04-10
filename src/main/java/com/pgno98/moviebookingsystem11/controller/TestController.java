package com.pgno98.moviebookingsystem11.controller;

import com.pgno98.moviebookingsystem11.entity.Promotion;
import com.pgno98.moviebookingsystem11.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping("/promotions")
    public List<Promotion> testPromotions() {
        List<Promotion> promotions = promotionService.getAllPromotions();
        System.out.println("Total promotions found: " + promotions.size());
        return promotions;
    }
    
    @GetMapping("/promotions/simple")
    public String testSimplePromotions(Model model) {
        try {
            List<Promotion> promotions = promotionService.getAllPromotions();
            model.addAttribute("promotions", promotions);
            return "Simple test page with " + promotions.size() + " promotions";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
