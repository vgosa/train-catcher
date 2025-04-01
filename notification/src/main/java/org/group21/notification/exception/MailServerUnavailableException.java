package org.group21.notification.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when the mail server is not reachable.
 */
@Slf4j
public class MailServerUnavailableException extends RuntimeException {

    public MailServerUnavailableException(String message) {
        super(message);
        log.warn(message);
    }

    public MailServerUnavailableException(String message, Throwable cause) {
        super(message, cause);
        log.warn(message, cause);
    }

    public MailServerUnavailableException(Throwable cause) {
        super(cause);
        log.warn(cause.getMessage(), cause);
    }

    public MailServerUnavailableException() {
        super();
        log.warn("Mail server is not reachable.");
    }
}
