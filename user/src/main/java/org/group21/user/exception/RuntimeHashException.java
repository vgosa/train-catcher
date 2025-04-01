package org.group21.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(code= HttpStatus.INTERNAL_SERVER_ERROR, reason = "Something went wrong!")
public class RuntimeHashException extends RuntimeException {

    public RuntimeHashException(String message, Throwable cause) {
        super(message, cause);
        log.error(message, cause);
    }

}
