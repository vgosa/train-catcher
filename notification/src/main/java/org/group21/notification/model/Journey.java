package org.group21.notification.model;
import lombok.*;

import java.time.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Journey {
    private Long id;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime departureTime;
    private Integer travelTime;
    private Double price;
    private Operator operator;
}