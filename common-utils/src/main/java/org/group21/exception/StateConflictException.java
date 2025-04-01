package org.group21.exception;

public class StateConflictException extends RuntimeException {

    public StateConflictException(String message) {
        super(message);
    }

    public StateConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateConflictException(Throwable cause) {
        super(cause);
    }
    public StateConflictException() {
        super();
    }

}
