package com.environment.manager.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeploymentHistory {
    private final String deploymentId;
    private final String environmentName;
    private final String version;
    private final String deployedBy;
    private final LocalDateTime deployedAt;
    private DeploymentStatus status;
    private String notes;
    private long durationMs;

    public DeploymentHistory(String environmentName, String version, String deployedBy) {
        this.deploymentId = UUID.randomUUID().toString().substring(0, 8);
        this.environmentName = environmentName;
        this.version = version;
        this.deployedBy = deployedBy;
        this.deployedAt = LocalDateTime.now();
        this.status = DeploymentStatus.IN_PROGRESS;
        this.notes = "";
        this.durationMs = 0;
    }

    public void complete(DeploymentStatus finalStatus, String notes, long durationMs) {
        this.status = finalStatus;
        this.notes = notes;
        this.durationMs = durationMs;
    }

    // Getters
    public String getDeploymentId() { return deploymentId; }
    public String getEnvironmentName() { return environmentName; }
    public String getVersion() { return version; }
    public String getDeployedBy() { return deployedBy; }
    public LocalDateTime getDeployedAt() { return deployedAt; }
    public DeploymentStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    public long getDurationMs() { return durationMs; }

    @Override
    public String toString() {
        return String.format("Deployment %s: %s v%s by %s at %s - %s",
                deploymentId, environmentName, version, deployedBy,
                deployedAt, status.getDisplayName());
    }
}