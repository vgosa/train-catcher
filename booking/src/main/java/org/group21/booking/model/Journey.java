package org.group21.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Journey {
    private Long id;
    private String departureStation;
    private String arrivalStation;
    //TODO: Add custom date-time serializer/deserializer to avoid errors on ISO-compliant timestamps with timezones
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", lenient = OptBoolean.TRUE)
    private LocalDateTime departureTime;
    private Integer travelTime;
    private Double price;
    private Operator operator;
}