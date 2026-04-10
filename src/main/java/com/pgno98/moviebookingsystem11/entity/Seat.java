package com.pgno98.moviebookingsystem11.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;
    
    @Column(name = "theater_id", nullable = false)
    private Long theaterId;
    
    @Column(name = "seat_number", nullable = false)
    private String seatNumber;
    
    @Column(name = "seat_row")
    private String rowNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType seatType = SeatType.STANDARD;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.AVAILABLE;
    
    public enum SeatType {
        STANDARD, PREMIUM, VIP
    }
    
    public enum Status {
        AVAILABLE, OCCUPIED, MAINTENANCE
    }
}
