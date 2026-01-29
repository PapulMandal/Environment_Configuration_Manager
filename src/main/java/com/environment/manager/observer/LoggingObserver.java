package com.environment.manager.observer;

import com.environment.manager.model.Environment;
import com.environment.manager.model.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingObserver implements DeploymentObserver {

    @Override
    public void onDeploymentStart(Environment environment, Service service, String version) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.printf("[%s] 🚀 START: Deploying %s v%s to %s%n",
                timestamp, service.getName(), version, environment.getName());
    }

    @Override
    public void onDeploymentSuccess(Environment environment, Service service, String version) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.printf("[%s] ✅ SUCCESS: %s v%s deployed to %s%n",
                timestamp, service.getName(), version, environment.getName());
    }

    @Override
    public void onDeploymentFailure(Environment environment, Service service, String version, String error) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.printf("[%s] ❌ FAILURE: Failed to deploy %s v%s to %s. Error: %s%n",
                timestamp, service.getName(), version, environment.getName(), error);
    }

    @Override
    public void onRollback(Environment environment, Service service) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        System.out.printf("[%s] ↩️ ROLLBACK: Rolling back %s in %s%n",
                timestamp, service.getName(), environment.getName());
    }
}