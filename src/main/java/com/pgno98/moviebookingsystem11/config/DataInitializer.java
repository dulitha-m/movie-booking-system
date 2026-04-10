package com.pgno98.moviebookingsystem11.config;

import com.pgno98.moviebookingsystem11.entity.*;
import com.pgno98.moviebookingsystem11.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private TheaterRepository theaterRepository;
    
    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Clean up unwanted sample users first
        cleanupUnwantedUsers();
        
        // Clean up excess promotions to keep only the best 6
        cleanupExcessPromotions();
        
        // Only initialize if database is completely empty (no users, movies, theaters, or promotions)
        if (userRepository.count() == 0 && movieRepository.count() == 0 && theaterRepository.count() == 0 && promotionRepository.count() == 0) {
            System.out.println("Database is empty. Initializing sample data...");
            initializeData();
        } else {
            System.out.println("Database contains existing data. Skipping full initialization to preserve your data.");
            
            // Always ensure promotions exist
            if (promotionRepository.count() == 0) {
                System.out.println("No promotions found. Creating sample promotions...");
                createSamplePromotions();
            } else {
                // Update existing promotions to have Rs.1000 maximum discount
                updateExistingPromotionsMaxDiscount();
            }
            
            // Create sample bookings and reviews if they don't exist
            if (bookingRepository.count() == 0 && reviewRepository.count() == 0) {
                System.out.println("No bookings or reviews found. Creating sample bookings and reviews...");
                createSampleBookingsAndReviews();
            }
        }
    }
    
    private void cleanupUnwantedUsers() {
        // Remove specific unwanted sample users
        String[] unwantedEmails = {"jane.smith@example.com", "mike.johnson@example.com"};
        
        for (String email : unwantedEmails) {
            try {
                java.util.Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    System.out.println("Removing unwanted user: " + email);
                    userRepository.delete(user);
                    System.out.println("Successfully removed user: " + email);
                }
            } catch (Exception e) {
                System.err.println("Error removing user " + email + ": " + e.getMessage());
            }
        }
    }
    
    private void cleanupExcessPromotions() {
        // Keep only the best 6 promotions
        String[] keepPromotions = {"SAVE20", "WELCOME10", "FLAT100", "WEEKEND15", "STUDENT25", "EARLY30"};
        
        try {
            List<Promotion> allPromotions = promotionRepository.findAll();
            int removedCount = 0;
            
            for (Promotion promotion : allPromotions) {
                boolean shouldKeep = false;
                for (String keepCode : keepPromotions) {
                    if (promotion.getCode().equals(keepCode)) {
                        shouldKeep = true;
                        break;
                    }
                }
                
                if (!shouldKeep) {
                    System.out.println("Removing excess promotion: " + promotion.getCode() + " - " + promotion.getName());
                    promotionRepository.delete(promotion);
                    removedCount++;
                }
            }
            
            if (removedCount > 0) {
                System.out.println("Successfully removed " + removedCount + " excess promotions. Kept only the best 6.");
            } else {
                System.out.println("No excess promotions found. All promotions are already in the best 6 list.");
            }
            
        } catch (Exception e) {
            System.err.println("Error cleaning up excess promotions: " + e.getMessage());
        }
    }
    
    private void initializeData() {
        // Create admin user
        User admin = new User();
        admin.setEmail("admin@moviebooking.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setRole(User.Role.ADMIN);
        admin.setPhoneNumber("+1-555-0123");
        userRepository.save(admin);
        
        // Create sample customers
        User customer1 = new User();
        customer1.setEmail("customer@example.com");
        customer1.setPassword(passwordEncoder.encode("password"));
        customer1.setFirstName("John");
        customer1.setLastName("Doe");
        customer1.setRole(User.Role.USER);
        customer1.setPhoneNumber("+1-555-0124");
        userRepository.save(customer1);
        
        // Create additional sample customers
        User customer2 = new User();
        customer2.setEmail("sarah.wilson@example.com");
        customer2.setPassword(passwordEncoder.encode("password123"));
        customer2.setFirstName("Sarah");
        customer2.setLastName("Wilson");
        customer2.setRole(User.Role.USER);
        customer2.setPhoneNumber("+1-555-0125");
        userRepository.save(customer2);
        
        User customer3 = new User();
        customer3.setEmail("michael.brown@example.com");
        customer3.setPassword(passwordEncoder.encode("password123"));
        customer3.setFirstName("Michael");
        customer3.setLastName("Brown");
        customer3.setRole(User.Role.USER);
        customer3.setPhoneNumber("+1-555-0126");
        userRepository.save(customer3);
        
        User customer4 = new User();
        customer4.setEmail("emma.davis@example.com");
        customer4.setPassword(passwordEncoder.encode("password123"));
        customer4.setFirstName("Emma");
        customer4.setLastName("Davis");
        customer4.setRole(User.Role.USER);
        customer4.setPhoneNumber("+1-555-0127");
        userRepository.save(customer4);
        
        User customer5 = new User();
        customer5.setEmail("david.miller@example.com");
        customer5.setPassword(passwordEncoder.encode("password123"));
        customer5.setFirstName("David");
        customer5.setLastName("Miller");
        customer5.setRole(User.Role.USER);
        customer5.setPhoneNumber("+1-555-0128");
        userRepository.save(customer5);
        
        // Create sample theaters
        Theater theater1 = new Theater();
        theater1.setName("Cineplex Downtown");
        theater1.setAddress("123 Main Street");
        theater1.setCity("Colombo");
        theater1.setState("Western");
        theater1.setZipCode("10001");
        theater1.setPhoneNumber("+94-11-555-0101");
        theater1.setTotalScreens(8);
        theater1.setIsActive(true);
        theaterRepository.save(theater1);
        
        Theater theater2 = new Theater();
        theater2.setName("AMC Westside");
        theater2.setAddress("456 West Avenue");
        theater2.setCity("Kandy");
        theater2.setState("Central");
        theater2.setZipCode("20000");
        theater2.setPhoneNumber("+94-81-555-0102");
        theater2.setTotalScreens(12);
        theater2.setIsActive(true);
        theaterRepository.save(theater2);
        
        Theater theater3 = new Theater();
        theater3.setName("Regal Cinemas");
        theater3.setAddress("789 East Boulevard");
        theater3.setCity("Galle");
        theater3.setState("Southern");
        theater3.setZipCode("80000");
        theater3.setPhoneNumber("+94-91-555-0103");
        theater3.setTotalScreens(6);
        theater3.setIsActive(true);
        theaterRepository.save(theater3);
        
        // Create comprehensive movie collection covering all genres
        
        // ACTION MOVIES
        Movie movie1 = new Movie();
        movie1.setTitle("The Dark Knight");
        movie1.setDescription("When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.");
        movie1.setReleaseDate(LocalDate.of(2008, 7, 18));
        movie1.setDurationMinutes(152);
        movie1.setGenre(Movie.Genre.ACTION);
        movie1.setDirector("Christopher Nolan");
        movie1.setCast("Christian Bale, Heath Ledger, Aaron Eckhart, Michael Caine");
        movie1.setPosterUrl("/images/dark-knight-poster.jpeg");
        movie1.setTrailerUrl("https://www.youtube.com/watch?v=EXeTwQWrcwY");
        movie1.setRating(4.5);
        movie1.setIsActive(true);
        movieRepository.save(movie1);
        
        Movie movie2 = new Movie();
        movie2.setTitle("F1: The Movie");
        movie2.setDescription("A Formula One driver comes out of retirement to mentor and team up with a younger driver.");
        movie2.setReleaseDate(LocalDate.of(2025, 6, 27));
        movie2.setDurationMinutes(155);
        movie2.setGenre(Movie.Genre.ACTION);
        movie2.setDirector("Joseph Kosinski");
        movie2.setCast("Brad Pitt, Damson Idris, Javier Bardem, Kerry Condon, Tobias Menzies");
        movie2.setPosterUrl("/images/f1-the-movie-poster.jpg");
        movie2.setTrailerUrl("https://www.youtube.com/watch?v=EXeTwQWrcwY");
        movie2.setRating(3.85);
        movie2.setIsActive(true);
        movieRepository.save(movie2);
        
        Movie movie3 = new Movie();
        movie3.setTitle("Top Gun: Maverick");
        movie3.setDescription("After thirty years, Maverick is still pushing the envelope as a top naval aviator, but must confront ghosts of his past when he leads TOP GUN's elite graduates on a mission that demands the ultimate sacrifice from those chosen to fly it.");
        movie3.setReleaseDate(LocalDate.of(2022, 5, 27));
        movie3.setDurationMinutes(131);
        movie3.setGenre(Movie.Genre.ACTION);
        movie3.setDirector("Joseph Kosinski");
        movie3.setCast("Tom Cruise, Miles Teller, Jennifer Connelly, Jon Hamm");
        movie3.setPosterUrl("/images/topgun-maverick.jpeg");
        movie3.setTrailerUrl("https://www.youtube.com/watch?v=qSqVVswa420");
        movie3.setRating(4.2);
        movie3.setIsActive(true);
        movieRepository.save(movie3);
        
        // COMEDY MOVIES
        Movie movie4 = new Movie();
        movie4.setTitle("Deadpool 3");
        movie4.setDescription("Wolverine is recovering from his injuries when he crosses paths with the loudmouth, fast-talking mercenary Deadpool. They team up to defeat a common enemy.");
        movie4.setReleaseDate(LocalDate.of(2024, 7, 26));
        movie4.setDurationMinutes(127);
        movie4.setGenre(Movie.Genre.COMEDY);
        movie4.setDirector("Shawn Levy");
        movie4.setCast("Ryan Reynolds, Hugh Jackman, Emma Corrin, Matthew Macfadyen");
        movie4.setPosterUrl("/images/deadpool-3.jpeg");
        movie4.setTrailerUrl("https://www.youtube.com/watch?v=u3V5KD1Qklk");
        movie4.setRating(4.1);
        movie4.setIsActive(true);
        movieRepository.save(movie4);
        
        Movie movie5 = new Movie();
        movie5.setTitle("Barbie");
        movie5.setDescription("Barbie and Ken are having the time of their lives in the colorful and seemingly perfect world of Barbie Land. However, when they get a chance to go to the real world, they soon discover the joys and perils of living among humans.");
        movie5.setReleaseDate(LocalDate.of(2023, 7, 21));
        movie5.setDurationMinutes(114);
        movie5.setGenre(Movie.Genre.COMEDY);
        movie5.setDirector("Greta Gerwig");
        movie5.setCast("Margot Robbie, Ryan Gosling, America Ferrera, Kate McKinnon");
        movie5.setPosterUrl("/images/barbie.jpeg");
        movie5.setTrailerUrl("https://www.youtube.com/watch?v=pBk4NYhWNMM");
        movie5.setRating(3.8);
        movie5.setIsActive(true);
        movieRepository.save(movie5);
        
        // DRAMA MOVIES
        Movie movie6 = new Movie();
        movie6.setTitle("Oppenheimer");
        movie6.setDescription("The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.");
        movie6.setReleaseDate(LocalDate.of(2023, 7, 21));
        movie6.setDurationMinutes(180);
        movie6.setGenre(Movie.Genre.DRAMA);
        movie6.setDirector("Christopher Nolan");
        movie6.setCast("Cillian Murphy, Emily Blunt, Matt Damon, Robert Downey Jr.");
        movie6.setPosterUrl("/images/oppenheimer.jpeg");
        movie6.setTrailerUrl("https://www.youtube.com/watch?v=uYPbbksJxIg");
        movie6.setRating(4.3);
        movie6.setIsActive(true);
        movieRepository.save(movie6);
        
        Movie movie7 = new Movie();
        movie7.setTitle("The Whale");
        movie7.setDescription("A reclusive English teacher suffering from severe obesity attempts to reconnect with his estranged teenage daughter.");
        movie7.setReleaseDate(LocalDate.of(2022, 12, 9));
        movie7.setDurationMinutes(117);
        movie7.setGenre(Movie.Genre.DRAMA);
        movie7.setDirector("Darren Aronofsky");
        movie7.setCast("Brendan Fraser, Sadie Sink, Hong Chau, Ty Simpkins");
        movie7.setPosterUrl("/images/the-whale.jpeg");
        movie7.setTrailerUrl("https://www.youtube.com/watch?v=5VYb3B1ETlk");
        movie7.setRating(3.9);
        movie7.setIsActive(true);
        movieRepository.save(movie7);
        
        // HORROR MOVIES
        Movie movie8 = new Movie();
        movie8.setTitle("M3GAN");
        movie8.setDescription("A robotics engineer at a toy company builds a life-like doll that begins to take on a life of its own.");
        movie8.setReleaseDate(LocalDate.of(2022, 12, 28));
        movie8.setDurationMinutes(102);
        movie8.setGenre(Movie.Genre.HORROR);
        movie8.setDirector("Gerard Johnstone");
        movie8.setCast("Allison Williams, Violet McGraw, Ronny Chieng, Brian Jordan Alvarez");
        movie8.setPosterUrl("/images/megan.jpeg");
        movie8.setTrailerUrl("https://www.youtube.com/watch?v=9V9b8i0FJ4k");
        movie8.setRating(3.2);
        movie8.setIsActive(true);
        movieRepository.save(movie8);
        
        Movie movie9 = new Movie();
        movie9.setTitle("Scream VI");
        movie9.setDescription("Four survivors of the original Ghostface killings leave Woodsboro behind for a fresh start in New York City.");
        movie9.setReleaseDate(LocalDate.of(2023, 3, 10));
        movie9.setDurationMinutes(123);
        movie9.setGenre(Movie.Genre.HORROR);
        movie9.setDirector("Matt Bettinelli-Olpin, Tyler Gillett");
        movie9.setCast("Melissa Barrera, Jenna Ortega, Courteney Cox, Hayden Panettiere");
        movie9.setPosterUrl("/images/scream-6.jpeg");
        movie9.setTrailerUrl("https://www.youtube.com/watch?v=h74AXqw4Opc");
        movie9.setRating(3.4);
        movie9.setIsActive(true);
        movieRepository.save(movie9);
        
        // ROMANCE MOVIES
        Movie movie10 = new Movie();
        movie10.setTitle("Anyone But You");
        movie10.setDescription("After an amazing first date, Bea and Ben's fiery attraction turns ice cold -- until they find themselves unexpectedly reunited at a destination wedding in Australia.");
        movie10.setReleaseDate(LocalDate.of(2023, 12, 22));
        movie10.setDurationMinutes(103);
        movie10.setGenre(Movie.Genre.ROMANCE);
        movie10.setDirector("Will Gluck");
        movie10.setCast("Sydney Sweeney, Glen Powell, Alexandra Shipp, GaTa");
        movie10.setPosterUrl("/images/anyone-but-you.jpeg");
        movie10.setTrailerUrl("https://www.youtube.com/watch?v=Q9D7in1Ckwk");
        movie10.setRating(3.6);
        movie10.setIsActive(true);
        movieRepository.save(movie10);
        
        Movie movie11 = new Movie();
        movie11.setTitle("The Notebook");
        movie11.setDescription("A poor yet passionate young man falls in love with a rich young woman, giving her a sense of freedom, but they are soon separated because of their social differences.");
        movie11.setReleaseDate(LocalDate.of(2004, 6, 25));
        movie11.setDurationMinutes(123);
        movie11.setGenre(Movie.Genre.ROMANCE);
        movie11.setDirector("Nick Cassavetes");
        movie11.setCast("Ryan Gosling, Rachel McAdams, James Garner, Gena Rowlands");
        movie11.setPosterUrl("/images/the-notebook.jpeg");
        movie11.setTrailerUrl("https://www.youtube.com/watch?v=4M7LIcH8C9U");
        movie11.setRating(4.0);
        movie11.setIsActive(true);
        movieRepository.save(movie11);
        
        // FAMILY MOVIES
        Movie movie12 = new Movie();
        movie12.setTitle("How to Train Your Dragon");
        movie12.setDescription("As an ancient threat endangers both Vikings and dragons alike on the isle of Berk, the friendship between Hiccup, an inventive Viking, and Toothless, a Night Fury dragon, becomes the key to both species forging a new future together.");
        movie12.setReleaseDate(LocalDate.of(2025, 6, 13));
        movie12.setDurationMinutes(125);
        movie12.setGenre(Movie.Genre.FAMILY);
        movie12.setDirector("Dean DeBlois");
        movie12.setCast("Mason Thames, Nico Parker, Gerard Butler, Nick Frost, Gabriel Howell");
        movie12.setPosterUrl("/images/how-to-train-your-dragon-poster.jpg");
        movie12.setTrailerUrl("https://www.youtube.com/watch?v=EXeTwQWrcwY");
        movie12.setRating(3.9);
        movie12.setIsActive(true);
        movieRepository.save(movie12);
        
        Movie movie13 = new Movie();
        movie13.setTitle("Elemental");
        movie13.setDescription("Follows Ember and Wade, in a city where fire-, water-, land- and air-residents live together.");
        movie13.setReleaseDate(LocalDate.of(2023, 6, 16));
        movie13.setDurationMinutes(109);
        movie13.setGenre(Movie.Genre.FAMILY);
        movie13.setDirector("Peter Sohn");
        movie13.setCast("Leah Lewis, Mamoudou Athie, Ronnie del Carmen, Shila Ommi");
        movie13.setPosterUrl("/images/elemental.jpeg");
        movie13.setTrailerUrl("https://www.youtube.com/watch?v=hXzcyx9V0xw");
        movie13.setRating(3.7);
        movie13.setIsActive(true);
        movieRepository.save(movie13);
        
        // SCI-FI MOVIES
        Movie movie14 = new Movie();
        movie14.setTitle("Avatar: The Way of Water");
        movie14.setDescription("Set more than a decade after the events of the first film, Avatar: The Way of Water begins to tell the story of the Sully family, the trouble that follows them, the lengths they go to keep each other safe.");
        movie14.setReleaseDate(LocalDate.of(2022, 12, 16));
        movie14.setDurationMinutes(192);
        movie14.setGenre(Movie.Genre.SCI_FI);
        movie14.setDirector("James Cameron");
        movie14.setCast("Sam Worthington, Zoe Saldana, Sigourney Weaver, Stephen Lang");
        movie14.setPosterUrl("/images/avatar-the-way-of-water.jpeg");
        movie14.setTrailerUrl("https://www.youtube.com/watch?v=d9MyW72ELq0");
        movie14.setRating(4.0);
        movie14.setIsActive(true);
        movieRepository.save(movie14);
        
        Movie movie15 = new Movie();
        movie15.setTitle("Dune: Part Two");
        movie15.setDescription("Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.");
        movie15.setReleaseDate(LocalDate.of(2024, 3, 1));
        movie15.setDurationMinutes(166);
        movie15.setGenre(Movie.Genre.SCI_FI);
        movie15.setDirector("Denis Villeneuve");
        movie15.setCast("Timothée Chalamet, Zendaya, Rebecca Ferguson, Josh Brolin");
        movie15.setPosterUrl("/images/dune-part-2.jpeg");
        movie15.setTrailerUrl("https://www.youtube.com/watch?v=Way9Dexny3w");
        movie15.setRating(4.1);
        movie15.setIsActive(true);
        movieRepository.save(movie15);
        
        // THRILLER MOVIES
        Movie movie16 = new Movie();
        movie16.setTitle("Saltburn");
        movie16.setDescription("A student at Oxford University finds himself drawn into the world of a charming and aristocratic classmate, who invites him to Saltburn, his eccentric family's sprawling estate, for a summer never to be forgotten.");
        movie16.setReleaseDate(LocalDate.of(2023, 11, 17));
        movie16.setDurationMinutes(131);
        movie16.setGenre(Movie.Genre.THRILLER);
        movie16.setDirector("Emerald Fennell");
        movie16.setCast("Barry Keoghan, Jacob Elordi, Rosamund Pike, Richard E. Grant");
        movie16.setPosterUrl("/images/saltburn.jpeg");
        movie16.setTrailerUrl("https://www.youtube.com/watch?v=6c1BThu95d8");
        movie16.setRating(3.5);
        movie16.setIsActive(true);
        movieRepository.save(movie16);
        
        Movie movie17 = new Movie();
        movie17.setTitle("The Killer");
        movie17.setDescription("After a fateful near-miss, an assassin battles his employers, and himself, on an international manhunt he insists isn't personal.");
        movie17.setReleaseDate(LocalDate.of(2023, 10, 27));
        movie17.setDurationMinutes(118);
        movie17.setGenre(Movie.Genre.THRILLER);
        movie17.setDirector("David Fincher");
        movie17.setCast("Michael Fassbender, Tilda Swinton, Charles Parnell, Arliss Howard");
        movie17.setPosterUrl("/images/the-killer.jpeg");
        movie17.setTrailerUrl("https://www.youtube.com/watch?v=5S6f7k8Buwg");
        movie17.setRating(3.8);
        movie17.setIsActive(true);
        movieRepository.save(movie17);
        
        // FANTASY MOVIES
        Movie movie18 = new Movie();
        movie18.setTitle("Wonka");
        movie18.setDescription("The story of how the world's greatest inventor, magician and chocolate-maker became the beloved Willy Wonka we know today.");
        movie18.setReleaseDate(LocalDate.of(2023, 12, 15));
        movie18.setDurationMinutes(116);
        movie18.setGenre(Movie.Genre.FAMILY);
        movie18.setDirector("Paul King");
        movie18.setCast("Timothée Chalamet, Calah Lane, Keegan-Michael Key, Paterson Joseph");
        movie18.setPosterUrl("/images/wonka.jpeg");
        movie18.setTrailerUrl("https://www.youtube.com/watch?v=otNh9bTjXU8");
        movie18.setRating(3.9);
        movie18.setIsActive(true);
        movieRepository.save(movie18);
        
        Movie movie19 = new Movie();
        movie19.setTitle("The Little Mermaid");
        movie19.setDescription("A young mermaid makes a deal with a sea witch to trade her beautiful voice for human legs so she can discover the world above water and impress a prince.");
        movie19.setReleaseDate(LocalDate.of(2023, 5, 26));
        movie19.setDurationMinutes(135);
        movie19.setGenre(Movie.Genre.FAMILY);
        movie19.setDirector("Rob Marshall");
        movie19.setCast("Halle Bailey, Jonah Hauer-King, Melissa McCarthy, Javier Bardem");
        movie19.setPosterUrl("/images/the-little-mermaid.jpeg");
        movie19.setTrailerUrl("https://www.youtube.com/watch?v=kpGo2_d3oYE");
        movie19.setRating(3.6);
        movie19.setIsActive(true);
        movieRepository.save(movie19);
        
        // ANIMATION MOVIES
        Movie movie20 = new Movie();
        movie20.setTitle("Spider-Man: Across the Spider-Verse");
        movie20.setDescription("After reuniting with Gwen Stacy, Brooklyn's full-time, friendly neighborhood Spider-Man is catapulted across the Multiverse, where he encounters a team of Spider-People charged with protecting its very existence.");
        movie20.setReleaseDate(LocalDate.of(2023, 6, 2));
        movie20.setDurationMinutes(140);
        movie20.setGenre(Movie.Genre.ANIMATION);
        movie20.setDirector("Joaquim Dos Santos, Kemp Powers, Justin K. Thompson");
        movie20.setCast("Shameik Moore, Hailee Steinfeld, Brian Tyree Henry, Jake Johnson");
        movie20.setPosterUrl("/images/spiderman-across-the-spider-verse.jpeg");
        movie20.setTrailerUrl("https://www.youtube.com/watch?v=cqGjhVJWtEg");
        movie20.setRating(4.2);
        movie20.setIsActive(true);
        movieRepository.save(movie20);
        
        // DOCUMENTARY MOVIES
        Movie movie21 = new Movie();
        movie21.setTitle("Free Solo");
        movie21.setDescription("Follow Alex Honnold as he attempts to become the first person to ever free solo climb Yosemite's 3,000ft high El Capitan Wall. With no ropes or safety gear, he completed arguably the greatest feat in rock climbing history.");
        movie21.setReleaseDate(LocalDate.of(2018, 9, 28));
        movie21.setDurationMinutes(100);
        movie21.setGenre(Movie.Genre.DOCUMENTARY);
        movie21.setDirector("Elizabeth Chai Vasarhelyi, Jimmy Chin");
        movie21.setCast("Alex Honnold, Tommy Caldwell, Jimmy Chin, Sanni McCandless");
        movie21.setPosterUrl("/images/free-solo.jpeg");
        movie21.setTrailerUrl("https://www.youtube.com/watch?v=urRVZ4SW7WU");
        movie21.setRating(4.4);
        movie21.setIsActive(true);
        movieRepository.save(movie21);
        
        // Create comprehensive showtimes for all movies
        createShowtimesForMovies();
        
        // Create sample promotions
        createSamplePromotions();
        
        // Create sample bookings and reviews
        createSampleBookingsAndReviews();
        
        System.out.println("Comprehensive sample data initialized successfully!");
        System.out.println("Total Movies: " + movieRepository.count());
        System.out.println("Total Theaters: " + theaterRepository.count());
        System.out.println("Total Users: " + userRepository.count());
        System.out.println("Total Promotions: " + promotionRepository.count());
        System.out.println("Total Bookings: " + bookingRepository.count());
        System.out.println("Total Reviews: " + reviewRepository.count());
    }
    
    private void createShowtimesForMovies() {
        // Get all movies and theaters
        List<Movie> movies = movieRepository.findAll();
        List<Theater> theaters = theaterRepository.findAll();
        
        // Create showtimes for each movie across different theaters
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            Theater theater = theaters.get(i % theaters.size());
            
            // Create multiple showtimes for each movie
            for (int j = 0; j < 3; j++) {
                Showtime showtime = new Showtime();
                showtime.setMovie(movie);
                showtime.setTheater(theater);
                showtime.setScreenNumber((j % theater.getTotalScreens()) + 1);
                
                // Spread showtimes across different days and times
                LocalDateTime baseTime = LocalDateTime.now().plusDays(j + 1);
                int hour = 14 + (j * 3); // 2 PM, 5 PM, 8 PM
                showtime.setShowDateTime(baseTime.withHour(hour).withMinute(0));
                
                // Vary ticket prices based on movie popularity and time
                BigDecimal basePrice = new BigDecimal("1000.00");
                if (movie.getRating() > 4.0) {
                    basePrice = basePrice.add(new BigDecimal("200.00"));
                }
                if (hour >= 18) { // Evening shows cost more
                    basePrice = basePrice.add(new BigDecimal("300.00"));
                }
                showtime.setTicketPrice(basePrice);
                
                showtime.setTotalSeats(100 + (j * 20)); // Vary seat counts
                showtime.setAvailableSeats(showtime.getTotalSeats());
                showtime.setIsActive(true);
                showtimeRepository.save(showtime);
            }
        }
    }
    
    private void createSamplePromotions() {
        // Summer Sale - 20% off
        Promotion promotion1 = new Promotion();
        promotion1.setCode("SAVE20");
        promotion1.setName("Summer Sale");
        promotion1.setDescription("Get 20% off on your movie booking this summer!");
        promotion1.setDiscountType(Promotion.DiscountType.PERCENTAGE);
        promotion1.setDiscountValue(new BigDecimal("20"));
        promotion1.setMinimumAmount(new BigDecimal("500"));
        promotion1.setMaximumDiscount(new BigDecimal("1000"));
        promotion1.setUsageLimit(100);
        promotion1.setUsedCount(0);
        promotion1.setStartDate(LocalDate.now());
        promotion1.setEndDate(LocalDate.now().plusMonths(3));
        promotion1.setIsActive(true);
        promotionRepository.save(promotion1);
        
        // Welcome Offer - 10% off
        Promotion promotion2 = new Promotion();
        promotion2.setCode("WELCOME10");
        promotion2.setName("Welcome Offer");
        promotion2.setDescription("Welcome to Book My Show! Get 10% off on your first booking.");
        promotion2.setDiscountType(Promotion.DiscountType.PERCENTAGE);
        promotion2.setDiscountValue(new BigDecimal("10"));
        promotion2.setMinimumAmount(new BigDecimal("300"));
        promotion2.setMaximumDiscount(new BigDecimal("1000"));
        promotion2.setUsageLimit(50);
        promotion2.setUsedCount(0);
        promotion2.setStartDate(LocalDate.now());
        promotion2.setEndDate(LocalDate.now().plusMonths(6));
        promotion2.setIsActive(true);
        promotionRepository.save(promotion2);
        
        // Flat Discount - Rs. 100 off
        Promotion promotion3 = new Promotion();
        promotion3.setCode("FLAT100");
        promotion3.setName("Flat Discount");
        promotion3.setDescription("Get Rs. 100 off on bookings above Rs. 800");
        promotion3.setDiscountType(Promotion.DiscountType.FIXED_AMOUNT);
        promotion3.setDiscountValue(new BigDecimal("100"));
        promotion3.setMinimumAmount(new BigDecimal("800"));
        promotion3.setMaximumDiscount(new BigDecimal("1000"));
        promotion3.setUsageLimit(200);
        promotion3.setUsedCount(0);
        promotion3.setStartDate(LocalDate.now());
        promotion3.setEndDate(LocalDate.now().plusMonths(2));
        promotion3.setIsActive(true);
        promotionRepository.save(promotion3);
        
        // Weekend Special - 15% off
        Promotion promotion4 = new Promotion();
        promotion4.setCode("WEEKEND15");
        promotion4.setName("Weekend Special");
        promotion4.setDescription("Get 15% off on weekend movie bookings!");
        promotion4.setDiscountType(Promotion.DiscountType.PERCENTAGE);
        promotion4.setDiscountValue(new BigDecimal("15"));
        promotion4.setMinimumAmount(new BigDecimal("400"));
        promotion4.setMaximumDiscount(new BigDecimal("1000"));
        promotion4.setUsageLimit(150);
        promotion4.setUsedCount(0);
        promotion4.setStartDate(LocalDate.now());
        promotion4.setEndDate(LocalDate.now().plusMonths(4));
        promotion4.setIsActive(true);
        promotionRepository.save(promotion4);
        
        // Student Discount - 25% off
        Promotion promotion5 = new Promotion();
        promotion5.setCode("STUDENT25");
        promotion5.setName("Student Discount");
        promotion5.setDescription("Students get 25% off on all movie tickets!");
        promotion5.setDiscountType(Promotion.DiscountType.PERCENTAGE);
        promotion5.setDiscountValue(new BigDecimal("25"));
        promotion5.setMinimumAmount(new BigDecimal("400"));
        promotion5.setMaximumDiscount(new BigDecimal("1000"));
        promotion5.setUsageLimit(300);
        promotion5.setUsedCount(0);
        promotion5.setStartDate(LocalDate.now());
        promotion5.setEndDate(LocalDate.now().plusMonths(12));
        promotion5.setIsActive(true);
        promotionRepository.save(promotion5);
        
        // Early Bird Special - 30% off
        Promotion promotion6 = new Promotion();
        promotion6.setCode("EARLY30");
        promotion6.setName("Early Bird");
        promotion6.setDescription("Book early and save 30% on your movie tickets!");
        promotion6.setDiscountType(Promotion.DiscountType.PERCENTAGE);
        promotion6.setDiscountValue(new BigDecimal("30"));
        promotion6.setMinimumAmount(new BigDecimal("500"));
        promotion6.setMaximumDiscount(new BigDecimal("1000"));
        promotion6.setUsageLimit(100);
        promotion6.setUsedCount(0);
        promotion6.setStartDate(LocalDate.now());
        promotion6.setEndDate(LocalDate.now().plusMonths(3));
        promotion6.setIsActive(true);
        promotionRepository.save(promotion6);
        
        System.out.println("6 best promotions created successfully!");
    }
    
    private void updateExistingPromotionsMaxDiscount() {
        System.out.println("Updating existing promotions to have Rs.1000 maximum discount...");
        
        List<Promotion> allPromotions = promotionRepository.findAll();
        for (Promotion promotion : allPromotions) {
            promotion.setMaximumDiscount(new BigDecimal("1000"));
            promotionRepository.save(promotion);
            System.out.println("Updated " + promotion.getCode() + " (" + promotion.getName() + ") to have Rs.1000 maximum discount");
        }
        
        System.out.println("All existing promotions updated successfully!");
    }
    
    private void createSampleBookingsAndReviews() {
        System.out.println("Creating sample bookings and reviews...");
        
        // Get all users (customers only), movies, and showtimes
        List<User> customers = userRepository.findAll().stream()
            .filter(user -> user.getRole() == User.Role.USER)
            .collect(java.util.stream.Collectors.toList());
        
        List<Movie> movies = movieRepository.findAll();
        List<Showtime> showtimes = showtimeRepository.findAll();
        
        if (customers.isEmpty() || showtimes.isEmpty()) {
            System.out.println("No customers or showtimes found. Skipping booking creation.");
            return;
        }
        
        // Create sample bookings
        String[] seatNumbers = {"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3", "D1", "D2", "D3"};
        String[] paymentMethods = {"CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER"};
        Booking.Status[] bookingStatuses = {Booking.Status.CONFIRMED, Booking.Status.CANCELLED};
        
        for (int i = 0; i < 25; i++) { // Create 25 sample bookings
            Booking booking = new Booking();
            
            // Assign random customer
            User customer = customers.get(i % customers.size());
            booking.setUser(customer);
            
            // Assign random showtime
            Showtime showtime = showtimes.get(i % showtimes.size());
            booking.setShowtime(showtime);
            
            // Generate booking reference
            booking.setBookingReference("BK" + String.format("%06d", i + 1));
            
            // Select random seats (1-3 seats per booking)
            int numSeats = (i % 3) + 1;
            StringBuilder selectedSeats = new StringBuilder();
            for (int j = 0; j < numSeats; j++) {
                if (j > 0) selectedSeats.append(",");
                selectedSeats.append(seatNumbers[(i + j) % seatNumbers.length]);
            }
            booking.setSelectedSeats(selectedSeats.toString());
            
            // Calculate total amount
            BigDecimal ticketPrice = showtime.getTicketPrice();
            BigDecimal totalAmount = ticketPrice.multiply(BigDecimal.valueOf(numSeats));
            
            // Apply random promotion (20% chance)
            if (i % 5 == 0) {
                List<Promotion> promotions = promotionRepository.findAll();
                if (!promotions.isEmpty()) {
                    Promotion promotion = promotions.get(i % promotions.size());
                    booking.setPromotionCode(promotion.getCode());
                    
                    // Calculate discount
                    BigDecimal discountAmount = BigDecimal.ZERO;
                    if (promotion.getDiscountType() == Promotion.DiscountType.PERCENTAGE) {
                        discountAmount = totalAmount.multiply(promotion.getDiscountValue()).divide(BigDecimal.valueOf(100));
                        if (promotion.getMaximumDiscount() != null && discountAmount.compareTo(promotion.getMaximumDiscount()) > 0) {
                            discountAmount = promotion.getMaximumDiscount();
                        }
                    } else {
                        discountAmount = promotion.getDiscountValue();
                    }
                    
                    booking.setDiscountAmount(discountAmount);
                    totalAmount = totalAmount.subtract(discountAmount);
                }
            }
            
            booking.setTotalAmount(totalAmount);
            booking.setPaymentMethod(paymentMethods[i % paymentMethods.length]);
            booking.setStatus(bookingStatuses[i % bookingStatuses.length]);
            
            // Set booking date (spread across last 30 days)
            LocalDateTime bookingDate = LocalDateTime.now().minusDays(i % 30).minusHours(i % 24);
            booking.setBookingDate(bookingDate);
            
            bookingRepository.save(booking);
        }
        
        // Create sample reviews
        String[] reviewComments = {
            "Amazing movie! Highly recommended.",
            "Great acting and storyline.",
            "Perfect for a family outing.",
            "One of the best movies I've seen this year.",
            "Excellent cinematography and direction.",
            "Loved every minute of it!",
            "Great entertainment value.",
            "Must watch movie!",
            "Fantastic performance by the cast.",
            "Outstanding movie experience.",
            "Really enjoyed this film.",
            "Great plot and character development.",
            "Highly entertaining!",
            "Wonderful movie with great visuals.",
            "Excellent storytelling."
        };
        
        for (int i = 0; i < 20; i++) { // Create 20 sample reviews
            Review review = new Review();
            
            // Assign random customer
            User customer = customers.get(i % customers.size());
            review.setUser(customer);
            
            // Assign random movie
            Movie movie = movies.get(i % movies.size());
            review.setMovie(movie);
            
            // Random rating (3-5 stars)
            int rating = 3 + (i % 3);
            review.setRating(rating);
            
            // Random comment
            review.setComment(reviewComments[i % reviewComments.length]);
            
            // Most reviews are approved
            review.setIsApproved(i % 10 != 0); // 90% approved
            
            // Set creation date (spread across last 20 days)
            LocalDateTime createdAt = LocalDateTime.now().minusDays(i % 20).minusHours(i % 12);
            review.setCreatedAt(createdAt);
            review.setUpdatedAt(createdAt);
            
            reviewRepository.save(review);
        }
        
        System.out.println("Sample bookings and reviews created successfully!");
    }
}