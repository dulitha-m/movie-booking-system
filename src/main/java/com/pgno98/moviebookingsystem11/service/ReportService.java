package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.*;
import com.pgno98.moviebookingsystem11.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public Map<String, Object> generateComprehensiveReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Basic Statistics - All from actual database
        report.put("totalMovies", movieRepository.count());
        report.put("activeMovies", movieRepository.findByIsActiveTrue().size());
        report.put("totalBookings", bookingRepository.count());
        report.put("totalUsers", userRepository.count());
        report.put("totalTheaters", theaterRepository.count());
        report.put("totalReviews", reviewRepository.count());
        
        // Movie Genre Analysis - Real data from database
        List<Movie> allMovies = movieRepository.findAll();
        Map<Movie.Genre, Long> genreDistribution = allMovies.stream()
            .collect(Collectors.groupingBy(Movie::getGenre, Collectors.counting()));
        report.put("genreDistribution", genreDistribution);
        
        // Top Movies by Rating - Real data from database
        List<Movie> topRatedMovies = allMovies.stream()
            .filter(movie -> movie.getRating() > 0)
            .sorted((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()))
            .limit(5)
            .collect(Collectors.toList());
        report.put("topRatedMovies", topRatedMovies);
        
        // Revenue Analysis - Real data from database
        List<Booking> allBookings = bookingRepository.findAll();
        BigDecimal totalRevenue = allBookings.stream()
            .map(Booking::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.put("totalRevenue", totalRevenue);
        
        // Average Revenue per Booking - Real calculation
        BigDecimal avgRevenuePerBooking = allBookings.isEmpty() ? BigDecimal.ZERO : 
            totalRevenue.divide(BigDecimal.valueOf(allBookings.size()), 2, RoundingMode.HALF_UP);
        report.put("avgRevenuePerBooking", avgRevenuePerBooking);
        
        // Recent Bookings (Last 30 days) - Real data
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Booking> recentBookings = allBookings.stream()
            .filter(booking -> booking.getBookingDate().isAfter(thirtyDaysAgo))
            .collect(Collectors.toList());
        report.put("recentBookings", recentBookings.size());
        
        // Recent Revenue (Last 30 days) - Real calculation
        BigDecimal recentRevenue = recentBookings.stream()
            .map(Booking::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.put("recentRevenue", recentRevenue);
        
        // Most Popular Movies (by booking count) - Real data from database
        Map<Long, Long> movieBookingCounts = allBookings.stream()
            .collect(Collectors.groupingBy(
                booking -> booking.getShowtime().getMovie().getId(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> popularMovies = movieBookingCounts.entrySet().stream()
            .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
            .limit(5)
            .map(entry -> {
                Movie movie = movieRepository.findById(entry.getKey()).orElse(null);
                Map<String, Object> movieData = new HashMap<>();
                if (movie != null) {
                    movieData.put("movie", movie);
                    movieData.put("bookingCount", entry.getValue());
                }
                return movieData;
            })
            .filter(data -> data.get("movie") != null)
            .collect(Collectors.toList());
        report.put("popularMovies", popularMovies);
        
        // Theater Performance - Real data from database
        Map<Long, Long> theaterBookingCounts = allBookings.stream()
            .collect(Collectors.groupingBy(
                booking -> booking.getShowtime().getTheater().getId(),
                Collectors.counting()
            ));
        
        List<Map<String, Object>> theaterPerformance = theaterBookingCounts.entrySet().stream()
            .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
            .map(entry -> {
                Theater theater = theaterRepository.findById(entry.getKey()).orElse(null);
                Map<String, Object> theaterData = new HashMap<>();
                if (theater != null) {
                    theaterData.put("theater", theater);
                    theaterData.put("bookingCount", entry.getValue());
                }
                return theaterData;
            })
            .filter(data -> data.get("theater") != null)
            .collect(Collectors.toList());
        report.put("theaterPerformance", theaterPerformance);
        
        // Review Analysis - Real data from database
        List<Review> allReviews = reviewRepository.findAll();
        if (!allReviews.isEmpty()) {
            double averageRating = allReviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
            report.put("averageReviewRating", averageRating);
        } else {
            report.put("averageReviewRating", 0.0);
        }
        
        // Showtime Analysis - Real data from database
        List<Showtime> allShowtimes = showtimeRepository.findAll();
        report.put("totalShowtimes", allShowtimes.size());
        
        long activeShowtimes = allShowtimes.stream()
            .filter(showtime -> showtime.getIsActive() && showtime.getShowDateTime().isAfter(LocalDateTime.now()))
            .count();
        report.put("activeShowtimes", activeShowtimes);
        
        // Average ticket price - Real calculation from database
        if (!allShowtimes.isEmpty()) {
            BigDecimal avgTicketPrice = allShowtimes.stream()
                .map(Showtime::getTicketPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(allShowtimes.size()), 2, RoundingMode.HALF_UP);
            report.put("avgTicketPrice", avgTicketPrice);
        } else {
            report.put("avgTicketPrice", BigDecimal.ZERO);
        }
        
        // Report Generation Date
        report.put("reportGeneratedAt", LocalDateTime.now());
        
        return report;
    }
}
