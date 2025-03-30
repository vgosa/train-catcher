package org.group21.trainsearch.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operator implements Serializable {

    public Operator(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @JsonIgnore
    private Long id;
    private String name;

    @JsonIgnore
    private Double balance;
    private String url;
}