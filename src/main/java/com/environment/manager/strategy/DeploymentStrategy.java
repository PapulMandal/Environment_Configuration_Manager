package com.environment.manager.strategy;

import com.environment.manager.model.Environment;
import com.environment.manager.model.Service;

public interface DeploymentStrategy {
    void deploy(Environment environment, Service service, String version);
    void rollback(Environment environment, Service service);
    String getStrategyName();
    String getDescription();
}