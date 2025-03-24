package org.group21.user.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
        log.error("The provided credentials are invalid. \n Cause: {}", message);
    }
}
