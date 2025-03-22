package org.group21.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operator implements Serializable {
    private String name; // optional, if you want to keep metadata
    private String url;
}