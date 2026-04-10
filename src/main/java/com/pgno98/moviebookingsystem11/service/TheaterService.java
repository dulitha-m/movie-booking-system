package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.Theater;
import com.pgno98.moviebookingsystem11.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    public List<Theater> getAllActiveTheaters() {
        return theaterRepository.findByIsActiveTrue();
    }

    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    public Optional<Theater> findById(Long id) {
        return theaterRepository.findById(id);
    }

    @Transactional
    public Theater saveTheater(Theater theater) {
        return theaterRepository.save(theater);
    }
}