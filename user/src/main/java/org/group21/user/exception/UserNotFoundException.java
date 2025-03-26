package org.group21.user.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message, Long userId) {
        super(message);
        log.error("User could not be found. \n Cause: {} \n User ID {}", message, userId);
    }

    public UserNotFoundException(String message, String email) {
        super(message);
        log.error("User could not be found. \n Cause: {} \n User Email {}", message, email);
    }
}
