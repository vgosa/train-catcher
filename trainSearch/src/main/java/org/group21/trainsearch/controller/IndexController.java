package org.group21.trainsearch.controller;

import jakarta.validation.constraints.Min;
import org.group21.trainsearch.model.Route;
import org.group21.trainsearch.service.TrainSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class IndexController {

    private final TrainSearchService trainSearchService;

    public IndexController(TrainSearchService trainSearchService) {
        this.trainSearchService = trainSearchService;
    }

    @GetMapping("/")
    public String showIndex() {
        return "search";
    }

    @GetMapping("/routes")
    public String showRoutes(Model model,
                             @RequestParam("departure_station") String departureStation,
                             @RequestParam("arrival_station") String arrivalStation,
                             @RequestParam("departure_time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
                             @RequestParam(value = "max_changes", defaultValue = "2") @Min(value = 0) int maxChanges) {
        List<Route> routes = trainSearchService.searchRoutes(departureStation, arrivalStation, departureTime, maxChanges);
        model.addAttribute("routes", routes);
        return "routes";
    }
}
