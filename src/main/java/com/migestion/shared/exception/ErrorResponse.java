package com.migestion.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error response format for all REST endpoints.
 * Provides detailed information about errors with correlation tracking.
 */
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    boolean success,
    Object data,
    ErrorDetails error,
    Instant timestamp,
    String path,
    String correlationId
) {

    /**
     * Details of the error including code, message, and field-level issues.
     */
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ErrorDetails(
        String code,
        String message,
        List<FieldError> details
    ) {
    }

    /**
     * Field-level error information for validation failures.
     */
    @Builder
    @Jacksonized
    public record FieldError(
        String field,
        String issue
    ) {
    }
}
