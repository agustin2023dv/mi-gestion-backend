package com.migestion.shared.exception;

/**
 * Exception thrown when a business rule is violated.
 * Results in HTTP 409 Conflict.
 * Examples: duplicate SKU, invalid state transition, constraint violation.
 */
public class BusinessRuleViolationException extends RuntimeException {
    private final String code;

    public BusinessRuleViolationException(String message) {
        super(message);
        this.code = "BUSINESS_RULE_VIOLATION";
    }

    public BusinessRuleViolationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
