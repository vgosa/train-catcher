package org.group21.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(code=HttpStatus.UNAUTHORIZED, reason = "The provided credentials are invalid.")
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
        log.error("The provided credentials are invalid. \n Cause: {}", message);
    }
}
