package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.Seat;
import com.pgno98.moviebookingsystem11.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> generateSeatLayout(Long showtimeId, Long theaterId) {
        List<Seat> seats = new ArrayList<>();
        
        System.out.println("Generating seat layout for showtimeId: " + showtimeId + ", theaterId: " + theaterId);
        
        // Generate theater layout exactly matching the image
        
        // DIR'S LOUNGE Section
        // Left block: DL11, DL10, DL9, DL8, DL7, DL6, DL5 (7 seats)
        generateSeatRow(seats, showtimeId, theaterId, "DL", 11, 5); // Reverse order DL11-DL5
        
        // Right block: DL4, DL3, DL2, DL1 (4 seats)  
        generateSeatRow(seats, showtimeId, theaterId, "DL", 4, 1); // Reverse order DL4-DL1
        
        // STANDARD Section - Upper Rows (I, H, G)
        // Left block (11 seats each): I17-I7, H17-H7, G17-G7
        // Right block (6 seats each): I6-I1, H6-H1, G6-G1
        String[] upperRows = {"I", "H", "G"};
        for (String row : upperRows) {
            generateSeatRow(seats, showtimeId, theaterId, row, 17, 7);  // Left block (11 seats)
            generateSeatRow(seats, showtimeId, theaterId, row, 6, 1);   // Right block (6 seats)
        }
        
        // STANDARD Section - Lower Rows (F, E, D, C, B, A)
        // Left block (11 seats each): F15-F5, E15-E5, D15-D5, C15-C5, B15-B5, A15-A5
        // Right block (4 seats each): F4-F1, E4-E1, D4-D1, C4-C1, B4-B1, A4-A1
        String[] lowerRows = {"F", "E", "D", "C", "B", "A"};
        for (String row : lowerRows) {
            generateSeatRow(seats, showtimeId, theaterId, row, 15, 5);  // Left block (11 seats)
            generateSeatRow(seats, showtimeId, theaterId, row, 4, 1);   // Right block (4 seats)
        }
        
        System.out.println("Generated " + seats.size() + " seats");
        return seats;
    }
    
    private void generateSeatRow(List<Seat> seats, Long showtimeId, Long theaterId, String row, int start, int end) {
        // Handle reverse numbering for DL seats and other sections as needed
        if (start > end) { // Reverse order
            for (int seatNum = start; seatNum >= end; seatNum--) {
                String seatNumber = row + seatNum;
                
                // Check if seat already exists
                Optional<Seat> existingSeat = seatRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
                
                if (existingSeat.isPresent()) {
                    seats.add(existingSeat.get());
                } else {
                    // Create new seat
                    Seat seat = new Seat();
                    seat.setShowtimeId(showtimeId);
                    seat.setTheaterId(theaterId);
                    seat.setSeatNumber(seatNumber);
                    seat.setRowNumber(row);
                    seat.setStatus(Seat.Status.AVAILABLE);
                    seatRepository.save(seat);
                    seats.add(seat);
                }
            }
        } else { // Normal order
            for (int seatNum = start; seatNum <= end; seatNum++) {
                String seatNumber = row + seatNum;
                
                // Check if seat already exists
                Optional<Seat> existingSeat = seatRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
                
                if (existingSeat.isPresent()) {
                    seats.add(existingSeat.get());
                } else {
                    // Create new seat
                    Seat seat = new Seat();
                    seat.setShowtimeId(showtimeId);
                    seat.setTheaterId(theaterId);
                    seat.setSeatNumber(seatNumber);
                    seat.setRowNumber(row);
                    seat.setStatus(Seat.Status.AVAILABLE);
                    seatRepository.save(seat);
                    seats.add(seat);
                }
            }
        }
    }

    public Seat findByShowtimeAndSeatNumber(Long showtimeId, String seatNumber) {
        return seatRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber).orElse(null);
    }

    @Transactional
    public Seat saveSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public List<Seat> getSeatsByShowtime(Long showtimeId) {
        return seatRepository.findByShowtimeId(showtimeId);
    }
    
    public boolean isSeatOccupied(Long showtimeId, String seatNumber) {
        Optional<Seat> seat = seatRepository.findByShowtimeIdAndSeatNumber(showtimeId, seatNumber);
        return seat.isPresent() && seat.get().getStatus() == Seat.Status.OCCUPIED;
    }
}
