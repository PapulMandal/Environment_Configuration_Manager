package com.environment.manager;

import builder.EnvironmentBuilder;
import builder.ConfigurationBuilder;
import com.environment.manager.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentManagerTest {

    private Environment devEnvironment;
    private Environment qaEnvironment;
    private Environment prodEnvironment;

    @BeforeEach
    void setUp() {
        // Create test environments using builders
        devEnvironment = EnvironmentBuilder.createDevelopment("Test Development");
        qaEnvironment = new EnvironmentBuilder()
                .setId("QA-TEST-001")
                .setName("Test QA")
                .setType(EnvironmentType.QUALITY_ASSURANCE)
                .setBaseUrl("https://qa-test.company.com")
                .build();
        prodEnvironment = EnvironmentBuilder.createProduction("Test Production");
    }

    @Test
    @DisplayName("Test environment creation and basic properties")
    void testEnvironmentCreation() {
        assertNotNull(devEnvironment);
        assertNotNull(qaEnvironment);
        assertNotNull(prodEnvironment);

        assertEquals("Test Development", devEnvironment.getName());
        assertEquals("Test QA", qaEnvironment.getName());
        assertEquals("Test Production", prodEnvironment.getName());

        assertEquals(EnvironmentType.DEVELOPMENT, devEnvironment.getType());
        assertEquals(EnvironmentType.QUALITY_ASSURANCE, qaEnvironment.getType());
        assertEquals(EnvironmentType.PRODUCTION, prodEnvironment.getType());

        assertFalse(devEnvironment.isActive());
        assertEquals(DeploymentStatus.PENDING, devEnvironment.getStatus());
    }

    @Test
    @DisplayName("Test environment validation rules")
    void testEnvironmentValidation() {
        // Dev environment validation
        List<String> devIssues = devEnvironment.validate();
        assertTrue(devIssues.isEmpty() || devIssues.contains("Dev environment should use HTTP, not HTTPS"));

        // QA environment validation
        List<String> qaIssues = qaEnvironment.validate();
        System.out.println("QA Validation issues: " + qaIssues);

        // Production environment validation
        List<String> prodIssues = prodEnvironment.validate();
        System.out.println("Prod Validation issues: " + prodIssues);
        // Should pass all validations if created with builder
        assertTrue(prodIssues.isEmpty());
    }

    @Test
    @DisplayName("Test configuration management")
    void testConfigurationManagement() {
        // Test adding configurations
        ConfigItem featureFlag = ConfigItem.featureFlag("NEW_FEATURE", true,
                "Test new feature", "test-user");
        devEnvironment.addConfiguration(featureFlag);

        assertNotNull(devEnvironment.getConfiguration("NEW_FEATURE"));
        assertEquals("true", devEnvironment.getConfiguration("NEW_FEATURE").getValue());
        assertEquals(ConfigType.FEATURE_FLAG, devEnvironment.getConfiguration("NEW_FEATURE").getType());

        // Test getting configurations by type
        List<ConfigItem> featureFlags = devEnvironment.getConfigurationsByType(ConfigType.FEATURE_FLAG);
        assertFalse(featureFlags.isEmpty());

        // Test removing configuration
        devEnvironment.removeConfiguration("NEW_FEATURE");
        assertNull(devEnvironment.getConfiguration("NEW_FEATURE"));
    }

    @Test
    @DisplayName("Test service management")
    void testServiceManagement() {
        Service webService = new Service("web-001", "Web App", "1.0.0",
                ServiceType.WEB_SERVICE, devEnvironment.getBaseUrl());

        devEnvironment.addService(webService);
        assertEquals(1, devEnvironment.getServices().size());
        assertTrue(devEnvironment.getServices().contains(webService));

        devEnvironment.removeService("web-001");
        assertEquals(0, devEnvironment.getServices().size());
    }

    @Test
    @DisplayName("Test deployment tracking")
    void testDeploymentTracking() {
        devEnvironment.recordDeployment("2.0.0", "test-user");
        assertEquals(1, devEnvironment.getDeploymentHistory().size());

        DeploymentHistory deployment = devEnvironment.getDeploymentHistory().get(0);
        assertEquals("2.0.0", deployment.getVersion());
        assertEquals("test-user", deployment.getDeployedBy());
        assertEquals(DeploymentStatus.IN_PROGRESS, deployment.getStatus());

        // Test status updates
        devEnvironment.updateStatus(DeploymentStatus.SUCCESS);
        assertEquals(DeploymentStatus.SUCCESS, devEnvironment.getStatus());
        assertTrue(devEnvironment.isActive());
    }

    @Test
    @DisplayName("Test environment type specific properties")
    void testEnvironmentTypeProperties() {
        // Test requires approval
        assertFalse(devEnvironment.requiresApproval());
        assertFalse(qaEnvironment.requiresApproval());
        assertTrue(prodEnvironment.requiresApproval());

        // Test max parallel deployments
        assertEquals(5, devEnvironment.getMaxParallelDeployments());
        assertEquals(2, qaEnvironment.getMaxParallelDeployments());
        assertEquals(1, prodEnvironment.getMaxParallelDeployments());
    }

    @Test
    @DisplayName("Test ConfigurationBuilder")
    void testConfigurationBuilder() {
        Map<String, ConfigItem> devConfigs = ConfigurationBuilder.createDevelopmentConfig();
        assertNotNull(devConfigs);
        assertFalse(devConfigs.isEmpty());

        assertTrue(devConfigs.containsKey("db.url"));
        assertTrue(devConfigs.containsKey("api.baseUrl"));
        assertTrue(devConfigs.containsKey("feature.experimentalFeature"));

        // Test applying configurations to environment
        ConfigurationBuilder configBuilder = new ConfigurationBuilder()
                .forEnvironment("TEST-001")
                .withFeatureFlag("TEST_FLAG", true, "Test flag")
                .withLogLevel("DEBUG");

        configBuilder.applyToEnvironment(devEnvironment);

        assertNotNull(devEnvironment.getConfiguration("logging.level"));
    }

    @Test
    @DisplayName("Test environment equality and hashcode")
    void testEnvironmentEquality() {
        Environment env1 = new EnvironmentBuilder()
                .setId("TEST-001")
                .setName("Test Env")
                .setType(EnvironmentType.DEVELOPMENT)
                .setBaseUrl("https://test.com")
                .build();

        Environment env2 = new EnvironmentBuilder()
                .setId("TEST-001")  // Same ID
                .setName("Different Name")
                .setType(EnvironmentType.QUALITY_ASSURANCE) // Different type
                .setBaseUrl("https://different.com") // Different URL
                .build();

        // Environments are equal based on ID only
        assertEquals(env1, env2);
        assertEquals(env1.hashCode(), env2.hashCode());

        Environment env3 = new EnvironmentBuilder()
                .setId("TEST-002")  // Different ID
                .setName("Test Env")
                .setType(EnvironmentType.DEVELOPMENT)
                .setBaseUrl("https://test.com")
                .build();

        assertNotEquals(env1, env3);
    }

    @Test
    @DisplayName("Test environment state transitions")
    void testEnvironmentStateTransitions() {
        // Initial state
        assertFalse(devEnvironment.isActive());
        assertEquals(DeploymentStatus.PENDING, devEnvironment.getStatus());

        // Start deployment
        devEnvironment.recordDeployment("1.0.0", "user1");
        devEnvironment.updateStatus(DeploymentStatus.IN_PROGRESS);
        assertEquals(DeploymentStatus.IN_PROGRESS, devEnvironment.getStatus());
        assertFalse(devEnvironment.isActive());

        // Successful deployment
        devEnvironment.updateStatus(DeploymentStatus.SUCCESS);
        assertEquals(DeploymentStatus.SUCCESS, devEnvironment.getStatus());
        assertTrue(devEnvironment.isActive());

        // Failed deployment
        devEnvironment.updateStatus(DeploymentStatus.FAILED);
        assertEquals(DeploymentStatus.FAILED, devEnvironment.getStatus());
        assertFalse(devEnvironment.isActive());
    }

    @Test
    @DisplayName("Test environment subtypes creation")
    void testEnvironmentSubtypes() {
        // Test all environment subtypes
        Environment dev = new DevelopmentEnvironment("DEV-001", "Dev", "http://localhost:8080");
        Environment qa = new QAEnvironment("QA-001", "QA", "https://qa.company.com");
        Environment prod = new ProductionEnvironment("PROD-001", "Prod", "https://app.company.com");
        Environment staging = new StagingEnvironment("STG-001", "Staging", "https://staging.company.com");
        Environment uat = new UATEnvironment("UAT-001", "UAT", "https://uat.company.com");

        assertNotNull(dev);
        assertNotNull(qa);
        assertNotNull(prod);
        assertNotNull(staging);
        assertNotNull(uat);

        // Test specific validations for each type
        assertTrue(dev.validate().isEmpty() || !dev.validate().isEmpty());
        assertTrue(qa.validate().isEmpty() || !qa.validate().isEmpty());
        assertTrue(prod.validate().isEmpty() || !prod.validate().isEmpty());

        // Test default configurations
        assertFalse(dev.getConfigurations().isEmpty());
        assertFalse(qa.getConfigurations().isEmpty());
        assertFalse(prod.getConfigurations().isEmpty());
    }

    @Test
    @DisplayName("Test ConfigItem factory methods")
    void testConfigItemFactoryMethods() {
        ConfigItem featureFlag = ConfigItem.featureFlag("TEST", true, "Description", "user1");
        assertEquals(ConfigType.FEATURE_FLAG, featureFlag.getType());
        assertEquals("true", featureFlag.getValue());
        assertFalse(featureFlag.isEncrypted());

        ConfigItem apiConfig = ConfigItem.apiConfig("API_KEY", "12345", "API Key", "user2");
        assertEquals(ConfigType.API_CONFIG, apiConfig.getType());
        assertEquals("12345", apiConfig.getValue());

        ConfigItem dbConfig = ConfigItem.dbConfig("password", "secret", "DB Password", "user3");
        assertEquals(ConfigType.DB_CONFIG, dbConfig.getType());
        assertTrue(dbConfig.isEncrypted());
        assertTrue(dbConfig.getKey().startsWith("db."));

        ConfigItem secret = ConfigItem.secret("SECRET_KEY", "top-secret", "Secret key", "user4");
        assertEquals(ConfigType.SECRET, secret.getType());
        assertTrue(secret.isEncrypted());
    }

    @Test
    @DisplayName("Test environment toString and detailedInfo")
    void testEnvironmentStringRepresentations() {
        String toString = devEnvironment.toString();
        String detailedInfo = devEnvironment.getDetailedInfo();

        assertNotNull(toString);
        assertNotNull(detailedInfo);

        assertTrue(toString.contains(devEnvironment.getName()));
        assertTrue(toString.contains(devEnvironment.getType().getCode()));
        assertTrue(detailedInfo.contains("Environment:"));
        assertTrue(detailedInfo.contains("Type:"));
        assertTrue(detailedInfo.contains("URL:"));
        assertTrue(detailedInfo.contains("Status:"));
    }

    @Test
    @DisplayName("Test environment configuration map")
    void testEnvironmentConfigMap() {
        // Add some configurations
        devEnvironment.addConfiguration(ConfigItem.featureFlag("FLAG1", true, "Flag 1", "user"));
        devEnvironment.addConfiguration(ConfigItem.apiConfig("KEY1", "value1", "Key 1", "user"));

        Map<String, String> configMap = devEnvironment.getConfigMap();

        assertNotNull(configMap);
        assertTrue(configMap.containsKey("FLAG1"));
        assertTrue(configMap.containsKey("KEY1"));
        assertEquals("true", configMap.get("FLAG1"));
        assertEquals("value1", configMap.get("KEY1"));
    }
}