package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.Review;
import com.pgno98.moviebookingsystem11.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByMovie(Long movieId) {
        return reviewRepository.findByMovieIdAndIsApprovedTrue(movieId);
    }

    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<Review> findByUserAndMovie(Long userId, Long movieId) {
        return reviewRepository.findByUserIdAndMovieId(userId, movieId);
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getPendingReviews() {
        return reviewRepository.findByIsApprovedFalse();
    }

    @Transactional
    public void approveReview(Long id) {
        reviewRepository.findById(id).ifPresent(review -> {
            review.setIsApproved(true);
            reviewRepository.save(review);
        });
    }

    public double getAverageRatingForMovie(Long movieId) {
        List<Review> reviews = reviewRepository.findByMovieIdAndIsApprovedTrue(movieId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = reviews.stream().mapToDouble(Review::getRating).sum();
        return sum / reviews.size();
    }
}