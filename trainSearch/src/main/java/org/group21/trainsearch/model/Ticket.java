package org.group21.trainsearch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket implements Serializable {

    public static final String RESOURCE_NAME = "Ticket";
    public Ticket(long userId, long bookingId, boolean isValid) {
        this.userId = userId;
        this.bookingId = bookingId;
        this.isValid = isValid;
    }

    private Long id;

    private Long userId;

    private Long bookingId;

    private Boolean isValid;
}
