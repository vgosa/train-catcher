package org.group21.booking.model;

import jakarta.persistence.*;
import lombok.*;
import org.group21.booking.model.converter.RouteAttributeConverter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "booking")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Booking {

    public Booking (Long userId, Route route) {
        this.userId = userId;
        this.route = route;
        this.price = route.getTotalPrice();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Convert(converter = RouteAttributeConverter.class)
    @Column(name = "route", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Route route;

    @Column(name = "price", nullable = false)
    private Double price;
}
