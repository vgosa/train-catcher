package org.group21.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operator {
    private String name; // optional, if you want to keep metadata
    private String url;
}