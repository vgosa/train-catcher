package org.group21.trainsearch.model;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
public class Operator {
    private String name; // optional, if you want to keep metadata
    private String url;
}