package com.environment.manager.model;

import java.util.ArrayList;
import java.util.List;

public class UATEnvironment extends Environment {

    public UATEnvironment(String id, String name, String baseUrl) {
        super(id, name, EnvironmentType.USER_ACCEPTANCE, baseUrl);

        this.setDatabaseUrl("jdbc:mysql://uat-db.company.com:3306/uat_db");
        this.setApiEndpoint(baseUrl + "/api/v1");

        // UAT configurations
        addConfiguration(ConfigItem.featureFlag("DEBUG_MODE", false, "Debug disabled", "uat-team"));
        addConfiguration(ConfigItem.featureFlag("NEW_FEATURE", true, "Test new feature", "uat-team"));
        addConfiguration(ConfigItem.apiConfig("LOG_LEVEL", "INFO", "UAT log level", "uat-team"));
    }

    @Override
    public List<String> validate() {
        List<String> issues = new ArrayList<>();

        if (!getBaseUrl().contains("uat")) {
            issues.add("UAT URL should contain 'uat'");
        }

        return issues;
    }

    @Override
    public int getMaxParallelDeployments() {
        return 1; // Sequential deployments in UAT
    }
}