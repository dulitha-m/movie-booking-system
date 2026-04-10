package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.Promotion;
import com.pgno98.moviebookingsystem11.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    @Transactional
    public Promotion savePromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }
    
    @Transactional
    public Promotion updatePromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }
    
    @Transactional
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }
    
    public Optional<Promotion> findById(Long id) {
        return promotionRepository.findById(id);
    }
    
    public Optional<Promotion> findByCode(String code) {
        return promotionRepository.findByCode(code);
    }
    
    public Optional<Promotion> findValidPromotionByCode(String code) {
        return promotionRepository.findValidPromotionByCode(code, LocalDate.now());
    }
    
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }
    
    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }
    
    public List<Promotion> getValidPromotions() {
        return promotionRepository.findAllValidPromotions(LocalDate.now());
    }
    
    public boolean existsByCode(String code) {
        return promotionRepository.existsByCode(code);
    }
    
    @Transactional
    public void incrementUsageCount(String code) {
        Optional<Promotion> promotionOpt = promotionRepository.findByCode(code);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();
            promotion.setUsedCount(promotion.getUsedCount() + 1);
            promotionRepository.save(promotion);
        }
    }
    
    public BigDecimal calculateDiscount(String code, BigDecimal orderAmount) {
        Optional<Promotion> promotionOpt = findValidPromotionByCode(code);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();
            return promotion.calculateDiscount(orderAmount);
        }
        return BigDecimal.ZERO;
    }
    
    public boolean isValidPromotionCode(String code, BigDecimal orderAmount) {
        Optional<Promotion> promotionOpt = findValidPromotionByCode(code);
        if (promotionOpt.isPresent()) {
            Promotion promotion = promotionOpt.get();
            return promotion.canBeUsed(orderAmount);
        }
        return false;
    }
    
    public List<Promotion> getPromotionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return promotionRepository.findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByCreatedAtDesc(
                startDate, endDate);
    }
}
