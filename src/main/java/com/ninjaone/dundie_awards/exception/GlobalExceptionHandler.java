package com.ninjaone.dundie_awards.exception;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ExceptionResponse> handleOptimisticLockException(OptimisticLockException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableEx(HttpMessageNotReadableException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // Catch all 500s and do not show stack trace to client
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAllOtherExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("Unexpected server error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
