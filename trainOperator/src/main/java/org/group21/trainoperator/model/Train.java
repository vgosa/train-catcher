package org.group21.trainoperator.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "train")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Train {
    @Id
    private Integer id;

    private String name;

    private Integer seats;
}