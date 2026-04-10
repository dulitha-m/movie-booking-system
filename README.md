# Movie Booking System

A comprehensive web-based movie booking system built with Spring Boot and Java, featuring responsive HTML, CSS, and JavaScript interfaces, integrated with MySQL database.

## Features

### 🎬 Core Functionality
- **User Management**: Registration, login, and profile management
- **Movie Management**: Browse movies, view details, and search functionality
- **Review System**: Add, edit, and delete movie reviews and ratings
- **Admin Dashboard**: Comprehensive administrative interface with data visualization

### 🔐 Security Features
- Spring Security integration with role-based access control
- Password encryption using BCrypt
- Secure authentication and authorization

### 🎨 User Interface
- Responsive design using Bootstrap 5
- Modern, clean UI with professional styling
- Interactive charts and data visualization
- Mobile-friendly interface

### 📊 Admin Features
- Movie management (add, edit, delete)
- Showtime management with ticket pricing
- Real-time dashboard with statistics
- Data visualization with Chart.js

## Technology Stack

- **Backend**: Spring Boot 3.5.6, Java 17
- **Database**: MySQL with JPA/Hibernate
- **Security**: Spring Security 6
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js
- **Build Tool**: Maven
- **IDE**: Compatible with IntelliJ IDEA, Eclipse, VS Code

## Project Structure

```
src/
├── main/
│   ├── java/com/pgno98/moviebookingsystem11/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # MVC Controllers
│   │   ├── entity/          # JPA Entities
│   │   ├── repository/      # Data repositories
│   │   ├── service/          # Business logic
│   │   └── MovieBookingSystem11Application.java
│   └── resources/
│       ├── static/
│       │   ├── css/         # Custom styles
│       │   └── js/          # JavaScript files
│       ├── templates/       # Thymeleaf templates
│       └── application.properties
└── test/                    # Test classes
```

## Database Schema

The system includes the following entities:
- **User**: Customer and admin accounts
- **Movie**: Movie information and metadata
- **Theater**: Theater locations and details
- **Showtime**: Movie showtimes and pricing
- **Seat**: Seat management and availability
- **Booking**: Customer bookings
- **Payment**: Payment processing
- **Review**: Movie reviews and ratings
- **Promotion**: Promotional codes and discounts

## Getting Started

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd movie-booking-system-11
   ```

2. **Database Setup**
   - Create a MySQL database named `movie_booking_system`
   - Update database credentials in `src/main/resources/application.properties` if needed

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Open your browser and navigate to `http://localhost:8080`
   - Default admin credentials: username: `admin`, password: `admin123`

## Usage

### For Customers
1. **Register/Login**: Create an account or login with existing credentials
2. **Browse Movies**: View available movies, search by title or filter by genre
3. **View Details**: Click on any movie to see detailed information
4. **Add Reviews**: Rate and review movies you've watched
5. **Manage Profile**: Update personal information in your profile

### For Administrators
1. **Dashboard**: View system statistics and overview
2. **Movie Management**: Add, edit, or remove movies
3. **Showtime Management**: Set up movie showtimes and ticket prices
4. **Reports**: Monitor booking trends and revenue

## Key Features Explained

### User Management
- Secure user registration with email validation
- Profile management with personal information updates
- Role-based access control (Customer/Admin)

### Review System
- 5-star rating system for movies
- Text comments and reviews
- Automatic movie rating calculation
- Review management (edit/delete)

### Admin Dashboard
- Real-time statistics display
- Interactive charts showing:
  - Movie genre distribution
  - Booking trends over time
  - Revenue analytics
- Quick action buttons for common tasks

### Movie Management
- Comprehensive movie information storage
- Poster and trailer URL support
- Genre categorization
- Release date and duration tracking
- Director and cast information

## Configuration

### Database Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/movie_booking_system
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Security Configuration
Default admin credentials can be changed in the SecurityConfig class or by updating the application properties.

## API Endpoints

### Public Endpoints
- `GET /` - Home page
- `GET /movies` - Browse movies
- `GET /movies/{id}` - Movie details
- `GET /login` - Login page
- `GET /register` - Registration page

### User Endpoints (Requires Authentication)
- `GET /profile` - User profile
- `POST /profile` - Update profile
- `GET /review/my-reviews` - User's reviews
- `POST /review/add/{movieId}` - Add review

### Admin Endpoints (Requires Admin Role)
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/movies` - Movie management
- `POST /admin/movies` - Add movie
- `GET /admin/showtimes` - Showtime management
- `POST /admin/showtimes` - Add showtime

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please contact the development team or create an issue in the repository.

## Future Enhancements

- Payment gateway integration
- Email notifications
- Mobile app development
- Advanced booking system
- Social media integration
- Recommendation engine
- Multi-language support

---

**Built with ❤️ using Spring Boot and modern web technologies**
