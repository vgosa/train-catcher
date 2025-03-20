package org.group21.booking.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.group21.booking.model.Route;

@Slf4j
@Converter
public class RouteAttributeConverter implements AttributeConverter<Route, String> {

    private final ObjectMapper objectMapper;
    
    public RouteAttributeConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(Route route) {
        try {
            return objectMapper.writeValueAsString(route);
        } catch (JsonProcessingException e) {
            log.error("Could not convert the route object into JSON!", e);
            return null;
        }
    }

    @Override
    public Route convertToEntityAttribute(String value) {
        try {
            return objectMapper.readValue(value, Route.class);
        } catch (JsonProcessingException e) {
            log.error("Could not convert the provided string object into a Route entity", e);
            return null;
        }
    }
}
