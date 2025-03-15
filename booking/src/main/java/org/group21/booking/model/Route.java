package org.group21.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @JsonProperty("journeys")
    private List<Journey> journeys;

    @JsonProperty("totalPrice")
    private double totalPrice;

    @JsonProperty("totalDuration")
    private int totalDuration; // minutes

    public Route(List<Journey> journeys) {
        this.journeys = journeys;
        this.totalDuration = journeys.stream()
                .mapToInt(Journey::getTravelTime)
                .sum();
        this.totalPrice = journeys.stream()
                .mapToDouble(Journey::getPrice)
                .sum();
    }
}