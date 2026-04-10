package com.pgno98.moviebookingsystem11.service;

import com.pgno98.moviebookingsystem11.entity.Movie;
import com.pgno98.moviebookingsystem11.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    
    @Autowired
    private MovieRepository movieRepository;
    
    public List<Movie> getAllActiveMovies() {
        return movieRepository.findByIsActiveTrue();
    }
    
    public List<Movie> getMoviesByGenre(Movie.Genre genre) {
        return movieRepository.findByGenre(genre);
    }
    
    public List<Movie> getLatestMovies() {
        return movieRepository.findLatestMovies();
    }
    
    public List<Movie> getTopRatedMovies() {
        return movieRepository.findTopRatedMovies();
    }
    
    public List<Movie> searchMoviesByTitle(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }
    
    @Transactional
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }
    
    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
    
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
    
    public void updateMovieRating(Long movieId) {
        // This will be implemented when we add review functionality
        // For now, we'll keep it as a placeholder
    }
}
