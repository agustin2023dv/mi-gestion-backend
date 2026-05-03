package com.migestion.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Global exception handler for all REST controllers.
 * Converts various exceptions into standardized ErrorResponse format.
 * Logs all errors with correlation IDs for tracing.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException (404).
     * Resource does not exist.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
        ResourceNotFoundException ex,
        WebRequest request
    ) {
        String correlationId = extractCorrelationId(request);
        
        log.warn(
            "Resource not found: {} | path: {} | correlationId: {}",
            ex.getMessage(),
            request.getDescription(false),
            correlationId
        );

        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .data(null)
            .error(ErrorResponse.ErrorDetails.builder()
                .code("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .details(null)
                .build())
            .timestamp(Instant.now())
            .path(getRequestPath(request))
            .correlationId(correlationId)
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle BusinessRuleViolationException (409).
     * Business rule constraint violated (duplicate, invalid state, etc.).
     */
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(
        BusinessRuleViolationException ex,
        WebRequest request
    ) {
        String correlationId = extractCorrelationId(request);
        
        log.warn(
            "Business rule violation [{}]: {} | path: {} | correlationId: {}",
            ex.getCode(),
            ex.getMessage(),
            request.getDescription(false),
            correlationId
        );

        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .data(null)
            .error(ErrorResponse.ErrorDetails.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .details(null)
                .build())
            .timestamp(Instant.now())
            .path(getRequestPath(request))
            .correlationId(correlationId)
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle MethodArgumentNotValidException (400).
     * Request validation failed. Returns field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        WebRequest request
    ) {
        String correlationId = extractCorrelationId(request);
        
        log.warn(
            "Validation failed: {} errors | path: {} | correlationId: {}",
            ex.getBindingResult().getErrorCount(),
            request.getDescription(false),
            correlationId
        );

        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.add(ErrorResponse.FieldError.builder()
                .field(error.getField())
                .issue(error.getDefaultMessage())
                .build())
        );

        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .data(null)
            .error(ErrorResponse.ErrorDetails.builder()
                .code("VALIDATION_FAILED")
                .message("Request validation failed")
                .details(fieldErrors)
                .build())
            .timestamp(Instant.now())
            .path(getRequestPath(request))
            .correlationId(correlationId)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle all other unexpected exceptions (500).
     * Logs full stacktrace but returns only safe details in response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
        Exception ex,
        WebRequest request
    ) {
        String correlationId = extractCorrelationId(request);
        
        log.error(
            "Unhandled exception occurred | path: {} | correlationId: {}",
            request.getDescription(false),
            correlationId,
            ex
        );

        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .data(null)
            .error(ErrorResponse.ErrorDetails.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred: " + ex.getMessage())
                .details(null)
                .build())
            .timestamp(Instant.now())
            .path(getRequestPath(request))
            .correlationId(correlationId)
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Extract correlation ID from request headers or generate a new one.
     */
    private String extractCorrelationId(WebRequest request) {
        String correlationId = request.getHeader("X-Correlation-Id");
        return correlationId != null ? correlationId : UUID.randomUUID().toString();
    }

    /**
     * Extract path from request, removing "request=" prefix added by WebRequest.
     */
    private String getRequestPath(WebRequest request) {
        String description = request.getDescription(false);
        return description.replace("request=", "");
    }
}
