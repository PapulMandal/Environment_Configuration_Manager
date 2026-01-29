package com.environment.manager.util.util;

import com.environment.manager.exception.ValidationException;
import com.environment.manager.model.Environment;
import com.environment.manager.model.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class containing common validation logic.
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Utility class - prevent instantiation
    }

    // Validation patterns
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9-_]{2,49}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9\\s-_]{2,99}$");
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+(-[A-Za-z0-9]+)?$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * Validates an environment ID.
     */
    public static void validateEnvironmentId(String id) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (id == null || id.trim().isEmpty()) {
            errors.add("Environment ID cannot be null or empty");
        } else {
            if (id.length() < 3 || id.length() > 50) {
                errors.add("Environment ID must be between 3 and 50 characters");
            }
            if (!ID_PATTERN.matcher(id).matches()) {
                errors.add("Environment ID can only contain letters, numbers, hyphens, and underscores");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Environment ID validation failed", errors);
        }
    }

    /**
     * Validates an environment name.
     */
    public static void validateEnvironmentName(String name) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (name == null || name.trim().isEmpty()) {
            errors.add("Environment name cannot be null or empty");
        } else {
            if (name.length() < 3 || name.length() > 100) {
                errors.add("Environment name must be between 3 and 100 characters");
            }
            if (!NAME_PATTERN.matcher(name).matches()) {
                errors.add("Environment name contains invalid characters");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Environment name validation failed", errors);
        }
    }

    /**
     * Validates a URL string.
     */
    public static void validateUrl(String url, String fieldName) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (url == null || url.trim().isEmpty()) {
            errors.add(fieldName + " cannot be null or empty");
        } else {
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                errors.add("Invalid URL format for " + fieldName + ": " + url);
            }

            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                errors.add(fieldName + " must start with https:// or http://");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(fieldName + " validation failed", errors);
        }
    }

    /**
     * Validates a version string.
     */
    public static void validateVersion(String version) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (version == null || version.trim().isEmpty()) {
            errors.add("Version cannot be null or empty");
        } else if (!VERSION_PATTERN.matcher(version).matches()) {
            errors.add("Version must follow semantic versioning (e.g., 1.2.3 or 1.2.3-beta)");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Version validation failed", errors);
        }
    }

    /**
     * Validates an email address.
     */
    public static void validateEmail(String email) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (email == null || email.trim().isEmpty()) {
            errors.add("Email cannot be null or empty");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("Invalid email format: " + email);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Email validation failed", errors);
        }
    }

    /**
     * Validates that an object is not null.
     */
    public static void validateNotNull(Object obj, String fieldName) throws ValidationException {
        if (obj == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }

    /**
     * Validates that a string is not blank.
     */
    public static void validateNotBlank(String str, String fieldName) throws ValidationException {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be blank");
        }
    }

    /**
     * Validates that a number is positive.
     */
    public static void validatePositive(int number, String fieldName) throws ValidationException {
        if (number <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }

    /**
     * Validates that a number is within a range.
     */
    public static void validateRange(int number, int min, int max, String fieldName) throws ValidationException {
        if (number < min || number > max) {
            throw new ValidationException(fieldName + " must be between " + min + " and " + max);
        }
    }

    /**
     * Validates an entire environment object.
     */
    public static List<String> validateEnvironment(Environment environment) {
        List<String> errors = new ArrayList<>();

        try {
            validateEnvironmentId(environment.getId());
        } catch (ValidationException e) {
            if (e.getValidationErrors() != null) {
                errors.addAll(e.getValidationErrors());
            } else {
                errors.add(e.getMessage());
            }
        }

        try {
            validateEnvironmentName(environment.getName());
        } catch (ValidationException e) {
            if (e.getValidationErrors() != null) {
                errors.addAll(e.getValidationErrors());
            } else {
                errors.add(e.getMessage());
            }
        }

        try {
            validateUrl(environment.getBaseUrl(), "Base URL");
        } catch (ValidationException e) {
            if (e.getValidationErrors() != null) {
                errors.addAll(e.getValidationErrors());
            } else {
                errors.add(e.getMessage());
            }
        }

        if (environment.getType() == null) {
            errors.add("Environment type cannot be null");
        }

        // Validate all services in the environment
        if (environment.getServices() != null) {
            for (Service service : environment.getServices()) {
                List<String> serviceErrors = validateService(service);
                if (!serviceErrors.isEmpty()) {
                    errors.add("Service '" + service.getName() + "': " + String.join("; ", serviceErrors));
                }
            }
        }

        return errors;
    }

    /**
     * Validates a service object.
     */
    public static List<String> validateService(Service service) {
        List<String> errors = new ArrayList<>();

        if (service == null) {
            errors.add("Service cannot be null");
            return errors;
        }

        if (service.getId() == null || service.getId().trim().isEmpty()) {
            errors.add("Service ID cannot be null or empty");
        } else if (service.getId().length() > 100) {
            errors.add("Service ID cannot exceed 100 characters");
        }

        if (service.getName() == null || service.getName().trim().isEmpty()) {
            errors.add("Service name cannot be null or empty");
        } else if (service.getName().length() > 200) {
            errors.add("Service name cannot exceed 200 characters");
        }

        try {
            validateVersion(service.getVersion());
        } catch (ValidationException e) {
            if (e.getValidationErrors() != null) {
                errors.addAll(e.getValidationErrors());
            } else {
                errors.add(e.getMessage());
            }
        }

        if (service.getType() == null) {
            errors.add("Service type cannot be null");
        }

        return errors;
    }

    /**
     * Validates multiple conditions and throws a single ValidationException if any fail.
     */
    public static void validateAll(ValidationCheck... checks) throws ValidationException {
        List<String> errors = new ArrayList<>();

        for (ValidationCheck check : checks) {
            try {
                check.validate();
            } catch (ValidationException e) {
                if (e.getValidationErrors() != null) {
                    errors.addAll(e.getValidationErrors());
                } else {
                    errors.add(e.getMessage());
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Multiple validation errors occurred", errors);
        }
    }

    /**
     * Functional interface for validation checks.
     */
    @FunctionalInterface
    public interface ValidationCheck {
        void validate() throws ValidationException;
    }
}