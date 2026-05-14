package com.newdawn.tagvote.global.error;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            final ResponseStatusException exception,
            final HttpServletRequest request
    ) {
        String message = RequestLogSupport.fallbackMessage(exception.getReason(), "Request failed");
        return buildResponse(request, exception.getStatusCode(), message, exception, false);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception,
            final HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        message = RequestLogSupport.fallbackMessage(message, "Validation failed");
        return buildResponse(request, HttpStatus.BAD_REQUEST, message, exception, false);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException exception,
            final HttpServletRequest request
    ) {
        return buildResponse(request, HttpStatus.BAD_REQUEST, "Request body is invalid", exception, false);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            final IllegalArgumentException exception,
            final HttpServletRequest request
    ) {
        String message = RequestLogSupport.fallbackMessage(exception.getMessage(), "Invalid request");
        return buildResponse(request, HttpStatus.BAD_REQUEST, message, exception, false);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            final Exception exception,
            final HttpServletRequest request
    ) {
        return buildResponse(
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                exception,
                true
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            final HttpServletRequest request,
            final HttpStatusCode statusCode,
            final String message,
            final Exception exception,
            final boolean includeStackTrace
    ) {
        String logMessage = RequestLogSupport.errorLogMessage(request, statusCode.value(), message);
        if (includeStackTrace) {
            log.error(logMessage, exception);
        } else {
            log.error(logMessage);
        }

        return ResponseEntity.status(statusCode).body(ApiErrorResponse.of(statusCode, message, request));
    }

    private String formatFieldError(final FieldError fieldError) {
        String defaultMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("is invalid");
        return fieldError.getField() + " " + defaultMessage;
    }
}
