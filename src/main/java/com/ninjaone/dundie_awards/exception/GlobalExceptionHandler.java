package com.ninjaone.dundie_awards.exception;

import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.info("Handled ResourceNotFoundException: \n {} \n {}", ex.getMessage(), ex.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ExceptionResponse> handleOptimisticLockException(OptimisticLockException ex) {
        logger.error("Handled OptimisticlockException: \n {} \n {}", ex.getMessage(), ex.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse(
                        ex.getMessage(),
                        HttpStatus.CONFLICT.value()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableEx(HttpMessageNotReadableException ex) {
        logger.error("Handled HttpMessageNotReadableException: \n {} \n {}", ex.getMessage(), ex.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(
                        ex.getMessage(),
                        HttpStatus.BAD_REQUEST.value()));
    }

    // Catch all 500s and do not show stack trace to client
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllOtherExceptions(Exception ex) {
        logger.error("Handled Exception: \n {} \n {}", ex.getMessage(), ex.getStackTrace());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        "Unexpected server error occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
