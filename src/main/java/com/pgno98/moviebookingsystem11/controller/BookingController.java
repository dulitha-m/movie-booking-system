package com.pgno98.moviebookingsystem11.controller;

import com.pgno98.moviebookingsystem11.entity.*;
import com.pgno98.moviebookingsystem11.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ShowtimeService showtimeService;


    @Autowired
    private SeatService seatService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private PromotionService promotionService;

    // Step 1: Select Showtime for a Movie
    @GetMapping("/select-showtime/{movieId}")
    public String selectShowtime(@PathVariable Long movieId, Model model) {
        Movie movie = movieService.findById(movieId).orElse(null);
        if (movie == null) {
            return "redirect:/movies";
        }

        List<Showtime> showtimes = showtimeService.getActiveShowtimesByMovie(movieId);
        model.addAttribute("movie", movie);
        model.addAttribute("showtimes", showtimes);
        return "booking/select-showtime";
    }

    // Step 2: Select Seats for a Showtime
    @GetMapping("/select-seats/{showtimeId}")
    public String selectSeats(@PathVariable Long showtimeId, Model model) {
        try {
            System.out.println("Selecting seats for showtime: " + showtimeId);
            
            Showtime showtime = showtimeService.findById(showtimeId).orElse(null);
            if (showtime == null) {
                System.out.println("Showtime not found: " + showtimeId);
                return "redirect:/movies";
            }

            System.out.println("Found showtime: " + showtime.getMovie().getTitle() + " at " + showtime.getTheater().getName());

            // Generate seat layout (theater layout similar to the image)
            List<Seat> seats = seatService.generateSeatLayout(showtimeId, showtime.getTheater().getId());
            System.out.println("Generated " + seats.size() + " seats");
            
            model.addAttribute("showtime", showtime);
            model.addAttribute("seats", seats);
            model.addAttribute("movie", showtime.getMovie());
            model.addAttribute("theater", showtime.getTheater());
            
            return "booking/select-seats";
        } catch (Exception e) {
            System.err.println("Error in selectSeats: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/movies";
        }
    }

    // Step 3: Checkout Page (POST - from seat selection)
    @PostMapping("/checkout")
    public String checkout(@RequestParam Long showtimeId, 
                          @RequestParam List<String> selectedSeats,
                          Model model) {
        Showtime showtime = showtimeService.findById(showtimeId).orElse(null);
        if (showtime == null) {
            return "redirect:/movies";
        }

        // Calculate total price
        BigDecimal seatPrice = showtime.getTicketPrice();
        BigDecimal totalPrice = seatPrice.multiply(BigDecimal.valueOf(selectedSeats.size()));

        model.addAttribute("showtime", showtime);
        model.addAttribute("movie", showtime.getMovie());
        model.addAttribute("theater", showtime.getTheater());
        model.addAttribute("selectedSeats", selectedSeats);
        model.addAttribute("seatPrice", seatPrice);
        model.addAttribute("totalPrice", totalPrice);
        
        return "booking/checkout";
    }

    // Step 3: Checkout Page (GET - for error handling)
    @GetMapping("/checkout")
    public String checkoutWithError(@RequestParam Long showtimeId, 
                                   @RequestParam List<String> selectedSeats,
                                   Model model) {
        Showtime showtime = showtimeService.findById(showtimeId).orElse(null);
        if (showtime == null) {
            return "redirect:/movies";
        }

        // Calculate total price
        BigDecimal seatPrice = showtime.getTicketPrice();
        BigDecimal totalPrice = seatPrice.multiply(BigDecimal.valueOf(selectedSeats.size()));

        model.addAttribute("showtime", showtime);
        model.addAttribute("movie", showtime.getMovie());
        model.addAttribute("theater", showtime.getTheater());
        model.addAttribute("selectedSeats", selectedSeats);
        model.addAttribute("seatPrice", seatPrice);
        model.addAttribute("totalPrice", totalPrice);
        
        return "booking/checkout";
    }

    // Step 4: Process Booking
    @PostMapping("/process-booking")
    public String processBooking(@RequestParam Long showtimeId,
                                @RequestParam List<String> selectedSeats,
                                @RequestParam String paymentMethod,
                                @RequestParam(required = false) String promotionCode,
                                @RequestParam String termsAccepted,
                                RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found. Please login again.");
                return "redirect:/login";
            }

            // Validate terms and conditions acceptance
            if (!"true".equals(termsAccepted)) {
                redirectAttributes.addFlashAttribute("error", "You must accept the terms and conditions to proceed with the booking.");
                // Redirect back to seat selection since checkout is a POST mapping
                return "redirect:/booking/select-seats/" + showtimeId;
            }

            Showtime showtime = showtimeService.findById(showtimeId).orElse(null);
            if (showtime == null) {
                redirectAttributes.addFlashAttribute("error", "Showtime not found.");
                return "redirect:/movies";
            }

            // Calculate base total amount
            BigDecimal baseAmount = showtime.getTicketPrice().multiply(BigDecimal.valueOf(selectedSeats.size()));
            BigDecimal discountAmount = BigDecimal.ZERO;
            BigDecimal finalAmount = baseAmount;

            // Apply promotion code if provided
            if (promotionCode != null && !promotionCode.trim().isEmpty()) {
                BigDecimal calculatedDiscount = promotionService.calculateDiscount(promotionCode.trim().toUpperCase(), baseAmount);
                if (calculatedDiscount.compareTo(BigDecimal.ZERO) > 0) {
                    discountAmount = calculatedDiscount;
                    finalAmount = baseAmount.subtract(discountAmount);
                    
                    // Increment usage count for the promotion
                    promotionService.incrementUsageCount(promotionCode.trim().toUpperCase());
                }
            }

            // Create booking
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setShowtime(showtime);
            booking.setBookingDate(LocalDateTime.now());
            booking.setStatus(Booking.Status.CONFIRMED);
            booking.setBookingReference(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            booking.setTotalAmount(finalAmount);
            booking.setPromotionCode(promotionCode != null && !promotionCode.trim().isEmpty() ? promotionCode.trim().toUpperCase() : null);
            booking.setDiscountAmount(discountAmount);
            
            // Store selected seats as comma-separated string
            booking.setSelectedSeats(String.join(",", selectedSeats));

            // Create seats and booking
            for (String seatNumber : selectedSeats) {
                Seat seat = seatService.findByShowtimeAndSeatNumber(showtimeId, seatNumber);
                if (seat != null) {
                    seat.setStatus(Seat.Status.OCCUPIED);
                    seatService.saveSeat(seat);
                }
            }

            bookingService.saveBooking(booking);

            // Update showtime available seats
            showtime.setAvailableSeats(showtime.getAvailableSeats() - selectedSeats.size());
            showtimeService.saveShowtime(showtime);

            // Redirect to confirmation page with booking details
            redirectAttributes.addFlashAttribute("booking", booking);
            redirectAttributes.addFlashAttribute("movie", showtime.getMovie());
            redirectAttributes.addFlashAttribute("theater", showtime.getTheater());
            redirectAttributes.addFlashAttribute("showtime", showtime);
            redirectAttributes.addFlashAttribute("selectedSeats", selectedSeats);
            redirectAttributes.addFlashAttribute("seatPrice", showtime.getTicketPrice());
            redirectAttributes.addFlashAttribute("baseAmount", baseAmount);
            redirectAttributes.addFlashAttribute("discountAmount", discountAmount);
            redirectAttributes.addFlashAttribute("totalPrice", finalAmount);
            redirectAttributes.addFlashAttribute("paymentMethod", paymentMethod);
            redirectAttributes.addFlashAttribute("promotionCode", promotionCode);
            
            return "redirect:/booking/confirmation/" + booking.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Booking failed: " + e.getMessage());
            // Redirect back to movies page since we don't have the movieId here
            return "redirect:/movies";
        }
    }

    // View Booking Details
    @GetMapping("/details/{bookingId}")
    public String viewBookingDetails(@PathVariable Long bookingId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User currentUser = userService.findByEmail(email).orElse(null);
            
            if (currentUser == null) {
                return "redirect:/login";
            }

            Booking booking = bookingService.findById(bookingId).orElse(null);
            if (booking == null || !booking.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/booking/my-bookings";
            }

            model.addAttribute("booking", booking);
            model.addAttribute("movie", booking.getShowtime().getMovie());
            model.addAttribute("theater", booking.getShowtime().getTheater());
            model.addAttribute("showtime", booking.getShowtime());
            
            return "booking/booking-details";
            
        } catch (Exception e) {
            return "redirect:/booking/my-bookings";
        }
    }

    // Booking Confirmation Page
    @GetMapping("/confirmation/{bookingId}")
    public String bookingConfirmation(@PathVariable Long bookingId, Model model) {
        try {
            Booking booking = bookingService.findById(bookingId).orElse(null);
            if (booking == null) {
                return "redirect:/home";
            }

            // Check if user owns this booking
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User currentUser = userService.findByEmail(email).orElse(null);
            
            if (currentUser == null || !booking.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/home";
            }

            model.addAttribute("booking", booking);
            model.addAttribute("movie", booking.getShowtime().getMovie());
            model.addAttribute("theater", booking.getShowtime().getTheater());
            model.addAttribute("showtime", booking.getShowtime());
            
            // Get selected seats from the booking
            List<String> selectedSeats = Arrays.asList(booking.getSelectedSeats().split(","));
            
            model.addAttribute("selectedSeats", selectedSeats);
            model.addAttribute("seatPrice", booking.getShowtime().getTicketPrice());
            model.addAttribute("totalPrice", booking.getTotalAmount());
            model.addAttribute("paymentMethod", "Credit Card"); // Default payment method
            
            return "booking/booking-confirmation";
            
        } catch (Exception e) {
            return "redirect:/home";
        }
    }

    // My Bookings Page
    @GetMapping("/my-bookings")
    public String myBookings(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User currentUser = userService.findByEmail(email).orElse(null);
            
            if (currentUser == null) {
                return "redirect:/login";
            }

            List<Booking> userBookings = bookingService.getBookingsByUser(currentUser.getId());
            model.addAttribute("bookings", userBookings);
            model.addAttribute("user", currentUser);
            
            return "booking/my-bookings";
            
        } catch (Exception e) {
            return "redirect:/home";
        }
    }

    // Edit Booking Page
    @GetMapping("/edit/{bookingId}")
    public String editBooking(@PathVariable Long bookingId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User currentUser = userService.findByEmail(email).orElse(null);
            
            if (currentUser == null) {
                return "redirect:/login";
            }

            Booking booking = bookingService.findById(bookingId).orElse(null);
            if (booking == null || !booking.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/booking/my-bookings";
            }

            // Get available showtimes for the same movie
            List<Showtime> availableShowtimes = showtimeService.getActiveShowtimesByMovie(booking.getShowtime().getMovie().getId());
            
            model.addAttribute("booking", booking);
            model.addAttribute("movie", booking.getShowtime().getMovie());
            model.addAttribute("theater", booking.getShowtime().getTheater());
            model.addAttribute("currentShowtime", booking.getShowtime());
            model.addAttribute("availableShowtimes", availableShowtimes);
            
            return "booking/edit-booking";
            
        } catch (Exception e) {
            return "redirect:/booking/my-bookings";
        }
    }

    // Update Booking
    @PostMapping("/update/{bookingId}")
    public String updateBooking(@PathVariable Long bookingId,
                               @RequestParam Long newShowtimeId,
                               RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User currentUser = userService.findByEmail(email).orElse(null);
            
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found. Please login again.");
                return "redirect:/login";
            }

            Booking booking = bookingService.findById(bookingId).orElse(null);
            if (booking == null || !booking.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "Booking not found or access denied.");
                return "redirect:/booking/my-bookings";
            }

            Showtime newShowtime = showtimeService.findById(newShowtimeId).orElse(null);
            if (newShowtime == null) {
                redirectAttributes.addFlashAttribute("error", "Selected showtime not found.");
                return "redirect:/booking/edit/" + bookingId;
            }

            // Calculate the number of seats from the existing booking's selected seats
            String[] seatArray = booking.getSelectedSeats().split(",");
            int numberOfSeats = seatArray.length;
            
            // Update booking with new showtime
            booking.setShowtime(newShowtime);
            
            // Recalculate total amount with the new showtime's ticket price
            booking.setTotalAmount(newShowtime.getTicketPrice().multiply(BigDecimal.valueOf(numberOfSeats)));
            
            bookingService.saveBooking(booking);
            
            redirectAttributes.addFlashAttribute("success", "Booking updated successfully!");
            return "redirect:/booking/my-bookings";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update booking: " + e.getMessage());
            return "redirect:/booking/edit/" + bookingId;
        }
    }

    // Cancel Booking
    @PostMapping("/cancel/{bookingId}")
    public String cancelBooking(@PathVariable Long bookingId,
                               RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            User currentUser = userService.findByEmail(email).orElse(null);
            
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "User not found. Please login again.");
                return "redirect:/login";
            }

            Booking booking = bookingService.findById(bookingId).orElse(null);
            if (booking == null || !booking.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "Booking not found or access denied.");
                return "redirect:/booking/my-bookings";
            }

            // Cancel the booking
            booking.setStatus(Booking.Status.CANCELLED);
            bookingService.saveBooking(booking);

            // Free up the seats
            String[] seatNumbers = booking.getSelectedSeats().split(",");
            int freedSeats = 0;
            
            for (String seatNumber : seatNumbers) {
                Seat seat = seatService.findByShowtimeAndSeatNumber(booking.getShowtime().getId(), seatNumber.trim());
                if (seat != null && seat.getStatus() == Seat.Status.OCCUPIED) {
                    seat.setStatus(Seat.Status.AVAILABLE);
                    seatService.saveSeat(seat);
                    freedSeats++;
                }
            }

            // Update showtime available seats
            Showtime showtime = booking.getShowtime();
            showtime.setAvailableSeats(showtime.getAvailableSeats() + freedSeats);
            showtimeService.saveShowtime(showtime);
            
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully!");
            return "redirect:/booking/my-bookings";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to cancel booking: " + e.getMessage());
            return "redirect:/booking/my-bookings";
        }
    }
}
