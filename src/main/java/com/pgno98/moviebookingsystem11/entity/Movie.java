package com.pgno98.moviebookingsystem11.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Enumerated(EnumType.STRING)
    private Genre genre;
    
    @Column(name = "director")
    private String director;
    
    @Column(name = "cast")
    private String cast;
    
    @Column(name = "poster_url")
    private String posterUrl;
    
    @Column(name = "trailer_url")
    private String trailerUrl;
    
    @Column(name = "rating")
    private Double rating;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Showtime> showtimes;
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum Genre {
        ACTION("Action"), 
        COMEDY("Comedy"), 
        DRAMA("Drama"), 
        HORROR("Horror"), 
        ROMANCE("Romance"), 
        SCI_FI("Sci-Fi"), 
        THRILLER("Thriller"), 
        ANIMATION("Animation"), 
        DOCUMENTARY("Documentary"), 
        FAMILY("Family");
        
        private final String displayName;
        
        Genre(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
