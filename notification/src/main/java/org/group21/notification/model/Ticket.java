package org.group21.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private String passenger;
    private String email;
    private List<Journey> journeys;
    private double totalPrice;
    private int totalDuration;
}
