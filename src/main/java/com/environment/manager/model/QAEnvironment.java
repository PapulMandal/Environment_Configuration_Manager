package com.environment.manager.model;

import java.util.ArrayList;
import java.util.List;

public class QAEnvironment extends Environment {

    public QAEnvironment(String id, String name, String baseUrl) {
        super(id, name, EnvironmentType.QUALITY_ASSURANCE, baseUrl);

        this.setDatabaseUrl("jdbc:mysql://qa-db.company.com:3306/qa_db");
        this.setApiEndpoint(baseUrl + "/api/v1");

        // QA-specific configurations using ConfigItem class
        addConfiguration(ConfigItem.featureFlag("DEBUG_MODE", false, "Disable debug in QA", "qa-team"));
        addConfiguration(ConfigItem.apiConfig("LOG_LEVEL", "INFO", "QA log level", "qa-team"));
        addConfiguration(ConfigItem.apiConfig("MAX_USERS", "100", "QA user limit", "qa-team"));
        addConfiguration(ConfigItem.securityConfig("AUTH_TYPE", "TEST", "Test authentication", "qa-team"));
        addConfiguration(ConfigItem.dbConfig("username", "qa_user", "QA database user", "qa-team"));
        addConfiguration(ConfigItem.dbConfig("password", "qa_pass", "QA database password", "qa-team"));
    }

    @Override
    public List<String> validate() {
        List<String> issues = new ArrayList<>();

        if (!getBaseUrl().contains("qa")) {
            issues.add("QA environment URL should contain 'qa'");
        }

        if (getDatabaseUrl() == null || !getDatabaseUrl().contains("qa-db")) {
            issues.add("QA database URL should point to qa-db");
        }

        // Check for MAX_USERS configuration
        if (getConfiguration("MAX_USERS") == null) {
            issues.add("MAX_USERS configuration is required for QA");
        }

        return issues;
    }

    @Override
    public int getMaxParallelDeployments() {
        return 2; // Limited parallel deployments in QA
    }
}

// Helper method for security config
class ConfigItemHelper {
    public static ConfigItem securityConfig(String key, String value, String description, String modifiedBy) {
        return new ConfigItem("security." + key, value, description, ConfigType.SECURITY_CONFIG,
                java.time.LocalDateTime.now(), modifiedBy, false);
    }
}