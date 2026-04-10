package com.pgno98.moviebookingsystem11.config;

import com.pgno98.moviebookingsystem11.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RatingMigration implements CommandLineRunner {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void run(String... args) throws Exception {
        // Convert any existing ratings from 10-point scale to 5-point scale
        movieRepository.findAll().forEach(movie -> {
            if (movie.getRating() != null && movie.getRating() > 5.0) {
                // Convert from 10-point scale to 5-point scale
                double oldRating = movie.getRating();
                double newRating = (oldRating / 10.0) * 5.0;
                // Round to 1 decimal place
                newRating = Math.round(newRating * 10.0) / 10.0;
                movie.setRating(newRating);
                movieRepository.save(movie);
                System.out.println("Updated rating for " + movie.getTitle() + " from " + oldRating + " to " + newRating);
            }
        });
    }
}
