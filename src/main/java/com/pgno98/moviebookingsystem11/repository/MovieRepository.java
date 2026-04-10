package com.pgno98.moviebookingsystem11.repository;

import com.pgno98.moviebookingsystem11.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByIsActiveTrue();
    List<Movie> findByGenre(Movie.Genre genre);
    List<Movie> findByReleaseDateAfter(LocalDate date);
    List<Movie> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT m FROM Movie m WHERE m.isActive = true ORDER BY m.rating DESC")
    List<Movie> findTopRatedMovies();
    
    @Query("SELECT m FROM Movie m WHERE m.isActive = true ORDER BY m.createdAt DESC")
    List<Movie> findLatestMovies();
}
