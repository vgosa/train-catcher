package org.group21.trainoperator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(name = "journey")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Journey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "departure_station", nullable = false)
    private String departureStation;

    @Column(name = "arrival_station", nullable = false)
    private String arrivalStation;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "travel_time", nullable = false)
    private Integer travelTime;  // in minutes

    @Column(nullable = false)
    private Double price;

    @Column(name = "occupied_seats", nullable = false)
    private Integer occupiedSeats;

    @Column(name = "blocked_seats", nullable = false)
    private Integer blockedSeats = 0;
}