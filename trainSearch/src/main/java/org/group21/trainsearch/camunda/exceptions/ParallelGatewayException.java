package org.group21.trainsearch.camunda.exceptions;

public class ParallelGatewayException extends RuntimeException {
    public ParallelGatewayException(String message) {
        super(message);
    }
}
