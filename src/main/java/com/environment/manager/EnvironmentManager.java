package com.environment.manager;

import com.environment.manager.model.*;
import com.environment.manager.repository.EnvironmentRepository;
import com.environment.manager.service.DeploymentService;
import com.environment.manager.service.ValidationService;
import com.environment.manager.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnvironmentManager {
    private final EnvironmentRepository repository;
    private final DeploymentService deploymentService;
    private final ValidationService validationService;

    public EnvironmentManager(EnvironmentRepository repository,
                              DeploymentService deploymentService,
                              ValidationService validationService) {
        this.repository = repository;
        this.deploymentService = deploymentService;
        this.validationService = validationService;
    }

    public Environment createEnvironment(EnvironmentType type, String name, String baseUrl)
            throws ValidationException {

        String id = generateEnvironmentId(type, name);

        Environment environment = createEnvironmentByType(type, id, name, baseUrl);

        List<String> validationErrors = environment.validate();
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Environment validation failed", validationErrors);
        }

        repository.save(environment);
        return environment;
    }

    /**
     * Updates an existing environment.
     * Since Environment has final fields, we need to handle updates differently.
     */
    public Environment updateEnvironment(String environmentId, String newName, String newBaseUrl)
            throws ValidationException {
        Environment oldEnvironment = repository.findById(environmentId)
                .orElseThrow(() -> new IllegalArgumentException("Environment not found: " + environmentId));

        // For immutable Environment, we create a new one with updated values
        String name = (newName != null && !newName.trim().isEmpty()) ? newName : oldEnvironment.getName();
        String baseUrl = (newBaseUrl != null && !newBaseUrl.trim().isEmpty()) ? newBaseUrl : oldEnvironment.getBaseUrl();

        Environment updatedEnvironment = createEnvironmentByType(
                oldEnvironment.getType(),
                oldEnvironment.getId(), // Keep same ID
                name,
                baseUrl
        );

        // Validate the updated environment
        List<String> validationErrors = updatedEnvironment.validate();
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Environment update validation failed", validationErrors);
        }

        // Save the new environment (replaces the old one)
        repository.save(updatedEnvironment);
        return updatedEnvironment;
    }

    /**
     * Deletes an environment.
     */
    public void deleteEnvironment(String environmentId) {
        repository.delete(environmentId);
    }

    /**
     * Retrieves an environment by ID.
     */
    public Optional<Environment> getEnvironment(String environmentId) {
        return repository.findById(environmentId);
    }

    /**
     * Retrieves an environment by name.
     */
    public Optional<Environment> getEnvironmentByName(String name) {
        return repository.findByName(name);
    }

    /**
     * Lists all environments.
     */
    public List<Environment> getAllEnvironments() {
        return repository.findAll();
    }

    /**
     * Lists environments by type.
     */
    public List<Environment> getEnvironmentsByType(EnvironmentType type) {
        return repository.findAll().stream()
                .filter(env -> env.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Lists active environments.
     */
    public List<Environment> getActiveEnvironments() {
        return repository.findAll().stream()
                .filter(Environment::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Activates an environment.
     * Note: Since Environment doesn't have setActive(), we need a different approach.
     */
    public void activateEnvironment(String environmentId) {
        // You need to implement this based on your Environment design
        // Either add setActive() to Environment class or handle activation differently
        System.out.println("Activate environment functionality needs implementation: " + environmentId);
    }

    /**
     * Deactivates an environment.
     */
    public void deactivateEnvironment(String environmentId) {
        // You need to implement this based on your Environment design
        System.out.println("Deactivate environment functionality needs implementation: " + environmentId);
    }

    /**
     * Adds a service to an environment.
     */
    public void addServiceToEnvironment(String environmentId, Service service) {
        Environment environment = repository.findById(environmentId)
                .orElseThrow(() -> new IllegalArgumentException("Environment not found: " + environmentId));

        environment.addService(service);
        repository.save(environment);
    }

    /**
     * Removes a service from an environment.
     */
    public void removeServiceFromEnvironment(String environmentId, String serviceId) {
        Environment environment = repository.findById(environmentId)
                .orElseThrow(() -> new IllegalArgumentException("Environment not found: " + environmentId));

        environment.removeService(serviceId);
        repository.save(environment);
    }

    /**
     * Deploys a service to an environment.
     */
    public void deployToEnvironment(String environmentId, Service service,
                                    String version, String deployedBy) {
        deploymentService.deployToEnvironment(environmentId, service, version, deployedBy);
    }

    /**
     * Deploys to all testing environments.
     */
    public void deployToAllTesting(Service service, String version, String deployedBy) {
        deploymentService.deployToAllTesting(service, version, deployedBy);
    }

    /**
     * Rolls back a deployment.
     */
    public void rollbackDeployment(String environmentId, String serviceId) {
        deploymentService.rollback(environmentId, serviceId);
    }

    /**
     * Validates all environments.
     */
    public List<String> validateAllEnvironments() {
        return validationService.validateAllEnvironments();
    }

    /**
     * Checks if an environment is valid for deployment.
     */
    public boolean isValidForDeployment(String environmentId) {
        return validationService.isValidForDeployment(environmentId);
    }

    /**
     * Gets environment statistics.
     */
    public EnvironmentStatistics getStatistics() {
        List<Environment> allEnvironments = repository.findAll();

        long totalEnvironments = allEnvironments.size();
        long activeEnvironments = allEnvironments.stream()
                .filter(Environment::isActive)
                .count();
        long totalServices = allEnvironments.stream()
                .mapToInt(env -> env.getServices().size())
                .sum();
        long totalDeployments = allEnvironments.stream()
                .mapToInt(env -> env.getDeploymentHistory().size())
                .sum();

        return new EnvironmentStatistics(totalEnvironments, activeEnvironments,
                totalServices, totalDeployments);
    }

    /**
     * Searches environments by criteria.
     */
    public List<Environment> searchEnvironments(String query) {
        if (query == null || query.trim().isEmpty()) {
            return repository.findAll();
        }

        String searchTerm = query.toLowerCase().trim();
        return repository.findAll().stream()
                .filter(env -> matchesSearch(env, searchTerm))
                .collect(Collectors.toList());
    }

    private boolean matchesSearch(Environment env, String searchTerm) {
        return env.getName().toLowerCase().contains(searchTerm) ||
                env.getId().toLowerCase().contains(searchTerm) ||
                env.getBaseUrl().toLowerCase().contains(searchTerm) ||
                env.getType().name().toLowerCase().contains(searchTerm);
    }

    private String generateEnvironmentId(EnvironmentType type, String name) {
        String prefix;
        if (type != null) {
            switch (type) {
                case DEVELOPMENT: prefix = "DEV"; break;
                case QUALITY_ASSURANCE: prefix = "QA"; break;
                case USER_ACCEPTANCE: prefix = "UAT"; break;
                case STAGING: prefix = "STG"; break;
                case PRODUCTION: prefix = "PROD"; break;
                default: prefix = "ENV";
            }
        } else {
            prefix = "ENV";
        }

        String cleanName = name != null ?
                name.replaceAll("[^A-Za-z0-9]", "-").toUpperCase() : "UNNAMED";
        return String.format("%s-%s-%d", prefix, cleanName, System.currentTimeMillis() % 10000);
    }

    private Environment createEnvironmentByType(EnvironmentType type, String id, String name, String baseUrl) {
        switch (type) {
            case DEVELOPMENT:
                return new DevelopmentEnvironment(id, name, baseUrl);
            case QUALITY_ASSURANCE:
                return new QAEnvironment(id, name, baseUrl);
            case USER_ACCEPTANCE:
                return createUatEnvironment(id, name, baseUrl);
            case STAGING:
                return createStagingEnvironment(id, name, baseUrl);
            case PRODUCTION:
                return createProductionEnvironment(id, name, baseUrl);
            default:
                return createGenericEnvironment(id, name, type, baseUrl);
        }
    }

    /**
     * Creates a UAT environment.
     */
    private Environment createUatEnvironment(String id, String name, String baseUrl) {
        return new Environment(id, name, EnvironmentType.USER_ACCEPTANCE, baseUrl) {
            @Override
            public List<String> validate() {
                List<String> errors = new ArrayList<>();
                if (!getBaseUrl().contains("uat")) {
                    errors.add("UAT environment URL should contain 'uat'");
                }
                return errors;
            }

            @Override
            public boolean requiresApproval() {
                return true; // UAT requires approval
            }

            @Override
            public int getMaxParallelDeployments() {
                return 2;
            }
        };
    }

    /**
     * Creates a Staging environment.
     */
    private Environment createStagingEnvironment(String id, String name, String baseUrl) {
        return new Environment(id, name, EnvironmentType.STAGING, baseUrl) {
            @Override
            public List<String> validate() {
                List<String> errors = new ArrayList<>();
                if (getBaseUrl().contains("localhost")) {
                    errors.add("Staging environment should not use localhost");
                }
                if (!getBaseUrl().startsWith("https://")) {
                    errors.add("Staging environment should use HTTPS");
                }
                return errors;
            }

            @Override
            public boolean requiresApproval() {
                return true;
            }

            @Override
            public int getMaxParallelDeployments() {
                return 1;
            }
        };
    }

    /**
     * Creates a Production environment.
     */
    private Environment createProductionEnvironment(String id, String name, String baseUrl) {
        return new Environment(id, name, EnvironmentType.PRODUCTION, baseUrl) {
            @Override
            public List<String> validate() {
                List<String> errors = new ArrayList<>();
                if (getBaseUrl().startsWith("http://")) {
                    errors.add("Production environment must use HTTPS");
                }
                if (getDatabaseUrl() == null) {
                    errors.add("Production environment must have database configured");
                }
                return errors;
            }

            @Override
            public boolean requiresApproval() {
                return true;
            }

            @Override
            public int getMaxParallelDeployments() {
                return 1;
            }
        };
    }

    /**
     * Creates a generic environment for unknown types.
     */
    private Environment createGenericEnvironment(String id, String name, EnvironmentType type, String baseUrl) {
        return new Environment(id, name, type, baseUrl) {
            @Override
            public List<String> validate() {
                List<String> errors = new ArrayList<>();
                if (getName() == null || getName().trim().isEmpty()) {
                    errors.add("Environment name is required");
                }
                if (getBaseUrl() == null || getBaseUrl().trim().isEmpty()) {
                    errors.add("Base URL is required");
                }
                return errors;
            }

            @Override
            public boolean requiresApproval() {
                return type == EnvironmentType.PRODUCTION || type == EnvironmentType.STAGING;
            }

            @Override
            public int getMaxParallelDeployments() {
                switch (type) {
                    case DEVELOPMENT: return 5;
                    case QUALITY_ASSURANCE: return 3;
                    case USER_ACCEPTANCE: return 2;
                    case STAGING: return 1;
                    case PRODUCTION: return 1;
                    default: return 1;
                }
            }
        };
    }

    /**
         * Statistics data class.
         */
        public record EnvironmentStatistics(long totalEnvironments, long activeEnvironments, long totalServices,
                                            long totalDeployments) {

        public double getActivationRate() {
                return totalEnvironments > 0 ? (double) activeEnvironments / totalEnvironments * 100 : 0;
            }

            public double getServicesPerEnvironment() {
                return totalEnvironments > 0 ? (double) totalServices / totalEnvironments : 0;
            }

            @Override
            public String toString() {
                return String.format(
                        "EnvironmentStatistics{total=%d, active=%d (%.1f%%), services=%d, deployments=%d}",
                        totalEnvironments, activeEnvironments, getActivationRate(),
                        totalServices, totalDeployments
                );
            }

            public String toFormattedString() {
                return String.format(
                        "📊 Environment Statistics:\n" +
                                "  Total Environments: %d\n" +
                                "  Active Environments: %d (%.1f%%)\n" +
                                "  Total Services: %d\n" +
                                "  Total Deployments: %d\n" +
                                "  Avg Services/Environment: %.1f",
                        totalEnvironments, activeEnvironments, getActivationRate(),
                        totalServices, totalDeployments, getServicesPerEnvironment()
                );
            }
        }
}