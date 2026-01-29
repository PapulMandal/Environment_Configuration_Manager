package com.environment.manager.model;

import java.util.ArrayList;
import java.util.List;

public class StagingEnvironment extends Environment {

    public StagingEnvironment(String id, String name, String baseUrl) {
        super(id, name, EnvironmentType.STAGING, baseUrl);

        this.setDatabaseUrl("jdbc:mysql://staging-db.company.com:3306/staging_db");
        this.setApiEndpoint(baseUrl + "/api/v1");

        // Staging configurations (similar to production)
        addConfiguration(ConfigItem.featureFlag("DEBUG_MODE", false, "Debug disabled", "devops"));
        addConfiguration(ConfigItem.apiConfig("LOG_LEVEL", "INFO", "Staging log level", "devops"));
        addConfiguration(ConfigItem.securityConfig("SSL_ENABLED", "true", "SSL enabled", "devops"));
    }

    @Override
    public List<String> validate() {
        List<String> issues = new ArrayList<>();

        if (!getBaseUrl().contains("staging")) {
            issues.add("Staging URL should contain 'staging'");
        }

        return issues;
    }

    @Override
    public int getMaxParallelDeployments() {
        return 1; // Careful deployments in staging
    }
}