package org.group21.trainsearch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Train implements Serializable {
    private Long id;

    private String name;

    private Integer seats;

    public Train(String name, Integer seats) {
        this.name = name;
        this.seats = seats;
    }
}