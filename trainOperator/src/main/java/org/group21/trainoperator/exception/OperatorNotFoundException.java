package org.group21.trainoperator.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperatorNotFoundException extends RuntimeException {

    public OperatorNotFoundException(String message, Long operatorId) {
        super(message);
        log.error("Operator could not be found. \n Cause: {} \n Operator ID {}", message, operatorId);
    }
}
