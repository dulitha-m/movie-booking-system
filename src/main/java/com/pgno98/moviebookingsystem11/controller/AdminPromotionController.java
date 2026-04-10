package com.pgno98.moviebookingsystem11.controller;

import com.pgno98.moviebookingsystem11.entity.Promotion;
import com.pgno98.moviebookingsystem11.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/promotions")
public class AdminPromotionController {
    
    @Autowired
    private PromotionService promotionService;
    
    @GetMapping
    public String promotions(Model model) {
        List<Promotion> promotions = promotionService.getAllPromotions();
        model.addAttribute("promotions", promotions);
        return "admin/promotions";
    }
    
    @GetMapping("/add")
    public String addPromotion(Model model) {
        model.addAttribute("promotion", new Promotion());
        model.addAttribute("discountTypes", Promotion.DiscountType.values());
        return "admin/promotion-form";
    }
    
    @PostMapping("/add")
    public String savePromotion(@Valid @ModelAttribute("promotion") Promotion promotion, 
                               BindingResult bindingResult, 
                               Model model, 
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("discountTypes", Promotion.DiscountType.values());
            return "admin/promotion-form";
        }
        
        // Check if promotion code already exists
        if (promotionService.existsByCode(promotion.getCode())) {
            bindingResult.rejectValue("code", "error.promotion", "Promotion code already exists");
            model.addAttribute("discountTypes", Promotion.DiscountType.values());
            return "admin/promotion-form";
        }
        
        // Validate date range
        if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
            bindingResult.rejectValue("endDate", "error.promotion", "End date must be after start date");
            model.addAttribute("discountTypes", Promotion.DiscountType.values());
            return "admin/promotion-form";
        }
        
        try {
            promotionService.savePromotion(promotion);
            redirectAttributes.addFlashAttribute("success", "Promotion created successfully!");
            return "redirect:/admin/promotions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create promotion: " + e.getMessage());
            return "redirect:/admin/promotions/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editPromotion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Promotion promotion = promotionService.findById(id).orElse(null);
            if (promotion == null) {
                redirectAttributes.addFlashAttribute("error", "Promotion not found");
                return "redirect:/admin/promotions";
            }
            
            model.addAttribute("promotion", promotion);
            model.addAttribute("discountTypes", Promotion.DiscountType.values());
            return "admin/promotion-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to load promotion: " + e.getMessage());
            return "redirect:/admin/promotions";
        }
    }
    
    @PostMapping("/edit/{id}")
    public String updatePromotion(@PathVariable Long id, 
                                 @Valid @ModelAttribute("promotion") Promotion promotion, 
                                 BindingResult bindingResult, 
                                 Model model, 
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("discountTypes", Promotion.DiscountType.values());
            return "admin/promotion-form";
        }
        
        // Check if promotion code already exists (excluding current promotion)
        Optional<Promotion> existingPromotion = promotionService.findByCode(promotion.getCode());
        if (existingPromotion.isPresent() && !existingPromotion.get().getId().equals(id)) {
            bindingResult.rejectValue("code", "error.promotion", "Promotion code already exists");
            model.addAttribute("discountTypes", Promotion.DiscountType.values());
            return "admin/promotion-form";
        }
        
        // Validate date range
        if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
            bindingResult.rejectValue("endDate", "error.promotion", "End date must be after start date");
            model.addAttribute("discountTypes", Promotion.DiscountType.values());
            return "admin/promotion-form";
        }
        
        try {
            promotion.setId(id);
            promotionService.updatePromotion(promotion);
            redirectAttributes.addFlashAttribute("success", "Promotion updated successfully!");
            return "redirect:/admin/promotions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update promotion: " + e.getMessage());
            return "redirect:/admin/promotions/edit/" + id;
        }
    }
    
    @PostMapping("/delete/{id}")
    public String deletePromotion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            promotionService.deletePromotion(id);
            redirectAttributes.addFlashAttribute("success", "Promotion deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete promotion: " + e.getMessage());
        }
        return "redirect:/admin/promotions";
    }
    
    @PostMapping("/toggle-status/{id}")
    public String togglePromotionStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Promotion> promotionOpt = promotionService.findById(id);
            if (promotionOpt.isPresent()) {
                Promotion promotion = promotionOpt.get();
                promotion.setIsActive(!promotion.getIsActive());
                promotionService.updatePromotion(promotion);
                
                String status = promotion.getIsActive() ? "activated" : "deactivated";
                redirectAttributes.addFlashAttribute("success", "Promotion " + status + " successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Promotion not found");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update promotion status: " + e.getMessage());
        }
        return "redirect:/admin/promotions";
    }
}
