package org.group21.user.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuntimeHashException extends RuntimeException {

    public RuntimeHashException(String message, Throwable cause) {
        super(message, cause);
        log.error(message, cause);
    }

}
