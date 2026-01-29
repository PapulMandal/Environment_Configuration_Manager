package com.environment.manager.service;

import com.environment.manager.model.*;
import com.environment.manager.repository.EnvironmentRepository;
import com.environment.manager.strategy.DeploymentStrategy;
import com.environment.manager.observer.DeploymentObserver;

import java.util.ArrayList;
import java.util.List;

public class DeploymentService {
    private final EnvironmentRepository repository;
    private DeploymentStrategy deploymentStrategy;
    private final List<DeploymentObserver> observers = new ArrayList<>();

    public DeploymentService(EnvironmentRepository repository) {
        this.repository = repository;
    }

    public void setDeploymentStrategy(DeploymentStrategy strategy) {
        this.deploymentStrategy = strategy;
    }

    public void addObserver(DeploymentObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(DeploymentObserver observer) {
        observers.remove(observer);
    }

    public boolean deployToEnvironment(String environmentName, Service service, String version, String deployedBy) {
        return repository.findByName(environmentName)
                .map(environment -> {
                    // Validate environment
                    List<String> validationIssues = environment.validate();
                    if (!validationIssues.isEmpty()) {
                        System.out.println("❌ Validation failed:");
                        validationIssues.forEach(System.out::println);
                        return false;
                    }

                    // Check if approval is required
                    if (environment.requiresApproval()) {
                        System.out.println("⚠️ Approval required for " + environment.getType().getDescription());
                        System.out.print("Do you approve this deployment? (yes/no): ");
                        // In real app, this would come from user input or approval system
                        // For demo, auto-approve if user enters 'yes' in console
                    }

                    // Record deployment
                    environment.recordDeployment(version, deployedBy);
                    environment.updateStatus(DeploymentStatus.IN_PROGRESS);

                    // Notify observers
                    notifyDeploymentStart(environment, service, version);

                    // Perform deployment using strategy
                    try {
                        if (deploymentStrategy != null) {
                            deploymentStrategy.deploy(environment, service, version);
                        } else {
                            // Default deployment
                            System.out.println("🚀 Deploying " + service.getName() + " v" + version);
                            Thread.sleep(1500);
                            environment.updateStatus(DeploymentStatus.SUCCESS);
                        }

                        // Update environment
                        environment.setCurrentVersion(version);
                        environment.addService(service);
                        repository.save(environment);

                        // Notify success
                        notifyDeploymentSuccess(environment, service, version);
                        return true;

                    } catch (Exception e) {
                        environment.updateStatus(DeploymentStatus.FAILED);
                        notifyDeploymentFailure(environment, service, version, e.getMessage());
                        return false;
                    }
                })
                .orElseGet(() -> {
                    System.out.println("❌ Environment not found: " + environmentName);
                    return false;
                });
    }

    public boolean deployToAllTesting(Service service, String version, String deployedBy) {
        System.out.println("🚀 Deploying to all testing environments...");

        boolean allSuccess = true;
        List<Environment> testingEnvs = repository.findAll().stream()
                .filter(env -> {
                    EnvironmentType type = env.getType();
                    return type == EnvironmentType.DEVELOPMENT ||
                            type == EnvironmentType.QUALITY_ASSURANCE ||
                            type == EnvironmentType.USER_ACCEPTANCE ||
                            type == EnvironmentType.STAGING;
                })
                .toList();

        for (Environment env : testingEnvs) {
            System.out.println("📦 Deploying to " + env.getName() + "...");
            boolean success = deployToEnvironment(env.getName(), service, version, deployedBy);
            if (!success) {
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    public boolean rollback(String environmentName, String serviceId) {
        return repository.findByName(environmentName)
                .map(environment -> {
                    environment.getServices().stream()
                            .filter(service -> service.getId().equals(serviceId))
                            .findFirst()
                            .ifPresent(service -> {
                                // Notify observers
                                observers.forEach(obs -> obs.onRollback(environment, service));

                                // Remove service
                                environment.removeService(serviceId);

                                // Record rollback
                                environment.recordDeployment(service.getVersion() + "-ROLLBACK", "system");
                                environment.updateStatus(DeploymentStatus.ROLLED_BACK);

                                repository.save(environment);
                            });
                    return true;
                })
                .orElse(false);
    }

    private void notifyDeploymentStart(Environment environment, Service service, String version) {
        observers.forEach(obs -> obs.onDeploymentStart(environment, service, version));
    }

    private void notifyDeploymentSuccess(Environment environment, Service service, String version) {
        observers.forEach(obs -> obs.onDeploymentSuccess(environment, service, version));
    }

    private void notifyDeploymentFailure(Environment environment, Service service, String version, String error) {
        observers.forEach(obs -> obs.onDeploymentFailure(environment, service, version, error));
    }
}