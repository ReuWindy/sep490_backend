package com.fpt.sep490.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> handleAllExceptions(Exception ex) {
        ApiExceptionResponse error = new ApiExceptionResponse(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiExceptionResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getRootCause();

        if (cause instanceof JsonParseException) {
            JsonParseException jpe = (JsonParseException) cause;
            String message = jpe.getOriginalMessage();
            // Extract the unexpected character from the message
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
}
