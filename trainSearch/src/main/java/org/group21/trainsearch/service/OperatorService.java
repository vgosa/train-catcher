package org.group21.trainsearch.service;

import lombok.*;
import org.group21.trainsearch.model.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;

@Getter
@Service
public class OperatorService {

    private static final String OPERATOR_SERVICE_ID = "trainOperator";

    public static final String OPERATOR_JOURNEY_ENDPOINT = "/journey";

    private final List<Operator> operators = new CopyOnWriteArrayList<>(
            Arrays.asList(
                    new Operator("NS", String.format("http://%s%d", OPERATOR_SERVICE_ID, 1)),
                    new Operator("Arriva", String.format("http://%s%d", OPERATOR_SERVICE_ID, 2))
            )
    );

    public void registerOperator(Operator operator) {
        operators.add(operator);
    }

    public boolean removeOperator(String name, String url) {
        return operators.removeIf(op -> op.getName().equals(name) && op.getUrl().equals(url));
    }

}