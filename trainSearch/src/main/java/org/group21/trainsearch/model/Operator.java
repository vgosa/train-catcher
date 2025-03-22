package org.group21.trainsearch.model;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operator implements Serializable {
    private String name; // optional, if you want to keep metadata
    private String url;
}