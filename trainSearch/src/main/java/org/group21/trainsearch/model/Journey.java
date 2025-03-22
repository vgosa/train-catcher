package org.group21.trainsearch.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.*;

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
    private Operator operator;
}