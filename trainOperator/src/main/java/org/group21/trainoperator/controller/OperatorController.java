package org.group21.trainoperator.controller;

import org.group21.trainoperator.exception.OperatorNotFoundException;
import org.group21.trainoperator.model.Operator;
import org.group21.trainoperator.service.OperatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/operator")
public class OperatorController {
    private final OperatorService operatorService;

    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @GetMapping
    public ResponseEntity<List<Operator>> getOperators() {
        List<Operator> operators = operatorService.getAllOperators();
        return ResponseEntity.ok(operators);
    }

    @PutMapping("/{operatorId}")
    public ResponseEntity<Operator> updateOperator(@RequestBody Operator operator,
                                                   @PathVariable("operatorId") Long operatorId) {
        try {
            Operator updatedOperator = operatorService.updateOperator(operatorId, operator);
            return ResponseEntity.ok(updatedOperator);
        } catch (OperatorNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{operatorName}/topup")
    public ResponseEntity<Operator> topUpBalance(@PathVariable("operatorName") String operatorName,
                                                 @RequestBody Double amount) {
        Random random = new Random();
        if (random.nextBoolean()) throw new RuntimeException("Random failure");
        return operatorService.topUpBalance(operatorName, amount)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/{operatorName}/deduct")
    public ResponseEntity<Operator> deductBalance(@PathVariable("operatorName") String operatorName,
                                                  @RequestBody Double amount) {
        return operatorService.deductBalance(operatorName, amount)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
