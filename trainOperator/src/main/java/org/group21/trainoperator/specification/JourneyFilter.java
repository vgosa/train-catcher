package org.group21.trainoperator.specification;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class JourneyFilter {

    private String departureStation;
    private String arrivalStation;
    private LocalDateTime departureTime;
    // You can add an "isDelayed" field later if needed

    public JourneyFilter(String departureStation, String arrivalStation, LocalDateTime departureTime) {
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.departureTime = departureTime;
    }

}
