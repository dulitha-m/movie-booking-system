package com.pgno98.moviebookingsystem11.controller;

import com.pgno98.moviebookingsystem11.entity.Review;
import com.pgno98.moviebookingsystem11.entity.User;
import com.pgno98.moviebookingsystem11.service.ReviewService;
import com.pgno98.moviebookingsystem11.service.MovieService;
import com.pgno98.moviebookingsystem11.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private UserService userService;

    // View all reviews for a specific movie
    @GetMapping("/movie/{movieId}")
    public String movieReviews(@PathVariable Long movieId, Model model) {
        List<Review> reviews = reviewService.getReviewsByMovie(movieId);
        movieService.findById(movieId).ifPresent(movie -> {
            model.addAttribute("movie", movie);
            model.addAttribute("reviews", reviews);
        });
        return "review/movie-reviews";
    }

    // Show form to add a new review
    @GetMapping("/add/{movieId}")
    public String addReviewForm(@PathVariable Long movieId, Model model) {
        movieService.findById(movieId).ifPresent(movie -> {
            model.addAttribute("movie", movie);
            model.addAttribute("review", new Review());
        });
        return "review/add-review";
    }

    // Process adding a new review
    @PostMapping("/add/{movieId}")
    public String addReview(@PathVariable Long movieId,
                           @ModelAttribute Review review,
                           RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/movies/" + movieId;
            }

            // Check if user already reviewed this movie
            if (reviewService.findByUserAndMovie(user.getId(), movieId).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "You have already reviewed this movie");
                return "redirect:/movies/" + movieId;
            }

            movieService.findById(movieId).ifPresent(movie -> {
                review.setUser(user);
                review.setMovie(movie);
                reviewService.saveReview(review);
            });

            redirectAttributes.addFlashAttribute("success", "Review added successfully!");
            return "redirect:/movies/" + movieId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add review: " + e.getMessage());
            return "redirect:/movies/" + movieId;
        }
    }

    // Show form to edit an existing review
    @GetMapping("/edit/{id}")
    public String editReviewForm(@PathVariable Long id, Model model) {
        reviewService.findById(id).ifPresent(review -> {
            model.addAttribute("review", review);
            model.addAttribute("movie", review.getMovie());
        });
        return "review/edit-review";
    }

    // Process editing a review
    @PostMapping("/edit/{id}")
    public String editReview(@PathVariable Long id,
                            @ModelAttribute Review review,
                            RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/review/my-reviews";
            }

            reviewService.findById(id).ifPresent(existingReview -> {
                // Check if the review belongs to the current user
                if (!existingReview.getUser().getId().equals(user.getId())) {
                    redirectAttributes.addFlashAttribute("error", "You can only edit your own reviews");
                    return;
                }

                existingReview.setRating(review.getRating());
                existingReview.setComment(review.getComment());
                reviewService.saveReview(existingReview);
            });

            redirectAttributes.addFlashAttribute("success", "Review updated successfully!");
            return "redirect:/review/my-reviews";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update review: " + e.getMessage());
            return "redirect:/review/my-reviews";
        }
    }

    // Delete a review
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/review/my-reviews";
            }

            reviewService.findById(id).ifPresent(review -> {
                // Check if the review belongs to the current user
                if (!review.getUser().getId().equals(user.getId())) {
                    redirectAttributes.addFlashAttribute("error", "You can only delete your own reviews");
                    return;
                }

                reviewService.deleteReview(id);
            });

            redirectAttributes.addFlashAttribute("success", "Review deleted successfully!");
            return "redirect:/review/my-reviews";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete review: " + e.getMessage());
            return "redirect:/review/my-reviews";
        }
    }

    // View user's own reviews
    @GetMapping("/my-reviews")
    public String myReviews(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userService.findByEmail(email).orElse(null);
        if (user != null) {
            List<Review> reviews = reviewService.getReviewsByUser(user.getId());
            model.addAttribute("reviews", reviews);
        }

        return "review/my-reviews";
    }
}