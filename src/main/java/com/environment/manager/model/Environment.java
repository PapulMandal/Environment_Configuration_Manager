package com.environment.manager.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Environment {
    private final String id;
    private final String name;
    private final EnvironmentType type;
    private final String baseUrl;
    private final LocalDateTime createdAt;

    protected final Set<Service> services;
    protected final Map<String, ConfigItem> configurations;
    protected final List<DeploymentHistory> deploymentHistory;

    protected String currentVersion;
    protected DeploymentStatus status;
    protected boolean isActive;
    protected String databaseUrl;
    protected String apiEndpoint;

    protected Environment(String id, String name, EnvironmentType type, String baseUrl) {
        this.id = Objects.requireNonNull(id, "Environment ID cannot be null");
        this.name = Objects.requireNonNull(name, "Environment name cannot be null");
        this.type = Objects.requireNonNull(type, "Environment type cannot be null");
        this.baseUrl = Objects.requireNonNull(baseUrl, "Base URL cannot be null");
        this.createdAt = LocalDateTime.now();

        this.services = new HashSet<>();
        this.configurations = new HashMap<>();
        this.deploymentHistory = new ArrayList<>();

        this.currentVersion = "1.0.0";
        this.status = DeploymentStatus.PENDING;
        this.isActive = false;
    }

    // Template Method Pattern - subclasses define validation rules
    public abstract List<String> validate();
    public abstract int getMaxParallelDeployments();

    public boolean requiresApproval() {
        return type.requiresApproval();
    }

    // Common methods for all environments
    public void addService(Service service) {
        services.add(service);
    }

    public void removeService(String serviceId) {
        services.removeIf(service -> service.getId().equals(serviceId));
    }

    public void addConfiguration(ConfigItem config) {
        configurations.put(config.getKey(), config);
    }

    public ConfigItem getConfiguration(String key) {
        return configurations.get(key);
    }

    public void removeConfiguration(String key) {
        configurations.remove(key);
    }

    public void recordDeployment(String version, String deployedBy) {
        DeploymentHistory history = new DeploymentHistory(name, version, deployedBy);
        deploymentHistory.add(history);
    }

    public void updateStatus(DeploymentStatus newStatus) {
        this.status = newStatus;
        if (newStatus.isCompleted()) {
            this.isActive = newStatus.isSuccessful();
        }
    }

    public List<ConfigItem> getConfigurationsByType(ConfigType type) {
        return configurations.values().stream()
                .filter(config -> config.getType() == type)
                .collect(Collectors.toList());
    }

    public Map<String, String> getConfigMap() {
        return configurations.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getValue()
                ));
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public EnvironmentType getType() { return type; }
    public String getBaseUrl() { return baseUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getCurrentVersion() { return currentVersion; }
    public DeploymentStatus getStatus() { return status; }
    public boolean isActive() { return isActive; }
    public String getDatabaseUrl() { return databaseUrl; }
    public String getApiEndpoint() { return apiEndpoint; }
    public Set<Service> getServices() { return Collections.unmodifiableSet(services); }
    public Map<String, ConfigItem> getConfigurations() { return Collections.unmodifiableMap(configurations); }
    public List<DeploymentHistory> getDeploymentHistory() { return Collections.unmodifiableList(deploymentHistory); }

    // Setters
    public void setDatabaseUrl(String databaseUrl) { this.databaseUrl = databaseUrl; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }
    public void setCurrentVersion(String currentVersion) { this.currentVersion = currentVersion; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Environment that = (Environment) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - %s - v%s", name, type.getCode(), baseUrl, currentVersion);
    }

    public String getDetailedInfo() {
        return String.format("""
            ========================================
            Environment: %s
            Type: %s (%s)
            URL: %s
            Status: %s %s
            Active: %s
            Version: %s
            Created: %s
            Services: %d
            Configurations: %d
            Deployments: %d
            Requires Approval: %s
            ========================================
            """,
                name, type.getDescription(), type.getCode(),
                baseUrl, status.getEmoji(), status.getDisplayName(),
                isActive ? "✅ Yes" : "❌ No",
                currentVersion, createdAt,
                services.size(), configurations.size(), deploymentHistory.size(),
                requiresApproval() ? "✅ Yes" : "❌ No"
        );
    }
}