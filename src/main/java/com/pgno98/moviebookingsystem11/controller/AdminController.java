package com.pgno98.moviebookingsystem11.controller;

import com.pgno98.moviebookingsystem11.entity.Movie;
import com.pgno98.moviebookingsystem11.entity.Showtime;
import com.pgno98.moviebookingsystem11.entity.Theater;
import com.pgno98.moviebookingsystem11.service.MovieService;
import com.pgno98.moviebookingsystem11.service.ShowtimeService;
import com.pgno98.moviebookingsystem11.service.TheaterService;
import com.pgno98.moviebookingsystem11.service.BookingService;
import com.pgno98.moviebookingsystem11.service.UserService;
import com.pgno98.moviebookingsystem11.service.ReviewService;
import com.pgno98.moviebookingsystem11.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private MovieService movieService;
    
    @Autowired
    private TheaterService theaterService;
    
    @Autowired
    private ShowtimeService showtimeService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get real-time summary statistics
        List<Movie> allMovies = movieService.getAllMovies();
        List<Movie> activeMovies = movieService.getAllActiveMovies();
        Long upcomingShowtimes = showtimeService.countUpcomingShowtimes();
        
        // Get real booking count
        Long totalBookings = (long) bookingService.getAllBookings().size();
        
        // Get real user count (excluding admin users)
        List<com.pgno98.moviebookingsystem11.entity.User> allUsers = userService.getAllUsers();
        Long totalUsers = allUsers.stream()
            .filter(user -> user.getRole() == com.pgno98.moviebookingsystem11.entity.User.Role.USER)
            .count();
        
        // Get total theaters
        Long totalTheaters = (long) theaterService.getAllTheaters().size();
        
        // Get recent bookings (last 7 days)
        List<com.pgno98.moviebookingsystem11.entity.Booking> allBookings = bookingService.getAllBookings();
        Long recentBookings = allBookings.stream()
            .filter(booking -> booking.getBookingDate().isAfter(java.time.LocalDateTime.now().minusDays(7)))
            .count();
        
        // Get total revenue (sum of all booking amounts)
        java.math.BigDecimal totalRevenue = allBookings.stream()
            .map(com.pgno98.moviebookingsystem11.entity.Booking::getTotalAmount)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        model.addAttribute("totalMovies", allMovies.size());
        model.addAttribute("activeMovies", activeMovies.size());
        model.addAttribute("upcomingShowtimes", upcomingShowtimes);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalTheaters", totalTheaters);
        model.addAttribute("recentBookings", recentBookings);
        model.addAttribute("totalRevenue", totalRevenue);
        
        // Add movies and their analytics for the dashboard table
        model.addAttribute("movies", allMovies);
        
        // Calculate movie analytics
        Map<Long, Map<String, Object>> movieAnalytics = new HashMap<>();
        for (Movie movie : allMovies) {
            Map<String, Object> analytics = new HashMap<>();
            
            // Get booking count for this movie
            Long bookingCount = allBookings.stream()
                .filter(booking -> booking.getShowtime().getMovie().getId().equals(movie.getId()))
                .count();
            analytics.put("bookingCount", bookingCount);
            
            // Get total revenue for this movie
            BigDecimal movieRevenue = allBookings.stream()
                .filter(booking -> booking.getShowtime().getMovie().getId().equals(movie.getId()))
                .map(com.pgno98.moviebookingsystem11.entity.Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            analytics.put("totalRevenue", movieRevenue);
            
            // Get review count for this movie
            Long reviewCount = (long) reviewService.getReviewsByMovie(movie.getId()).size();
            analytics.put("reviewCount", reviewCount);
            
            movieAnalytics.put(movie.getId(), analytics);
        }
        model.addAttribute("movieAnalytics", movieAnalytics);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/movies")
    public String movieManagement(Model model) {
        List<Movie> movies = movieService.getAllMovies();
        Long upcomingShowtimes = showtimeService.countUpcomingShowtimes();
        
        // Get real booking count
        Long totalBookings = (long) bookingService.getAllBookings().size();
        
        // Calculate average ratings for each movie (same as customer view)
        java.util.Map<Long, Double> movieAverageRatings = new java.util.HashMap<>();
        for (Movie movie : movies) {
            double averageRating = reviewService.getAverageRatingForMovie(movie.getId());
            movieAverageRatings.put(movie.getId(), averageRating);
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", Movie.Genre.values());
        model.addAttribute("upcomingShowtimes", upcomingShowtimes);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("movieAverageRatings", movieAverageRatings);
        return "admin/movie-management";
    }
    
    @PostMapping("/movies")
    public String addMovie(@ModelAttribute Movie movie, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Movie title is required");
                return "redirect:/admin/movies";
            }
            
            // Set default values
            if (movie.getIsActive() == null) {
                movie.setIsActive(true);
            }
            
            movieService.saveMovie(movie);
            redirectAttributes.addFlashAttribute("success", "Movie added successfully!");
            
        } catch (Exception e) {
            System.err.println("Error adding movie: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add movie: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }
    
    @PostMapping("/movies/{id}/update")
    public String updateMovie(@PathVariable Long id, @ModelAttribute Movie movie, RedirectAttributes redirectAttributes) {
        try {
            // Validate that the movie exists
            Optional<Movie> existingMovie = movieService.findById(id);
            if (!existingMovie.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Movie not found with ID: " + id);
                return "redirect:/admin/movies";
            }
            
            // Validate required fields
            if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Movie title is required");
                return "redirect:/admin/movies/" + id + "/edit";
            }
            
            // Set the ID and preserve existing timestamps
            movie.setId(id);
            movie.setCreatedAt(existingMovie.get().getCreatedAt());
            movie.setUpdatedAt(java.time.LocalDateTime.now());
            
            // Save the updated movie
            movieService.saveMovie(movie);
            redirectAttributes.addFlashAttribute("success", "Movie updated successfully!");
            
        } catch (Exception e) {
            System.err.println("Error updating movie: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update movie: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }
    
    @GetMapping("/movies/{id}/edit")
    public String editMovie(@PathVariable Long id, Model model) {
        Movie movie = movieService.findById(id).orElse(null);
        if (movie == null) {
            return "redirect:/admin/movies";
        }
        
        // Calculate average rating from customer reviews (same as customer view)
        double averageRating = reviewService.getAverageRatingForMovie(id);
        
        model.addAttribute("movie", movie);
        model.addAttribute("genres", Movie.Genre.values());
        model.addAttribute("averageRating", averageRating);
        return "admin/edit-movie";
    }
    
    @PostMapping("/movies/{id}/delete")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            movieService.deleteMovie(id);
            redirectAttributes.addFlashAttribute("success", "Movie deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete movie: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/showtimes")
    public String showtimeManagement(Model model) {
        List<Showtime> showtimes = showtimeService.getAllShowtimes();
        List<Movie> movies = movieService.getAllActiveMovies();
        List<Theater> theaters = theaterService.getAllTheaters();
        
        model.addAttribute("showtimes", showtimes);
        model.addAttribute("movies", movies);
        model.addAttribute("theaters", theaters);
        model.addAttribute("showtime", new Showtime());
        
        return "admin/showtime-management";
    }
    
    @PostMapping("/showtimes")
    public String addShowtime(@ModelAttribute Showtime showtime, 
                             @RequestParam Long movieId,
                             @RequestParam Long theaterId,
                             @RequestParam BigDecimal ticketPrice,
                             @RequestParam Integer totalSeats,
                             RedirectAttributes redirectAttributes) {
        try {
            Movie movie = movieService.findById(movieId).orElse(null);
            Theater theater = theaterService.findById(theaterId).orElse(null);
            
            if (movie == null || theater == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid movie or theater selected");
                return "redirect:/admin/showtimes";
            }
            
            showtime.setMovie(movie);
            showtime.setTheater(theater);
            showtime.setTicketPrice(ticketPrice);
            showtime.setTotalSeats(totalSeats);
            showtime.setAvailableSeats(totalSeats);
            
            showtimeService.saveShowtime(showtime);
            redirectAttributes.addFlashAttribute("success", "Showtime added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add showtime: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
    
    @PostMapping("/showtimes/{id}/update")
    public String updateShowtime(@PathVariable Long id,
                                @RequestParam Long movieId,
                                @RequestParam Long theaterId,
                                @RequestParam Integer screenNumber,
                                @RequestParam LocalDateTime showDateTime,
                                @RequestParam BigDecimal ticketPrice,
                                @RequestParam Integer totalSeats,
                                @RequestParam Integer availableSeats,
                                @RequestParam(defaultValue = "false") boolean isActive,
                                RedirectAttributes redirectAttributes) {
        try {
            Showtime showtime = showtimeService.findById(id).orElse(null);
            if (showtime == null) {
                redirectAttributes.addFlashAttribute("error", "Showtime not found");
                return "redirect:/admin/showtimes";
            }
            
            // Get movie and theater
            Movie movie = movieService.findById(movieId).orElse(null);
            Theater theater = theaterService.findById(theaterId).orElse(null);
            
            if (movie == null || theater == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid movie or theater selected");
                return "redirect:/admin/showtimes/edit/" + id;
            }
            
            // Update the showtime with new data
            showtime.setMovie(movie);
            showtime.setTheater(theater);
            showtime.setScreenNumber(screenNumber);
            showtime.setShowDateTime(showDateTime);
            showtime.setTicketPrice(ticketPrice);
            showtime.setTotalSeats(totalSeats);
            showtime.setAvailableSeats(availableSeats);
            showtime.setIsActive(isActive);
            
            showtimeService.saveShowtime(showtime);
            redirectAttributes.addFlashAttribute("success", "Showtime updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update showtime: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/showtimes/{id}/edit")
    public String editShowtime(@PathVariable Long id, Model model) {
        Showtime showtime = showtimeService.findById(id).orElse(null);
        if (showtime == null) {
            return "redirect:/admin/showtimes";
        }
        
        List<Movie> movies = movieService.getAllActiveMovies();
        List<Theater> theaters = theaterService.getAllTheaters();
        
        model.addAttribute("showtime", showtime);
        model.addAttribute("movies", movies);
        model.addAttribute("theaters", theaters);
        return "admin/edit-showtime";
    }
    
    @PostMapping("/showtimes/{id}/delete")
    public String deleteShowtime(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Attempting to delete showtime with ID: " + id);
            
            Showtime showtime = showtimeService.findById(id).orElse(null);
            if (showtime == null) {
                System.out.println("Showtime not found with ID: " + id);
                redirectAttributes.addFlashAttribute("error", "Showtime not found");
                return "redirect:/admin/showtimes";
            }
            
            System.out.println("Found showtime: " + showtime.getMovie().getTitle() + " at " + showtime.getTheater().getName());
            
            // Check if showtime has any bookings
            List<com.pgno98.moviebookingsystem11.entity.Booking> showtimeBookings = bookingService.getBookingsByShowtime(id);
            System.out.println("Found " + showtimeBookings.size() + " bookings for this showtime");
            
            if (!showtimeBookings.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete showtime with existing bookings. Please contact system administrator.");
                return "redirect:/admin/showtimes";
            }
            
            System.out.println("Deleting showtime...");
            showtimeService.deleteShowtime(id);
            System.out.println("Showtime deleted successfully!");
            redirectAttributes.addFlashAttribute("success", "Showtime deleted successfully!");
        } catch (Exception e) {
            System.err.println("Error deleting showtime: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete showtime: " + e.getMessage());
        }
        return "redirect:/admin/showtimes";
    }
    
    @GetMapping("/reports")
    public String viewReports(Model model) {
        try {
            Map<String, Object> reportData = reportService.generateComprehensiveReport();
            model.addAllAttributes(reportData);
            return "admin/reports";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to generate report: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }
    
    @GetMapping("/movies/{id}/analytics")
    public String movieAnalytics(@PathVariable Long id, Model model) {
        try {
            // Get the movie
            Optional<Movie> movieOpt = movieService.findById(id);
            if (!movieOpt.isPresent()) {
                return "redirect:/admin/dashboard";
            }
            Movie movie = movieOpt.get();
            
            // Get all bookings for this movie
            List<com.pgno98.moviebookingsystem11.entity.Booking> movieBookings = bookingService.getAllBookings().stream()
                .filter(booking -> booking.getShowtime().getMovie().getId().equals(id))
                .collect(java.util.stream.Collectors.toList());
            
            // Get all reviews for this movie
            List<com.pgno98.moviebookingsystem11.entity.Review> movieReviews = reviewService.getReviewsByMovie(id);
            
            // Get all showtimes for this movie
            List<Showtime> movieShowtimes = showtimeService.getAllShowtimes().stream()
                .filter(showtime -> showtime.getMovie().getId().equals(id))
                .collect(java.util.stream.Collectors.toList());
            
            // Calculate analytics
            Long totalBookings = (long) movieBookings.size();
            BigDecimal totalRevenue = movieBookings.stream()
                .map(com.pgno98.moviebookingsystem11.entity.Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            Long totalSeatsBooked = movieBookings.stream()
                .mapToLong(booking -> booking.getSelectedSeats().split(",").length)
                .sum();
            
            Long totalReviews = (long) movieReviews.size();
            Double averageRating = movieReviews.stream()
                .mapToDouble(com.pgno98.moviebookingsystem11.entity.Review::getRating)
                .average()
                .orElse(0.0);
            
            Long approvedReviews = movieReviews.stream()
                .filter(com.pgno98.moviebookingsystem11.entity.Review::getIsApproved)
                .count();
            
            // Recent bookings (last 30 days)
            Long recentBookings = movieBookings.stream()
                .filter(booking -> booking.getBookingDate().isAfter(java.time.LocalDateTime.now().minusDays(30)))
                .count();
            
            // Recent revenue (last 30 days)
            BigDecimal recentRevenue = movieBookings.stream()
                .filter(booking -> booking.getBookingDate().isAfter(java.time.LocalDateTime.now().minusDays(30)))
                .map(com.pgno98.moviebookingsystem11.entity.Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Booking status distribution
            Map<com.pgno98.moviebookingsystem11.entity.Booking.Status, Long> bookingStatusDistribution = movieBookings.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    com.pgno98.moviebookingsystem11.entity.Booking::getStatus,
                    java.util.stream.Collectors.counting()
                ));
            
            // Theater performance for this movie
            Map<String, Long> theaterPerformance = movieBookings.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    booking -> booking.getShowtime().getTheater().getName(),
                    java.util.stream.Collectors.counting()
                ));
            
            // Add all data to model
            model.addAttribute("movie", movie);
            model.addAttribute("totalBookings", totalBookings);
            model.addAttribute("totalRevenue", totalRevenue);
            model.addAttribute("totalSeatsBooked", totalSeatsBooked);
            model.addAttribute("totalReviews", totalReviews);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("approvedReviews", approvedReviews);
            model.addAttribute("recentBookings", recentBookings);
            model.addAttribute("recentRevenue", recentRevenue);
            model.addAttribute("bookingStatusDistribution", bookingStatusDistribution);
            model.addAttribute("theaterPerformance", theaterPerformance);
            model.addAttribute("movieShowtimes", movieShowtimes);
            model.addAttribute("movieBookings", movieBookings);
            model.addAttribute("movieReviews", movieReviews);
            
            return "admin/movie-analytics";
            
        } catch (Exception e) {
            System.err.println("Error loading movie analytics: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/dashboard";
        }
    }
    
    // Booking Management Functions
    @PostMapping("/bookings/{id}/update")
    public String updateBooking(@PathVariable Long id, 
                               @RequestParam String status,
                               @RequestParam(required = false) String paymentMethod,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<com.pgno98.moviebookingsystem11.entity.Booking> bookingOpt = bookingService.findById(id);
            if (!bookingOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Booking not found");
                return "redirect:/admin/dashboard";
            }
            
            com.pgno98.moviebookingsystem11.entity.Booking booking = bookingOpt.get();
            
            // Update booking status
            try {
                booking.setStatus(com.pgno98.moviebookingsystem11.entity.Booking.Status.valueOf(status));
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("error", "Invalid booking status");
                return "redirect:/admin/movies/" + booking.getShowtime().getMovie().getId() + "/analytics";
            }
            
            // Update payment method if provided
            if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
                booking.setPaymentMethod(paymentMethod);
            }
            
            bookingService.saveBooking(booking);
            redirectAttributes.addFlashAttribute("success", "Booking updated successfully!");
            
        } catch (Exception e) {
            System.err.println("Error updating booking: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update booking: " + e.getMessage());
        }
        
        // Redirect back to the movie analytics page
        Optional<com.pgno98.moviebookingsystem11.entity.Booking> bookingOpt = bookingService.findById(id);
        if (bookingOpt.isPresent()) {
            return "redirect:/admin/movies/" + bookingOpt.get().getShowtime().getMovie().getId() + "/analytics";
        }
        return "redirect:/admin/dashboard";
    }
    
    @PostMapping("/bookings/{id}/delete")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<com.pgno98.moviebookingsystem11.entity.Booking> bookingOpt = bookingService.findById(id);
            if (!bookingOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Booking not found");
                return "redirect:/admin/dashboard";
            }
            
            Long movieId = bookingOpt.get().getShowtime().getMovie().getId();
            bookingService.deleteBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking deleted successfully!");
            return "redirect:/admin/movies/" + movieId + "/analytics";
            
        } catch (Exception e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete booking: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
    
    // Review Management Functions
    @PostMapping("/reviews/{id}/update")
    public String updateReview(@PathVariable Long id,
                              @RequestParam Integer rating,
                              @RequestParam String comment,
                              @RequestParam(defaultValue = "false") boolean isApproved,
                              RedirectAttributes redirectAttributes) {
        try {
            Optional<com.pgno98.moviebookingsystem11.entity.Review> reviewOpt = reviewService.findById(id);
            if (!reviewOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Review not found");
                return "redirect:/admin/dashboard";
            }
            
            com.pgno98.moviebookingsystem11.entity.Review review = reviewOpt.get();
            
            // Validate rating
            if (rating < 1 || rating > 5) {
                redirectAttributes.addFlashAttribute("error", "Rating must be between 1 and 5");
                return "redirect:/admin/movies/" + review.getMovie().getId() + "/analytics";
            }
            
            // Update review
            review.setRating(rating);
            review.setComment(comment);
            review.setIsApproved(isApproved);
            review.setUpdatedAt(java.time.LocalDateTime.now());
            
            reviewService.saveReview(review);
            redirectAttributes.addFlashAttribute("success", "Review updated successfully!");
            
        } catch (Exception e) {
            System.err.println("Error updating review: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update review: " + e.getMessage());
        }
        
        // Redirect back to the movie analytics page
        Optional<com.pgno98.moviebookingsystem11.entity.Review> reviewOpt = reviewService.findById(id);
        if (reviewOpt.isPresent()) {
            return "redirect:/admin/movies/" + reviewOpt.get().getMovie().getId() + "/analytics";
        }
        return "redirect:/admin/dashboard";
    }
    
    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<com.pgno98.moviebookingsystem11.entity.Review> reviewOpt = reviewService.findById(id);
            if (!reviewOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Review not found");
                return "redirect:/admin/dashboard";
            }
            
            Long movieId = reviewOpt.get().getMovie().getId();
            reviewService.deleteReview(id);
            redirectAttributes.addFlashAttribute("success", "Review deleted successfully!");
            return "redirect:/admin/movies/" + movieId + "/analytics";
            
        } catch (Exception e) {
            System.err.println("Error deleting review: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete review: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
    
    // Seat Management Functions
    @GetMapping("/bookings/{id}/edit-seats")
    public String editBookingSeats(@PathVariable Long id, Model model) {
        try {
            Optional<com.pgno98.moviebookingsystem11.entity.Booking> bookingOpt = bookingService.findById(id);
            if (!bookingOpt.isPresent()) {
                return "redirect:/admin/dashboard";
            }
            
            com.pgno98.moviebookingsystem11.entity.Booking booking = bookingOpt.get();
            model.addAttribute("booking", booking);
            
            return "admin/edit-seats";
            
        } catch (Exception e) {
            System.err.println("Error loading edit seats page: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/dashboard";
        }
    }
    
    @PostMapping("/bookings/{id}/update-seats")
    public String updateBookingSeats(@PathVariable Long id,
                                   @RequestParam String selectedSeats,
                                   RedirectAttributes redirectAttributes) {
        try {
            Optional<com.pgno98.moviebookingsystem11.entity.Booking> bookingOpt = bookingService.findById(id);
            if (!bookingOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Booking not found");
                return "redirect:/admin/dashboard";
            }
            
            com.pgno98.moviebookingsystem11.entity.Booking booking = bookingOpt.get();
            
            // Validate seat selection
            if (selectedSeats == null || selectedSeats.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select at least one seat");
                return "redirect:/admin/bookings/" + id + "/edit-seats";
            }
            
            // Update seat selection
            booking.setSelectedSeats(selectedSeats.trim());
            booking.setUpdatedAt(java.time.LocalDateTime.now());
            
            // Recalculate total amount based on new seat count
            int seatCount = selectedSeats.split(",").length;
            BigDecimal ticketPrice = booking.getShowtime().getTicketPrice();
            BigDecimal subtotal = ticketPrice.multiply(new BigDecimal(seatCount));
            
            // Apply discount if any
            BigDecimal discountAmount = booking.getDiscountAmount() != null ? booking.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal totalAmount = subtotal.subtract(discountAmount);
            
            booking.setTotalAmount(totalAmount);
            
            bookingService.saveBooking(booking);
            redirectAttributes.addFlashAttribute("success", "Seat selection updated successfully!");
            
        } catch (Exception e) {
            System.err.println("Error updating booking seats: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update seat selection: " + e.getMessage());
        }
        
        // Redirect back to the movie analytics page
        Optional<com.pgno98.moviebookingsystem11.entity.Booking> bookingOpt = bookingService.findById(id);
        if (bookingOpt.isPresent()) {
            return "redirect:/admin/movies/" + bookingOpt.get().getShowtime().getMovie().getId() + "/analytics";
        }
        return "redirect:/admin/dashboard";
    }
}
