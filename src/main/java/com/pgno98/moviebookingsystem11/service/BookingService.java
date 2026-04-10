package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.Booking;
import com.pgno98.moviebookingsystem11.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdOrderByBookingDateDesc(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public List<Booking> getBookingsByShowtime(Long showtimeId) {
        return bookingRepository.findByShowtimeId(showtimeId);
    }
    
    @Transactional
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
