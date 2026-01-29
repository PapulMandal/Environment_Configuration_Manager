package com.environment.manager;

import builder.EnvironmentBuilder;
import com.environment.manager.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentManagerIntegrationTest {

    @Test
    @DisplayName("Integration test: Full environment lifecycle")
    void testFullEnvironmentLifecycle() {
        // 1. Create environment
        Environment environment = EnvironmentBuilder.createDevelopment("Lifecycle Test");

        // 2. Add configurations
        environment.addConfiguration(ConfigItem.featureFlag("FEATURE_A", true, "Feature A", "admin"));
        environment.addConfiguration(ConfigItem.apiConfig("API_ENDPOINT", "/api/v1", "API endpoint", "admin"));

        // 3. Add services
        Service service1 = new Service("srv-001", "User Service", "1.0.0",
                ServiceType.WEB_SERVICE, environment.getBaseUrl() + "/users");
        Service service2 = new Service("srv-002", "Auth Service", "2.1.0",
                ServiceType.WEB_SERVICE, environment.getBaseUrl() + "/auth");

        environment.addService(service1);
        environment.addService(service2);

        // 4. Validate
        assertTrue(environment.validate().isEmpty());

        // 5. Start deployment
        environment.recordDeployment("2.0.0", "deploy-bot");
        environment.updateStatus(DeploymentStatus.IN_PROGRESS);

        // 6. Complete deployment
        environment.updateStatus(DeploymentStatus.SUCCESS);
        environment.setCurrentVersion("2.0.0");

        // 7. Verify final state
        assertEquals("2.0.0", environment.getCurrentVersion());
        assertEquals(DeploymentStatus.SUCCESS, environment.getStatus());
        assertTrue(environment.isActive());
        assertEquals(2, environment.getServices().size());
        assertTrue(environment.getConfigurations().size() >= 2); // Including defaults
        assertEquals(1, environment.getDeploymentHistory().size());

        // 8. Test configuration retrieval
        ConfigItem feature = environment.getConfiguration("FEATURE_A");
        assertNotNull(feature);
        assertEquals("true", feature.getValue());
        assertEquals(ConfigType.FEATURE_FLAG, feature.getType());

        // 9. Test service removal
        environment.removeService("srv-001");
        assertEquals(1, environment.getServices().size());

        // 10. Test configuration removal
        environment.removeConfiguration("FEATURE_A");
        assertNull(environment.getConfiguration("FEATURE_A"));
    }

    @Test
    @DisplayName("Integration test: Multiple environment types")
    void testMultipleEnvironmentTypes() {
        // Test all environment types
        for (EnvironmentType type : EnvironmentType.values()) {
            Environment env = TestUtils.createTestEnvironment(type);

            assertNotNull(env);
            assertEquals(type, env.getType());
            assertNotNull(env.getBaseUrl());
            assertNotNull(env.getName());
            assertNotNull(env.getId());

            // Each type should have specific validation rules
            List<String> validationIssues = env.validate();
            System.out.println(type + " validation issues: " + validationIssues.size());

            // Each type should have default configurations
            assertFalse(env.getConfigurations().isEmpty());

            // Test type-specific methods
            switch (type) {
                case DEVELOPMENT:
                    assertEquals(5, env.getMaxParallelDeployments());
                    assertFalse(env.requiresApproval());
                    break;
                case QUALITY_ASSURANCE:
                    assertEquals(2, env.getMaxParallelDeployments());
                    assertFalse(env.requiresApproval());
                    break;
                case PRODUCTION:
                    assertEquals(1, env.getMaxParallelDeployments());
                    assertTrue(env.requiresApproval());
                    break;
                case STAGING:
                case USER_ACCEPTANCE:
                    assertEquals(1, env.getMaxParallelDeployments());
                    assertTrue(env.requiresApproval());
                    break;
            }
        }
    }
}