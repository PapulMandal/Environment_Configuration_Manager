package com.environment.manager.model;

import java.util.ArrayList;
import java.util.List;

public class DevelopmentEnvironment extends Environment {

    public DevelopmentEnvironment(String id, String name, String baseUrl) {
        super(id, name, EnvironmentType.DEVELOPMENT, baseUrl);

        this.setDatabaseUrl("jdbc:mysql://localhost:3306/dev_db");
        this.setApiEndpoint(baseUrl + "/api");

        // Development-specific configurations
        addConfiguration(ConfigItem.featureFlag("DEBUG_MODE", true, "Enable debug logging", "system"));
        addConfiguration(ConfigItem.featureFlag("ENABLE_CACHE", false, "Disable cache in dev", "system"));
        addConfiguration(ConfigItem.apiConfig("LOG_LEVEL", "DEBUG", "Development log level", "system"));
        addConfiguration(ConfigItem.dbConfig("username", "dev_user", "Dev database user", "system"));
        addConfiguration(ConfigItem.dbConfig("password", "dev_pass", "Dev database password", "system"));
    }

    @Override
    public List<String> validate() {
        List<String> issues = new ArrayList<>();

        if (getBaseUrl().startsWith("https://")) {
            issues.add("Dev environment should use HTTP, not HTTPS");
        }

        if (!getDatabaseUrl().contains("localhost")) {
            issues.add("Dev database should be on localhost");
        }

        if (getConfigurationsByType(ConfigType.FEATURE_FLAG).size() < 2) {
            issues.add("Dev environment should have at least 2 feature flags");
        }

        return issues;
    }

    @Override
    public int getMaxParallelDeployments() {
        return 5; // Allow more parallel deployments in dev
    }
}