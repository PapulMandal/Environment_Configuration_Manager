package com.environment.manager.exception;

import java.util.List;

public class ValidationException extends Exception {
    private final List<String> validationErrors;

    public ValidationException(String message) {
        super(message);
        this.validationErrors = null;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = null;
    }

    public ValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public ValidationException(List<String> validationErrors) {
        super("Validation failed: " + String.join(", ", validationErrors));
        this.validationErrors = validationErrors;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    // Factory method for environment validation errors
    public static ValidationException forEnvironment(String environmentId, List<String> errors) {
        return new ValidationException("Environment '" + environmentId + "' validation failed", errors);
    }

    public ValidationException(String objectType, String objectId, List<String> validationErrors) {
        super(objectType + " '" + objectId + "' validation failed");
        this.validationErrors = validationErrors;
    }
}