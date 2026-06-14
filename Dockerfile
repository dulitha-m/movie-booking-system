# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# Dynamically replace hardcoded DB values with environment variables during build time
RUN sed -i 's|config.setJdbcUrl("jdbc:mysql://localhost:3306/movie_booking_system?createDatabaseIfNotExist=true\&useSSL=false\&serverTimezone=UTC\&allowPublicKeyRetrieval=true");|config.setJdbcUrl(System.getenv("SPRING_DATASOURCE_URL") != null ? System.getenv("SPRING_DATASOURCE_URL") : "jdbc:mysql://localhost:3306/movie_booking_system?createDatabaseIfNotExist=true\&useSSL=false\&serverTimezone=UTC\&allowPublicKeyRetrieval=true");|' src/main/java/com/pgno98/moviebookingsystem11/config/DatabaseConfig.java && \
    sed -i 's|config.setUsername("root");|config.setUsername(System.getenv("SPRING_DATASOURCE_USERNAME") != null ? System.getenv("SPRING_DATASOURCE_USERNAME") : "root");|' src/main/java/com/pgno98/moviebookingsystem11/config/DatabaseConfig.java && \
    sed -i 's|config.setPassword("Dulitha@23213");|config.setPassword(System.getenv("SPRING_DATASOURCE_PASSWORD") != null ? System.getenv("SPRING_DATASOURCE_PASSWORD") : "Dulitha@23213");|' src/main/java/com/pgno98/moviebookingsystem11/config/DatabaseConfig.java
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/movie-booking-system-11-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

