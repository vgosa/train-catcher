package org.group21.trainoperator.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/train")
public class TrainController {

    @GetMapping
    public ResponseEntity<List<Object>> getTrains() {
        // TODO: Retrieve and return all trains
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Object> addTrain(@RequestBody Object train) {
        // TODO: Add a new train and return the created train object
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{trainId}")
    public ResponseEntity<Object> getTrainById(@PathVariable("trainId") Long trainId) {
        // TODO: Retrieve and return a single train based on trainId
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{trainId}")
    public ResponseEntity<Object> updateTrain(@PathVariable("trainId") Long trainId,
                                              @RequestBody Object train) {
        // TODO: Update and return the train with the given trainId
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{trainId}")
    public ResponseEntity<Void> deleteTrain(@PathVariable("trainId") Long trainId) {
        // TODO: Delete the train with the given trainId
        return ResponseEntity.ok().build();
    }
}