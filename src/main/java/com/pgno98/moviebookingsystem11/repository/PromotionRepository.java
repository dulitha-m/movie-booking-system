package com.pgno98.moviebookingsystem11.repository;

import com.pgno98.moviebookingsystem11.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    
    Optional<Promotion> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Promotion> findByIsActiveTrueOrderByCreatedAtDesc();
    
    List<Promotion> findByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByCreatedAtDesc(
            LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND p.code = :code AND " +
           ":currentDate BETWEEN p.startDate AND p.endDate AND " +
           "(p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    Optional<Promotion> findValidPromotionByCode(@Param("code") String code, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT p FROM Promotion p WHERE p.isActive = true AND " +
           ":currentDate BETWEEN p.startDate AND p.endDate AND " +
           "(p.usageLimit IS NULL OR p.usedCount < p.usageLimit)")
    List<Promotion> findAllValidPromotions(@Param("currentDate") LocalDate currentDate);
}