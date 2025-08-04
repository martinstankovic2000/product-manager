package com.ingemark.productmanager.exception;

import com.ingemark.productmanager.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler that intercepts various application exceptions
 * and returns appropriate HTTP status codes and localized error messages
 * as JSON responses.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;

    /**
     * Handles ProductNotFoundException and returns a 404 Not Found response.
     *
     * @param ex the ProductNotFoundException thrown
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        String message = messageService.getMessage(ex.getMessage(), ex.getMessageArgs());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                message,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles UsernameInUseException and returns a 400 Bad Request response.
     *
     * @param ex the UsernameInUseException thrown
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(UsernameInUseException.class)
    public ResponseEntity<ErrorResponse> handleUsernameInUse(UsernameInUseException ex) {
        String message = messageService.getMessage(ex.getMessage(), ex.getMessageArgs());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles EmailInUseException and returns a 400 Bad Request response.
     *
     * @param ex the EmailInUseException thrown
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(EmailInUseException.class)
    public ResponseEntity<ErrorResponse> handleEmailInUse(EmailInUseException ex) {
        String message = messageService.getMessage(ex.getMessage(), ex.getMessageArgs());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles UserNotFoundException and returns a 404 Not Found response.
     *
     * @param ex the UserNotFoundException thrown
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        String message = messageService.getMessage(ex.getMessage(), ex.getMessageArgs());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                message,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles BadCredentialsException and returns a 401 Unauthorized response.
     *
     * @param ex the BadCredentialsException thrown
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(BadCredentialsException ex) {
        String message = messageService.getMessage("bad.credentials");

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                message,
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
