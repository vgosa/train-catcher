package org.group21.error;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.group21.exception.StateConflictException;
import org.group21.exception.UnauthenticatedException;
import org.group21.exception.UnauthorizedException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String ERROR_MESSAGE_400 = "400 Error";
    public static final String ERROR_MESSAGE_401 = "401 Error";
    public static final String ERROR_MESSAGE_403 = "403 Error";
    public static final String ERROR_MESSAGE_404 = "404 Error";
    public static final String ERROR_MESSAGE_409 = "409 Error";
    public static final String ERROR_MESSAGE_500 = "500 Error";

    public RestControllerExceptionHandler() {
        super();
    }

    // 400

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex, final WebRequest request) {
        log.debug(ERROR_MESSAGE_400, ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug(ERROR_MESSAGE_400, ex);
        return handleExceptionInternal(ex, ex.getMessage(), headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.debug(ERROR_MESSAGE_400, ex);
        return handleExceptionInternal(ex, ex.getMessage(), headers, HttpStatus.BAD_REQUEST, request);
    }

    // 401

    @ExceptionHandler(value = {HttpClientErrorException.Unauthorized.class, UnauthenticatedException.class})
    protected ResponseEntity<Object> handleUnauthenticated(final RuntimeException ex, final WebRequest request) {
        log.debug(ERROR_MESSAGE_401, ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    // 403

    @ExceptionHandler(value = {HttpClientErrorException.Forbidden.class, UnauthorizedException.class})
    protected ResponseEntity<Object> handleForbidden(final RuntimeException ex, final WebRequest request) {
        log.debug(ERROR_MESSAGE_403, ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    // 404

    @ExceptionHandler(value = EntityNotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(final RuntimeException ex, final WebRequest request) {
        log.debug(ERROR_MESSAGE_404, ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }


    // 409

    @ExceptionHandler(value = {InvalidDataAccessApiUsageException.class, StateConflictException.class, HttpClientErrorException.Conflict.class})
    protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
        log.debug(ERROR_MESSAGE_409, ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    // 500
    @ExceptionHandler(value = {IllegalStateException.class, IllegalArgumentException.class, NullPointerException.class})
    protected ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
        log.error(ERROR_MESSAGE_500, ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
