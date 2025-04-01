package org.group21.trainoperator.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "operator")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank
    private String name;

    @Column(name = "balance", nullable = false)
    @Min(0)
    private Double balance = 0.0d;
}
