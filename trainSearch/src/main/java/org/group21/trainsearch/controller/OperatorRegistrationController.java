package org.group21.trainsearch.controller;

import jakarta.persistence.EntityNotFoundException;
import org.group21.trainsearch.service.*;
import org.group21.trainsearch.model.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/operators")
public class OperatorRegistrationController {

    private final OperatorService operatorService;

    public OperatorRegistrationController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerOperator(@RequestBody Operator operator) {
        operatorService.registerOperator(operator);
        return ResponseEntity.ok("Operator registered successfully");
    }

    @GetMapping
    public ResponseEntity<List<Operator>> getAllOperators() {
        return ResponseEntity.ok(operatorService.getOperators());
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeOperator(@RequestParam("name") String name,
                                                 @RequestParam("url") String url) {
        boolean removed = operatorService.removeOperator(name, url);
        if (removed) {
            return ResponseEntity.noContent().build();
        } else {
            throw new EntityNotFoundException("Operator not found");
        }
    }
}