package org.group21.trainoperator.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(code= HttpStatus.NOT_FOUND, reason="Operator not found")
public class OperatorNotFoundException extends RuntimeException {

    public OperatorNotFoundException(String message, Long operatorId) {
        super(message);
        log.error("Operator could not be found. \n Cause: {} \n Operator ID {}", message, operatorId);
    }

    public OperatorNotFoundException(String message, String operatorName) {
        super(message);
        log.error("Operator could not be found. \n Cause: {} \n Operator Name {}", message, operatorName);
    }
}
