package org.group21.trainsearch.model;

import lombok.*;

import java.util.*;

@Getter
@Setter
public class Route {
    private List<Journey> journeys;
    private double totalPrice;
    private int totalDuration;

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