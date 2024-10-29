package com.fpt.sep490.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiExceptionResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        String errorMessage = Stream.concat(
                        ex.getBindingResult().getFieldErrors().stream().map(error -> String.format("Field %s: %s", error.getField(),  error.getDefaultMessage())),
                        ex.getBindingResult().getGlobalErrors().stream().map(globalEr -> String.format("Object %s: %s", globalEr.getObjectName(), globalEr.getDefaultMessage())))
                .collect(Collectors.joining(", "));

        ApiExceptionResponse error = new ApiExceptionResponse(
                errorMessage,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiExceptionResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof JsonParseException) {
            JsonParseException jpe = (JsonParseException) cause;
            String message = jpe.getOriginalMessage();
            String unexpectedCharacter = message.substring(message.indexOf('\''), message.indexOf('\'', message.indexOf('\'') + 1) + 1);
            ApiExceptionResponse error = new ApiExceptionResponse(
                    "JSON parse error: Unexpected character " + unexpectedCharacter,
                    HttpStatus.BAD_REQUEST,
                    LocalDateTime.now());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        ApiExceptionResponse error = new ApiExceptionResponse(
                cause.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> String.format("Field %s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining("    "));

        ApiExceptionResponse error = new ApiExceptionResponse(
                errorMessage,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiExceptionResponse> handleRuntimeExceptions(RuntimeException ex) {
        ApiExceptionResponse error = new ApiExceptionResponse(
                "Runtime exception: " +ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
