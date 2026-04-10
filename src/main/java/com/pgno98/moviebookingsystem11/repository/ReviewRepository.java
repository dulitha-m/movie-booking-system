package com.pgno98.moviebookingsystem11.repository;

import com.pgno98.moviebookingsystem11.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByMovieIdAndIsApprovedTrue(Long movieId);
    
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<Review> findByUserIdAndMovieId(Long userId, Long movieId);
    
    List<Review> findByIsApprovedFalse();
    
    List<Review> findByMovieId(Long movieId);
}