package com.environment.manager.validator;

import com.environment.manager.model.*;
import com.environment.manager.exception.ValidationException;
import com.environment.manager.util.util.ValidationUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Validator for environment configurations and health checks.
 */
public class EnvironmentValidator {
    private final ExecutorService executorService;
    private final int timeoutSeconds;

    public EnvironmentValidator() {
        this.executorService = Executors.newFixedThreadPool(5);
        this.timeoutSeconds = 30;
    }

    public EnvironmentValidator(int threadPoolSize, int timeoutSeconds) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Performs comprehensive validation of an environment.
     */
    public ValidationResult validateEnvironment(Environment environment) {
        List<ValidationCheck> checks = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Basic validation
        checks.add(() -> validateBasicProperties(environment));

        // URL validation
        checks.add(() -> validateUrls(environment));

        // Service validation
        checks.add(() -> validateServices(environment));

        // Health check
        checks.add(() -> performHealthCheck(environment));

        // Configuration validation
        checks.add(() -> validateConfiguration(environment));

        // Execute all checks
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

        // Perform additional checks that might generate warnings
        warnings.addAll(performWarningChecks(environment));

        return new ValidationResult(environment.getId(), environment.getName(),
                errors, warnings, errors.isEmpty());
    }

    /**
     * Validates basic environment properties.
     */
    private void validateBasicProperties(Environment environment) throws ValidationException {
        List<String> errors = new ArrayList<>();

        try {
            ValidationUtils.validateEnvironmentId(environment.getId());
        } catch (ValidationException e) {
            if (e.getValidationErrors() != null) {
                errors.addAll(e.getValidationErrors());
            } else {
                errors.add(e.getMessage());
            }
        }

        try {
            ValidationUtils.validateEnvironmentName(environment.getName());
        } catch (ValidationException e) {
            if (e.getValidationErrors() != null) {
                errors.addAll(e.getValidationErrors());
            } else {
                errors.add(e.getMessage());
            }
        }

        try {
            ValidationUtils.validateUrl(environment.getBaseUrl(), "Base URL");
        } catch (ValidationException e) {
            if (e.getValidationErrors() != null) {
                errors.addAll(e.getValidationErrors());
            } else {
                errors.add(e.getMessage());
            }
        }

        if (environment.getType() == null) {
            errors.add("Environment type must be specified");
        }

        if (!errors.isEmpty()) {
            // Use constructor: ValidationException(String message, List<String> validationErrors)
            throw new ValidationException("Basic properties validation failed", errors);
        }
    }

    /**
     * Validates all URLs in the environment.
     */
    private void validateUrls(Environment environment) throws ValidationException {
        List<String> errors = new ArrayList<>();

        // Validate base URL
        try {
            ValidationUtils.validateUrl(environment.getBaseUrl(), "Base URL");
        } catch (ValidationException e) {
            if (e.getValidationErrors() != null) {
                errors.addAll(e.getValidationErrors());
            } else {
                errors.add(e.getMessage());
            }
        }

        // Validate database URL if present
        if (environment.getDatabaseUrl() != null && !environment.getDatabaseUrl().isEmpty()) {
            try {
                // Check if it's a valid database URL format
                if (!environment.getDatabaseUrl().startsWith("jdbc:")) {
                    errors.add("Database URL should start with 'jdbc:'");
                }
            } catch (Exception e) {
                errors.add("Invalid database URL format: " + environment.getDatabaseUrl());
            }
        }

        // Validate API endpoint if present
        if (environment.getApiEndpoint() != null && !environment.getApiEndpoint().isEmpty()) {
            try {
                ValidationUtils.validateUrl(environment.getApiEndpoint(), "API Endpoint");
            } catch (ValidationException e) {
                if (e.getValidationErrors() != null) {
                    errors.addAll(e.getValidationErrors());
                } else {
                    errors.add(e.getMessage());
                }
            }
        }

        if (!errors.isEmpty()) {
            // Use constructor: ValidationException(String message, List<String> validationErrors)
            throw new ValidationException("URL validation failed", errors);
        }
    }

    /**
     * Validates all services in the environment.
     */
    private void validateServices(Environment environment) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (environment.getServices().isEmpty()) {
            errors.add("Environment must contain at least one service");
        }

        for (Service service : environment.getServices()) {
            List<String> serviceErrors = ValidationUtils.validateService(service);
            if (!serviceErrors.isEmpty()) {
                errors.add("Service '" + service.getName() + "': " + String.join("; ", serviceErrors));
            }

            // Additional service-specific validation
            if (service.getVersion() == null || service.getVersion().isEmpty()) {
                errors.add("Service '" + service.getName() + "' must have a version");
            }

            if (service.getType() == null) {
                errors.add("Service '" + service.getName() + "' must have a type");
            }
        }

