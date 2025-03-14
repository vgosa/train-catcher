package org.group21.trainoperator.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "train")
@NoArgsConstructor
//@AllArgsConstructor
@Getter
@Setter
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "seats", nullable = false)
    private Integer seats;

    public Train (String name, Integer seats) {
        this.name = name;
        this.seats = seats;
    }
}