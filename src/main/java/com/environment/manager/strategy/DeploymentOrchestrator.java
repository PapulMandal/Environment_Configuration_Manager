package com.environment.manager.strategy;

import com.environment.manager.model.Environment;
import com.environment.manager.model.Service;
import java.util.ArrayList;
import java.util.List;

public class DeploymentOrchestrator {
    private DeploymentStrategy strategy;
    private final List<DeploymentObserver> observers = new ArrayList<>();

    public DeploymentOrchestrator(DeploymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(DeploymentStrategy strategy) {
        this.strategy = strategy;
        notifyObservers("Strategy changed to: " + strategy.getClass().getSimpleName());
    }

    public boolean deploy(Environment environment, Service service, String version, String deployedBy) {
        notifyObservers("Starting deployment to " + environment.getName());
        boolean result = deployWithStrategy(strategy, environment, service, version, deployedBy);


        if (result) {
            notifyObservers("Deployment successful to " + environment.getName());
        } else {
            notifyObservers("Deployment failed to " + environment.getName());
        }

        return result;
    }

    public boolean rollback(Environment environment, String serviceId) {
        notifyObservers("Starting rollback for service: " + serviceId);
        boolean result = rollbackWithStrategy(strategy, environment, serviceId);

        if (result) {
            notifyObservers("Rollback successful for service: " + serviceId);
        } else {
            notifyObservers("Rollback failed for service: " + serviceId);
        }

        return result;
    }

    public void addObserver(DeploymentObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(DeploymentObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String message) {
        for (DeploymentObserver observer : observers) {
            observer.onDeploymentEvent(message);
        }
    }

    public interface DeploymentObserver {
        void onDeploymentEvent(String message);
    }
    private boolean deployWithStrategy(DeploymentStrategy strategy, Environment environment,
                                       Service service, String version, String deployedBy) {
        try {
            strategy.deploy(environment, service, version);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean rollbackWithStrategy(DeploymentStrategy strategy, Environment environment,
                                         String serviceId) {
        // Find the service first
        Service service = environment.getServices().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElse(null);

        if (service != null) {
            strategy.rollback(environment, service);
            return true;
        }
        return false;
    }
}