        if (!errors.isEmpty()) {
            // Use constructor: ValidationException(String objectType, String objectId, List<String> validationErrors)
            throw new ValidationException("Environment", environment.getId(), errors);
        }
    }

    /**
     * Performs health check on the environment.
     */
    private void performHealthCheck(Environment environment) throws ValidationException {
        if (environment.getType() == EnvironmentType.DEVELOPMENT) {
            // Skip health check for development environments
            return;
        }

        List<String> errors = new ArrayList<>();
        List<Future<HealthCheckResult>> futures = new ArrayList<>();

        // Check base URL
        futures.add(executorService.submit(() -> checkUrlAccessibility(environment.getBaseUrl())));

        // Check API endpoint if present
        if (environment.getApiEndpoint() != null && !environment.getApiEndpoint().isEmpty()) {
            futures.add(executorService.submit(() ->
                    checkUrlAccessibility(environment.getApiEndpoint())));
        }

        // Wait for all checks to complete
        for (Future<HealthCheckResult> future : futures) {
            try {
                HealthCheckResult result = future.get(timeoutSeconds, TimeUnit.SECONDS);
                if (!result.isAccessible()) {
                    errors.add(result.getMessage());
                }
            } catch (TimeoutException e) {
                errors.add("Health check timeout after " + timeoutSeconds + " seconds");
            } catch (Exception e) {
                errors.add("Health check failed: " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            // Use constructor: ValidationException(String objectType, String objectId, List<String> validationErrors)
            throw new ValidationException("Environment", environment.getId(), errors);
        }
    }

    /**
     * Checks if a URL is accessible.
     */
    private HealthCheckResult checkUrlAccessibility(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 400) {
                return new HealthCheckResult(true, urlString + " is accessible (HTTP " + responseCode + ")");
            } else {
                return new HealthCheckResult(false,
                        urlString + " returned error code: " + responseCode);
            }
        } catch (Exception e) {
            return new HealthCheckResult(false,
                    "Cannot connect to " + urlString + ": " + e.getMessage());
        }
    }

    /**
     * Validates environment configuration.
     */
    private void validateConfiguration(Environment environment) throws ValidationException {
        List<String> errors = new ArrayList<>();

        // Check for required configuration based on environment type
        switch (environment.getType()) {
            case PRODUCTION:
                if (environment.getDatabaseUrl() == null || environment.getDatabaseUrl().isEmpty()) {
                    errors.add("Production environment must have a database URL configured");
                }
                break;

            case STAGING:
                if (environment.getApiEndpoint() == null || environment.getApiEndpoint().isEmpty()) {
                    errors.add("Staging environment should have an API endpoint configured");
                }
                break;
            default:
                // For other environment types, no specific configuration requirements
                break;
        }

        if (!errors.isEmpty()) {
            // Use constructor: ValidationException(String objectType, String objectId, List<String> validationErrors)
            throw new ValidationException("Environment", environment.getId(), errors);
        }
    }

    /**
     * Performs warning-level checks.
     */
    private List<String> performWarningChecks(Environment environment) {
        List<String> warnings = new ArrayList<>();

        // Check for outdated services
        for (Service service : environment.getServices()) {
            if (isVersionOutdated(service.getVersion())) {
                warnings.add("Service '" + service.getName() + "' has potentially outdated version: " +
                        service.getVersion());
            }
        }

        // Check environment naming conventions
        if (!environment.getName().matches("^[A-Z][A-Za-z0-9\\s-]+$")) {
            warnings.add("Environment name should start with uppercase letter and contain only alphanumeric characters, spaces, and hyphens");
        }

        // Check for default ports in production
        if (environment.getType() == EnvironmentType.PRODUCTION) {
            if (environment.getBaseUrl().contains(":8080") ||
                    environment.getBaseUrl().contains(":3000") ||
                    environment.getBaseUrl().contains(":4200")) {
                warnings.add("Production environment uses default development port");
            }
        }

        return warnings;
    }

    /**
     * Simple check for outdated versions (basic heuristic).
     */
    private boolean isVersionOutdated(String version) {
        if (version == null || version.isEmpty()) {
            return false;
        }

        try {
            // Simple check: if version contains "alpha", "beta", "rc", or is very low
            String lowerVersion = version.toLowerCase();
            if (lowerVersion.contains("alpha") ||
                    lowerVersion.contains("beta") ||
                    lowerVersion.contains("rc") ||
                    lowerVersion.contains("snapshot")) {
                return true;
            }

            // Check if major version is 0
            if (version.startsWith("0.")) {
                return true;
            }

        } catch (Exception e) {
            // If we can't parse, don't mark as outdated
        }

        return false;
    }

    /**
     * Shutdown the validator.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Validation result container.
     */
    public static class ValidationResult {
        private final String environmentId;
        private final String environmentName;
        private final List<String> errors;
        private final List<String> warnings;
        private final boolean isValid;

        public ValidationResult(String environmentId, String environmentName,
                                List<String> errors, List<String> warnings, boolean isValid) {
            this.environmentId = environmentId;
            this.environmentName = environmentName;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
            this.isValid = isValid;
        }

        public String getEnvironmentId() { return environmentId; }
        public String getEnvironmentName() { return environmentName; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public List<String> getWarnings() { return new ArrayList<>(warnings); }
        public boolean isValid() { return isValid; }
        public boolean hasWarnings() { return !warnings.isEmpty(); }

        public String toFormattedString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Validation Result for '").append(environmentName).append("':\n");

            if (isValid) {
                sb.append("✅ VALID\n");
            } else {
                sb.append("❌ INVALID\n");
            }

            if (!errors.isEmpty()) {
                sb.append("\nErrors (").append(errors.size()).append("):\n");
                for (int i = 0; i < errors.size(); i++) {
                    sb.append("  ").append(i + 1).append(". ").append(errors.get(i)).append("\n");
                }
            }

            if (!warnings.isEmpty()) {
                sb.append("\nWarnings (").append(warnings.size()).append("):\n");
                for (int i = 0; i < warnings.size(); i++) {
                    sb.append("  ").append(i + 1).append(". ").append(warnings.get(i)).append("\n");
                }
            }

            return sb.toString();
        }
    }

    /**
     * Health check result container.
     */
    private static class HealthCheckResult {
        private final boolean accessible;
        private final String message;

        public HealthCheckResult(boolean accessible, String message) {
            this.accessible = accessible;
            this.message = message;
        }

        public boolean isAccessible() { return accessible; }
        public String getMessage() { return message; }
    }

    /**
     * Functional interface for validation checks.
     */
    @FunctionalInterface
    private interface ValidationCheck {
        void validate() throws ValidationException;
    }
}