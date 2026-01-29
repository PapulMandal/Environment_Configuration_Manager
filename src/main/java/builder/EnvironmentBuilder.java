package builder;

import com.environment.manager.model.*;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentBuilder {
    private String id;
    private String name;
    private EnvironmentType type;
    private String baseUrl;
    private String databaseUrl;
    private String apiEndpoint;
    private final Map<String, ConfigItem> configItems = new HashMap<>();

    public EnvironmentBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public EnvironmentBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public EnvironmentBuilder setType(EnvironmentType type) {
        this.type = type;
        return this;
    }

    public EnvironmentBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public EnvironmentBuilder setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
        return this;
    }

    public EnvironmentBuilder setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
        return this;
    }

    public EnvironmentBuilder addConfigItem(ConfigItem configItem) {
        this.configItems.put(configItem.getKey(), configItem);
        return this;
    }

    public EnvironmentBuilder withDatabaseConfig(String url, String username, String password, String modifiedBy) {
        this.databaseUrl = url;
        addConfigItem(ConfigItem.dbConfig("url", url, "Database URL", modifiedBy));
        addConfigItem(ConfigItem.dbConfig("username", username, "Database username", modifiedBy));
        addConfigItem(ConfigItem.dbConfig("password", password, "Database password", modifiedBy));
        return this;
    }

    public EnvironmentBuilder withApiConfig(String baseUrl, String apiKey, int timeout, String modifiedBy) {
        this.apiEndpoint = baseUrl;
        addConfigItem(ConfigItem.apiConfig("baseUrl", baseUrl, "API base URL", modifiedBy));
        addConfigItem(ConfigItem.secret("apiKey", apiKey, "API key", modifiedBy));
        addConfigItem(ConfigItem.apiConfig("timeout", String.valueOf(timeout), "API timeout in seconds", modifiedBy));
        return this;
    }

    public EnvironmentBuilder withFeatureFlag(String flagName, boolean enabled, String description, String modifiedBy) {
        addConfigItem(ConfigItem.featureFlag(flagName, enabled, description, modifiedBy));
        return this;
    }

    public EnvironmentBuilder withLogLevel(String level, String modifiedBy) {
        addConfigItem(ConfigItem.apiConfig("LOG_LEVEL", level, "Logging level", modifiedBy));
        return this;
    }

    public Environment build() {
        Environment environment;

        switch (type) {
            case DEVELOPMENT:
                environment = new DevelopmentEnvironment(id, name, baseUrl);
                break;
            case QUALITY_ASSURANCE:
                environment = new QAEnvironment(id, name, baseUrl);
                break;
            case USER_ACCEPTANCE:
                environment = new UATEnvironment(id, name, baseUrl);
                break;
            case STAGING:
                environment = new StagingEnvironment(id, name, baseUrl);
                break;
            case PRODUCTION:
                environment = new ProductionEnvironment(id, name, baseUrl);
                break;
            default:
                throw new IllegalArgumentException("Unknown environment type: " + type);
        }

        if (databaseUrl != null) {
            environment.setDatabaseUrl(databaseUrl);
        }

        if (apiEndpoint != null) {
            environment.setApiEndpoint(apiEndpoint);
        }

        // Add custom configurations
        configItems.values().forEach(environment::addConfiguration);

        return environment;
    }

    // Convenience methods for common environment types
    public static Environment createDevelopment(String name) {
        return new EnvironmentBuilder()
                .setId("DEV-" + System.currentTimeMillis())
                .setName(name)
                .setType(EnvironmentType.DEVELOPMENT)
                .setBaseUrl("http://localhost:8080")
                .withDatabaseConfig(
                        "jdbc:mysql://localhost:3306/dev_db",
                        "dev_user",
                        "dev_pass",
                        "system"
                )
                .withApiConfig(
                        "http://localhost:8080/api",
                        "dev-api-key-123",
                        30,
                        "system"
                )
                .withFeatureFlag("experimentalFeature", true, "Experimental feature flag", "system")
                .withFeatureFlag("debugMode", true, "Debug mode enabled", "system")
                .withLogLevel("DEBUG", "system")
                .build();
    }

    public static Environment createQA(String name) {
        return new EnvironmentBuilder()
                .setId("QA-" + System.currentTimeMillis())
                .setName(name)
                .setType(EnvironmentType.QUALITY_ASSURANCE)
                .setBaseUrl("https://qa.company.com")
                .withDatabaseConfig(
                        "jdbc:mysql://qa-db.company.com:3306/qa_db",
                        "qa_user",
                        "qa_secure_pass",
                        "qa-team"
                )
                .withApiConfig(
                        "https://qa-api.company.com/api",
                        "qa-api-key-456",
                        60,
                        "qa-team"
                )
                .withFeatureFlag("experimentalFeature", false, "Experimental feature disabled", "qa-team")
                .withFeatureFlag("debugMode", false, "Debug mode disabled", "qa-team")
                .withLogLevel("INFO", "qa-team")
                .build();
    }

    public static Environment createProduction(String name) {
        return new EnvironmentBuilder()
                .setId("PROD-" + System.currentTimeMillis())
                .setName(name)
                .setType(EnvironmentType.PRODUCTION)
                .setBaseUrl("https://app.company.com")
                .withDatabaseConfig(
                        "jdbc:mysql://prod-db-cluster.company.com:3306/prod_db",
                        "prod_user",
                        "prod_encrypted_pass_xyz",
                        "devops"
                )
                .withApiConfig(
                        "https://api.company.com/v1",
                        "prod-api-key-secure-789",
                        120,
                        "devops"
                )
                .withFeatureFlag("newUi", false, "New UI disabled", "devops")
                .withFeatureFlag("maintenanceMode", false, "Maintenance mode disabled", "devops")
                .withLogLevel("WARN", "devops")
                .addConfigItem(ConfigItem.securityConfig("sslEnabled", "true", "SSL enabled", "devops"))
                .addConfigItem(ConfigItem.securityConfig("corsOrigin", "https://company.com", "CORS origin", "devops"))
                .addConfigItem(ConfigItem.apiConfig("cache.ttl", "3600", "Cache TTL in seconds", "devops"))
                .build();
    }
}