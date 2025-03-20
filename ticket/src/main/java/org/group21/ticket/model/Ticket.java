package org.group21.ticket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    public static final String RESOURCE_NAME = "Ticket";
    public Ticket(long userId, long bookingId, boolean isValid) {
        this.userId = userId;
        this.bookingId = bookingId;
        this.isValid = isValid;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "isValid", nullable = false)
    private Boolean isValid;
}
