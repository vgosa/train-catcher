package org.group21.trainoperator.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String departureStation;

    @Column(name = "arrival_station", nullable = false)
    @NotBlank
    private String arrivalStation;

    @Column(name = "departure_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime departureTime;

    @Column(name = "travel_time", nullable = false)
    @Min(0)
    private Integer travelTime;  // in minutes

    @Column(nullable = false)
    @Min(0)
    private Double price;

    @Column(name = "occupied_seats", nullable = false)
    @Min(0)
    private Integer occupiedSeats;

    @Column(name = "blocked_seats", nullable = false)
    @Min(0)
    private Integer blockedSeats = 0;
}