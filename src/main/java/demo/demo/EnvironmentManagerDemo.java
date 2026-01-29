package demo.demo;

import builder.ConfigurationBuilder;
import builder.EnvironmentBuilder;
import com.environment.manager.model.*;

import java.util.Map;

public class EnvironmentManagerDemo {
    public static void main(String[] args) {
        System.out.println("=== Environment Management System Demo ===\n");

        // Example 1: Using EnvironmentBuilder
        System.out.println("1. Creating environments with EnvironmentBuilder:");

        Environment devEnv = EnvironmentBuilder.createDevelopment("My Development");
        System.out.println("Created: " + devEnv);
        System.out.println("Validation issues: " + devEnv.validate());
        System.out.println("Requires approval: " + devEnv.requiresApproval());
        System.out.println("Max parallel deployments: " + devEnv.getMaxParallelDeployments());
        System.out.println();

        Environment prodEnv = EnvironmentBuilder.createProduction("My Production");
        System.out.println("Created: " + prodEnv);
        System.out.println("Validation issues: " + prodEnv.validate());
        System.out.println("Requires approval: " + prodEnv.requiresApproval());
        System.out.println("Max parallel deployments: " + prodEnv.getMaxParallelDeployments());
        System.out.println(prodEnv.getDetailedInfo());

        // Example 2: Using ConfigurationBuilder
        System.out.println("\n2. Creating configurations with ConfigurationBuilder:");

        Map<String, ConfigItem> devConfigs = ConfigurationBuilder.createDevelopmentConfig();
        System.out.println("Development configurations created: " + devConfigs.size());
        devConfigs.values().forEach(config ->
                System.out.println("  - " + config.getKey() + ": " + config.getValue()));

        // Example 3: Manual environment creation
        System.out.println("\n3. Manual environment creation:");

        Environment qaEnv = new EnvironmentBuilder()
                .setId("QA-123")
                .setName("Manual QA Environment")
                .setType(EnvironmentType.QUALITY_ASSURANCE)
                .setBaseUrl("https://qa-test.company.com")
                .withDatabaseConfig(
                        "jdbc:mysql://test-qa-db.company.com:3306/test_db",
                        "test_user",
                        "test_pass",
                        "tester"
                )
                .withApiConfig(
                        "https://qa-test.company.com/api",
                        "test-api-key",
                        45,
                        "tester"
                )
                .build();

        System.out.println(qaEnv.getDetailedInfo());

        // Example 4: Adding services and recording deployments
        System.out.println("\n4. Adding services and recording deployments:");

        Service webService = new Service("web-001", "Web Application", "1.2.3",
                ServiceType.WEB_SERVICE);
        Service dbService = new Service("db-001", "Database", "5.7.0",
                ServiceType.DATABASE);

        qaEnv.addService(webService);
        qaEnv.addService(dbService);

        qaEnv.recordDeployment("2.0.0", "john.doe");
        qaEnv.updateStatus(DeploymentStatus.SUCCESS);

        System.out.println("Services: " + qaEnv.getServices());
        System.out.println("Deployments: " + qaEnv.getDeploymentHistory().size());

        // Example 5: Configuration management
        System.out.println("\n5. Configuration management:");

        ConfigItem newFeature = ConfigItem.featureFlag("NEW_DASHBOARD", true,
                "New dashboard feature", "product.team");
        qaEnv.addConfiguration(newFeature);

        System.out.println("Feature flags in QA:");
        qaEnv.getConfigurationsByType(ConfigType.FEATURE_FLAG)
                .forEach(config -> System.out.println("  - " + config));

        System.out.println("\n=== Demo Complete ===");
    }
}