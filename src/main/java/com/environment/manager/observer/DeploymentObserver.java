package com.environment.manager.observer;

import com.environment.manager.model.Environment;
import com.environment.manager.model.Service;

public interface DeploymentObserver {
    void onDeploymentStart(Environment environment, Service service, String version);
    void onDeploymentSuccess(Environment environment, Service service, String version);
    void onDeploymentFailure(Environment environment, Service service, String version, String error);
    void onRollback(Environment environment, Service service);
}