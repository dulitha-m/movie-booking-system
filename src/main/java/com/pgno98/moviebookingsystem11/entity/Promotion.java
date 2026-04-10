package com.pgno98.moviebookingsystem11.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "promo_code", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Promotion code is required")
    @Size(min = 3, max = 20, message = "Promotion code must be between 3 and 20 characters")
    private String code;
    
    @Column(name = "name", nullable = false)
    @NotBlank(message = "Promotion name is required")
    @Size(max = 100, message = "Promotion name cannot exceed 100 characters")
    private String name;
    
    @Column(name = "description", length = 500)
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.01", message = "Discount value must be greater than 0")
    private BigDecimal discountValue;
    
    @Column(name = "minimum_amount", precision = 10, scale = 2)
    private BigDecimal minimumAmount;
    
    @Column(name = "maximum_discount", precision = 10, scale = 2)
    private BigDecimal maximumDiscount;
    
    @Column(name = "usage_limit")
    private Integer usageLimit;
    
    @Column(name = "used_count")
    private Integer usedCount = 0;
    
    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum DiscountType {
        PERCENTAGE("Percentage"),
        FIXED_AMOUNT("Fixed Amount");
        
        private final String displayName;
        
        DiscountType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Helper methods
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return isActive && 
               today.isAfter(startDate.minusDays(1)) && 
               today.isBefore(endDate.plusDays(1)) &&
               (usageLimit == null || usedCount < usageLimit);
    }
    
    public boolean canBeUsed(BigDecimal orderAmount) {
        if (!isValid()) {
            return false;
        }
        
        if (minimumAmount != null && orderAmount.compareTo(minimumAmount) < 0) {
            return false;
        }
        
        return true;
    }
    
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!canBeUsed(orderAmount)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = BigDecimal.ZERO;
        
        if (discountType == DiscountType.PERCENTAGE) {
            discount = orderAmount.multiply(discountValue.divide(new BigDecimal("100")));
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            discount = discountValue;
        }
        
        // Apply maximum discount limit if set
        if (maximumDiscount != null && discount.compareTo(maximumDiscount) > 0) {
            discount = maximumDiscount;
        }
        
        // Ensure discount doesn't exceed order amount
        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }
        
        return discount.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}