package com.environment.manager.model;

import java.util.ArrayList;
import java.util.List;

public class ProductionEnvironment extends Environment {

    public ProductionEnvironment(String id, String name, String baseUrl) {
        super(id, name, EnvironmentType.PRODUCTION, baseUrl);

        this.setDatabaseUrl("jdbc:mysql://prod-db-cluster.company.com:3306/prod_db");
        this.setApiEndpoint(baseUrl + "/api/v1");

        // Production-specific configurations
        addConfiguration(ConfigItem.featureFlag("DEBUG_MODE", false, "Debug disabled in production", "devops"));
        addConfiguration(ConfigItem.featureFlag("MAINTENANCE_MODE", false, "Maintenance mode", "devops"));
        addConfiguration(ConfigItem.apiConfig("LOG_LEVEL", "WARN", "Production log level", "devops"));
        addConfiguration(ConfigItem.securityConfig("SSL_ENABLED", "true", "SSL/TLS enabled", "devops"));
        addConfiguration(ConfigItem.secret("API_KEY", "prod-api-key-789", "Production API key", "devops"));
        addConfiguration(ConfigItem.dbConfig("username", "prod_user", "Production DB user", "devops"));
        addConfiguration(ConfigItem.dbConfig("password", "prod_encrypted_pass", "Production DB password", "devops"));
    }

    @Override
    public List<String> validate() {
        List<String> issues = new ArrayList<>();

        if (!getBaseUrl().startsWith("https://")) {
            issues.add("Production environment must use HTTPS");
        }

        if (getDatabaseUrl() == null || !getDatabaseUrl().contains("cluster")) {
            issues.add("Production database should use a cluster");
        }

        if (getConfiguration("security.SSL_ENABLED") == null) {
            issues.add("SSL configuration is required for production");
        }

        ConfigItem debugMode = getConfiguration("DEBUG_MODE");
        if (debugMode != null && "true".equals(debugMode.getValue())) {
            issues.add("Debug mode should be false in production");
        }

        return issues;
    }

    @Override
    public int getMaxParallelDeployments() {
        return 1; // One at a time in production
    }
}