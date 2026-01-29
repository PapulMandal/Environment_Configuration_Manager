package com.environment.manager;

import com.environment.manager.model.*;

public class EnvironmentFactory {
    public static Environment createEnvironment(EnvironmentType type, String name, String baseUrl) {
        String id = generateId(type, name);

        switch (type) {
            case DEVELOPMENT:
                return new DevelopmentEnvironment(id, name, baseUrl);
            case QUALITY_ASSURANCE:
                return new QAEnvironment(id, name, baseUrl);
            case USER_ACCEPTANCE:
                return new UATEnvironment(id, name, baseUrl);
            case STAGING:
                return new StagingEnvironment(id, name, baseUrl);
            case PRODUCTION:
                return new ProductionEnvironment(id, name, baseUrl);
            default:
                throw new IllegalArgumentException("Unknown environment type: " + type);
        }
    }

    private static String generateId(EnvironmentType type, String name) {
        String prefix;
        switch (type) {
            case DEVELOPMENT: prefix = "DEV"; break;
            case QUALITY_ASSURANCE: prefix = "QA"; break;
            case USER_ACCEPTANCE: prefix = "UAT"; break;
            case STAGING: prefix = "STG"; break;
            case PRODUCTION: prefix = "PROD"; break;
            default: prefix = "ENV";
        }
        return prefix + "-" + name.replaceAll("\\s+", "-").toUpperCase() + "-" + System.currentTimeMillis();
    }
}