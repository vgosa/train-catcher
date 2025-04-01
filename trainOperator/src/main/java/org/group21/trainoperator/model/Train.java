package org.group21.trainoperator.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "train")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;

    @Column(name = "seats", nullable = false)
    @Min(0)
    private Integer seats;

    public Train (String name, Integer seats) {
        this.name = name;
        this.seats = seats;
    }
}