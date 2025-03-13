package org.group21.trainoperator.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/route")
public class RouteController {

    @GetMapping
    public ResponseEntity<List<Object>> findRoutes(
            @RequestParam("departure_station") String departureStation,
            @RequestParam("arrival_station") String arrivalStation,
            @RequestParam("departure_time") String departureTime,
            @RequestParam(value = "fetchDelayed", required = false, defaultValue = "false") Boolean fetchDelayed) {
        // TODO: Implement logic to create and return a list of routes based on journeys
        return ResponseEntity.ok().build();
    }
}