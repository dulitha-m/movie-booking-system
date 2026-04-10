package com.pgno98.moviebookingsystem11.repository;

import com.pgno98.moviebookingsystem11.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
    List<Seat> findByShowtimeId(Long showtimeId);
    
    Optional<Seat> findByShowtimeIdAndSeatNumber(Long showtimeId, String seatNumber);
    
    List<Seat> findByTheaterId(Long theaterId);
}