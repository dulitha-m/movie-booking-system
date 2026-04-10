package com.pgno98.moviebookingsystem11.controller;

import com.pgno98.moviebookingsystem11.entity.Movie;
import com.pgno98.moviebookingsystem11.entity.Review;
import com.pgno98.moviebookingsystem11.service.MovieService;
import com.pgno98.moviebookingsystem11.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    
    @Autowired
    private MovieService movieService;
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping("/")
    public String home(Model model) {
        // Get only featured movies (limit to 8 latest movies)
        List<Movie> allMovies = movieService.getAllActiveMovies();
        List<Movie> featuredMovies = allMovies.stream()
            .sorted((m1, m2) -> m2.getReleaseDate().compareTo(m1.getReleaseDate()))
            .limit(8)
            .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("featuredMovies", featuredMovies);
        return "home";
    }
    
    @GetMapping("/home")
    public String homePage(Model model) {
        return home(model);
    }
    
    @GetMapping("/movies")
    public String movies(@RequestParam(required = false) String genre, 
                        @RequestParam(required = false) String search, 
                        Model model) {
        List<Movie> movies;
        
        if (search != null && !search.isEmpty()) {
            movies = movieService.searchMoviesByTitle(search);
        } else if (genre != null && !genre.isEmpty()) {
            try {
                movies = movieService.getMoviesByGenre(Movie.Genre.valueOf(genre.toUpperCase()));
            } catch (IllegalArgumentException e) {
                movies = movieService.getAllActiveMovies();
            }
        } else {
            movies = movieService.getAllActiveMovies();
        }
        
        // Calculate average ratings for each movie (same as customer view)
        java.util.Map<Long, Double> movieAverageRatings = new java.util.HashMap<>();
        for (Movie movie : movies) {
            double averageRating = reviewService.getAverageRatingForMovie(movie.getId());
            movieAverageRatings.put(movie.getId(), averageRating);
        }

        model.addAttribute("movies", movies);
        model.addAttribute("genres", Movie.Genre.values());
        model.addAttribute("movieAverageRatings", movieAverageRatings);
        return "movies";
    }
    
    @GetMapping("/movies/{id}")
    public String movieDetails(@PathVariable Long id, Model model) {
        Optional<Movie> movieOpt = movieService.findById(id);
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            model.addAttribute("movie", movie);
            
            // Get recent reviews (limit to 4 most recent)
            List<Review> recentReviews = reviewService.getReviewsByMovie(id);
            if (recentReviews.size() > 4) {
                recentReviews = recentReviews.subList(0, 4);
            }
            model.addAttribute("recentReviews", recentReviews);
            
            // Calculate average rating from reviews
            double averageRating = reviewService.getAverageRatingForMovie(id);
            model.addAttribute("averageRating", averageRating);
            
            return "movie-details";
        }
        return "redirect:/movies";
    }
}
