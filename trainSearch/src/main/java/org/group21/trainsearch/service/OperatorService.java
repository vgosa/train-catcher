package org.group21.trainsearch.service;

import lombok.*;
import org.group21.trainsearch.model.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;

@Getter
@Service
public class OperatorService {
    private final List<Operator> operators = new CopyOnWriteArrayList<>(
            Arrays.asList(
                    new Operator("Operator 1", "http://localhost:8090"),
                    new Operator("Operator 2", "http://localhost:8091")
            )
    );

    public void registerOperator(Operator operator) {
        operators.add(operator);
    }

    public boolean removeOperator(String name, String url) {
        return operators.removeIf(op -> op.getName().equals(name) && op.getUrl().equals(url));
    }

}