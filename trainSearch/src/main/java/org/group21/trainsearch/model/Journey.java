package org.group21.trainsearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Journey implements Serializable {
    private Long id;
    private Train train;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime departureTime;
    private Integer travelTime;
    private Double price;
    private Integer occupiedSeats;
    private Integer blockedSeats;
    private Operator operator;
}