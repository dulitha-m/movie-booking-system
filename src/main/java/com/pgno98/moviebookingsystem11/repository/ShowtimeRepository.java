package com.pgno98.moviebookingsystem11.repository;

import com.pgno98.moviebookingsystem11.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    
    @Query("SELECT s FROM Showtime s WHERE s.movie.id = :movieId AND s.isActive = true AND s.showDateTime > :now ORDER BY s.showDateTime")
    List<Showtime> findByMovieIdAndActiveTrueAndShowDateTimeAfter(@Param("movieId") Long movieId, @Param("now") LocalDateTime now);
    
    @Query("SELECT s FROM Showtime s WHERE s.isActive = true AND s.showDateTime > :now ORDER BY s.showDateTime")
    List<Showtime> findByActiveTrueAndShowDateTimeAfter(@Param("now") LocalDateTime now);
}