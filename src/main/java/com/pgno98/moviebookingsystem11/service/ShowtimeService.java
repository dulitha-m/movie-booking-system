package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.Showtime;
import com.pgno98.moviebookingsystem11.repository.ShowtimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;

    public List<Showtime> getActiveShowtimesByMovie(Long movieId) {
        return showtimeRepository.findByMovieIdAndActiveTrueAndShowDateTimeAfter(movieId, LocalDateTime.now());
    }

    public Optional<Showtime> findById(Long id) {
        return showtimeRepository.findById(id);
    }

    @Transactional
    public Showtime saveShowtime(Showtime showtime) {
        return showtimeRepository.save(showtime);
    }

    public List<Showtime> getAllActiveShowtimes() {
        return showtimeRepository.findByActiveTrueAndShowDateTimeAfter(LocalDateTime.now());
    }

    public long countUpcomingShowtimes() {
        return showtimeRepository.findByActiveTrueAndShowDateTimeAfter(LocalDateTime.now()).size();
    }

    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    @Transactional
    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }
}