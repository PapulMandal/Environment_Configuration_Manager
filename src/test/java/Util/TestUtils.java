package com.environment.manager;

import com.environment.manager.model.*;

import java.util.List;

public class TestUtils {

    public static Environment createTestEnvironment(EnvironmentType type) {
        String id = type.getCode() + "-TEST-" + System.currentTimeMillis();
        String name = "Test " + type.getDescription();
        String baseUrl = getBaseUrlForType(type);

        switch (type) {
            case DEVELOPMENT:
                return new DevelopmentEnvironment(id, name, baseUrl);
            case QUALITY_ASSURANCE:
                return new QAEnvironment(id, name, baseUrl);
            case PRODUCTION:
                return new ProductionEnvironment(id, name, baseUrl);
            case STAGING:
                return new StagingEnvironment(id, name, baseUrl);
            case USER_ACCEPTANCE:
                return new UATEnvironment(id, name, baseUrl);
            default:
                throw new IllegalArgumentException("Unknown environment type: " + type);
        }
    }

    private static String getBaseUrlForType(EnvironmentType type) {
        switch (type) {
            case DEVELOPMENT:
                return "http://localhost:8080";
            case QUALITY_ASSURANCE:
                return "https://qa-test.company.com";
            case USER_ACCEPTANCE:
                return "https://uat-test.company.com";
            case STAGING:
                return "https://staging-test.company.com";
            case PRODUCTION:
                return "https://app-test.company.com";
            default:
                return "https://test.company.com";
        }
    }

    public static void printValidationResults(Environment environment) {
        System.out.println("=== Validation Results for " + environment.getName() + " ===");
        List<String> issues = environment.validate();
        if (issues.isEmpty()) {
            System.out.println("✅ All validations passed!");
        } else {
            System.out.println("❌ Found " + issues.size() + " issue(s):");
            for (String issue : issues) {
                System.out.println("  - " + issue);
            }
        }
        System.out.println();
    }

    public static void printEnvironmentSummary(Environment environment) {
        System.out.println(environment.getDetailedInfo());
        System.out.println("Configurations:");
        environment.getConfigurations().values().forEach(config ->
                System.out.println("  " + config));

        if (!environment.getServices().isEmpty()) {
            System.out.println("\nServices:");
            environment.getServices().forEach(service ->
                    System.out.println("  " + service));
        }

        if (!environment.getDeploymentHistory().isEmpty()) {
            System.out.println("\nDeployment History:");
            environment.getDeploymentHistory().forEach(deployment ->
                    System.out.println("  " + deployment));
        }
    }
}