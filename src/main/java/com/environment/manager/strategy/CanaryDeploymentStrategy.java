package com.environment.manager.strategy;

import com.environment.manager.model.Environment;
import com.environment.manager.model.Service;
import com.environment.manager.model.DeploymentStatus;

public class CanaryDeploymentStrategy implements DeploymentStrategy {

    @Override
    public void deploy(Environment environment, Service service, String version) {
        System.out.println("🐦 Starting Canary deployment for " + service.getName());
        System.out.println("1. Deploying new version to a small subset of users (5%)");
        System.out.println("2. Monitoring performance and error rates");
        System.out.println("3. Gradually increasing traffic to new version");
        System.out.println("4. Full rollout after successful validation");

        try {
            Thread.sleep(3000);
            System.out.println("✅ Canary deployment completed successfully!");
            environment.updateStatus(DeploymentStatus.SUCCESS);
        } catch (InterruptedException e) {
            System.out.println("❌ Canary deployment failed!");
            environment.updateStatus(DeploymentStatus.FAILED);
        }
    }

    @Override
    public void rollback(Environment environment, Service service) {
        System.out.println("↩️ Rolling back Canary deployment");
        System.out.println("Redirecting all traffic to stable version");
        environment.updateStatus(DeploymentStatus.ROLLED_BACK);
    }

    @Override
    public String getStrategyName() {
        return "Canary Deployment";
    }

    @Override
    public String getDescription() {
        return "Gradually releases new version to a subset of users before full rollout";
    }
}