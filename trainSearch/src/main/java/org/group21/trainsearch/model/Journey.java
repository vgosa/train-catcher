package org.group21.trainsearch.model;

import lombok.*;

import java.io.Serializable;
import java.time.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Journey implements Serializable {
    private Long id;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime departureTime;
    private Integer travelTime;
    private Double price;
    private Operator operator;
}