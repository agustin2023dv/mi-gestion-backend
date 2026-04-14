package com.migestion.shared.exception;

/**
 * Exception thrown when a requested resource does not exist.
 * Results in HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final Object resourceId;

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.resourceId = null;
    }

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s with id %s not found", resourceName, resourceId));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Object getResourceId() {
        return resourceId;
    }
}
