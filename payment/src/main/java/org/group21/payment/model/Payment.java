package org.group21.payment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    public static final String RESOURCE_NAME = "Payment";

    public Payment(long userId, long bookingId, PaymentMethod paymentMethod, boolean isSuccess) {
        this.userId = userId;
        this.bookingId = bookingId;
        this.paymentMethod = paymentMethod;
        this.isSuccess = isSuccess;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    @Min(0)
    private long userId;

    @Column(name = "booking_id", nullable = false)
    @Min(0)
    private long bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "isSuccess", nullable = false)
    private boolean isSuccess;
}
