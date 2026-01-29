package builder;

import com.environment.manager.model.ConfigItem;
import com.environment.manager.model.Configuration;
import com.environment.manager.model.Environment;
import com.environment.manager.model.ConfigType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationBuilder {
    private final Map<String, ConfigItem> configItems = new HashMap<>();
    private String environmentId;
    private String configVersion = "1.0";
    private boolean isEncrypted = false;
    private String lastModifiedBy = "system";

    public ConfigurationBuilder forEnvironment(String environmentId) {
        this.environmentId = environmentId;
        return this;
    }

    public ConfigurationBuilder withDatabaseConfig(String url, String username, String password) {
        configItems.put("db.url",
                new ConfigItem("db.url", url, "Database URL",
                        ConfigType.DB_CONFIG,
                        LocalDateTime.now(), lastModifiedBy, isEncrypted));
        configItems.put("db.username",
                new ConfigItem("db.username", username, "Database username",
                        ConfigType.DB_CONFIG,
                        LocalDateTime.now(), lastModifiedBy, isEncrypted));
        configItems.put("db.password",
                new ConfigItem("db.password", password, "Database password",
                        ConfigType.SECRET,
                        LocalDateTime.now(), lastModifiedBy, true));
        return this;
    }

    public ConfigurationBuilder withApiConfig(String baseUrl, String apiKey, int timeout) {
        configItems.put("api.baseUrl",
                new ConfigItem("api.baseUrl", baseUrl, "API base URL",
                        ConfigType.API_CONFIG,
                        LocalDateTime.now(), lastModifiedBy, false));
        configItems.put("api.key",
                new ConfigItem("api.key", apiKey, "API key",
                        ConfigType.SECRET,
                        LocalDateTime.now(), lastModifiedBy, true));
        configItems.put("api.timeout",
                new ConfigItem("api.timeout", String.valueOf(timeout), "API timeout in seconds",
                        ConfigType.API_CONFIG,
                        LocalDateTime.now(), lastModifiedBy, false));
        return this;
    }

    public ConfigurationBuilder withFeatureFlag(String flagName, boolean enabled, String description) {
        configItems.put("feature." + flagName,
                new ConfigItem("feature." + flagName, String.valueOf(enabled), description,
                        ConfigType.FEATURE_FLAG,
                        LocalDateTime.now(), lastModifiedBy, false));
        return this;
    }

    public ConfigurationBuilder withLogLevel(String level) {
        configItems.put("logging.level",
                new ConfigItem("logging.level", level, "Logging level",
                        ConfigType.GENERAL,
                        LocalDateTime.now(), lastModifiedBy, false));
        return this;
    }

    public ConfigurationBuilder withCustomConfig(String key, String value, String description,
                                                 ConfigType type) {
        configItems.put(key,
                new ConfigItem(key, value, description, type,
                        LocalDateTime.now(), lastModifiedBy, type == ConfigType.SECRET));
        return this;
    }

    public ConfigurationBuilder withVersion(String version) {
        this.configVersion = version;
        return this;
    }

    public ConfigurationBuilder encrypted() {
        this.isEncrypted = true;
        return this;
    }

    public ConfigurationBuilder modifiedBy(String user) {
        this.lastModifiedBy = user;
        return this;
    }

    public Map<String, ConfigItem> buildConfigMap() {
        if (environmentId == null || environmentId.isEmpty()) {
            throw new IllegalStateException("Environment ID must be specified");
        }
        return new HashMap<>(configItems);
    }

    public Configuration build() {
        if (environmentId == null || environmentId.isEmpty()) {
            throw new IllegalStateException("Environment ID must be specified");
        }
        // Return a Configuration object - note: this is a simplified implementation
        // You might need to adjust based on your Configuration class structure
        return new Configuration("config-" + environmentId, configVersion,
                "Configuration for " + environmentId, Configuration.ConfigType.GENERAL);
    }

    public void applyToEnvironment(Environment environment) {
        configItems.values().forEach(environment::addConfiguration);
        environment.setCurrentVersion(configVersion);
    }

    // Convenience methods for common configurations
    public static Map<String, ConfigItem> createDevelopmentConfig() {
        return new ConfigurationBuilder()
                .forEnvironment("DEV-001")
                .withDatabaseConfig(
                        "jdbc:mysql://localhost:3306/dev_db",
                        "dev_user",
                        "dev_pass"
                )
                .withApiConfig(
                        "http://localhost:8080/api",
                        "dev-api-key-123",
                        30
                )
                .withFeatureFlag("experimentalFeature", true, "Experimental feature")
                .withFeatureFlag("debugMode", true, "Debug mode enabled")
                .withLogLevel("DEBUG")
                .withVersion("1.0-dev")
                .modifiedBy("system")
                .buildConfigMap();
    }

    public static Configuration createQAConfig() {
        return new ConfigurationBuilder()
                .forEnvironment("QA-001")
                .withDatabaseConfig(
                        "jdbc:mysql://qa-db.company.com:3306/qa_db",
                        "qa_user",
                        "qa_pass"
                )
                .withApiConfig(
                        "https://qa-api.company.com/api",
                        "qa-api-key-456",
                        60
                )
                .withFeatureFlag("experimentalFeature", false, "Experimental feature disabled")
                .withFeatureFlag("debugMode", false, "Debug mode disabled")
                .withLogLevel("INFO")
                .withVersion("1.0-qa")
                .modifiedBy("qa-team")
                .build();
    }

    public static Map<String, ConfigItem> createProductionConfig() {
        return new ConfigurationBuilder()
                .forEnvironment("PROD-001")
                .withDatabaseConfig(
                        "jdbc:mysql://prod-db-cluster.company.com:3306/prod_db",
                        "prod_user",
                        "prod_encrypted_pass_xyz"
                )
                .withApiConfig(
                        "https://api.company.com/v1",
                        "prod-api-key-secure-789",
                        120
                )
                .withFeatureFlag("newUi", false, "New UI disabled")
                .withFeatureFlag("maintenanceMode", false, "Maintenance mode disabled")
                .withLogLevel("WARN")
                .withCustomConfig("security.sslEnabled", "true", "SSL enabled",
                        ConfigType.SECURITY_CONFIG)
                .withCustomConfig("security.corsOrigin", "https://company.com", "CORS origin",
                        ConfigType.SECURITY_CONFIG)
                .withCustomConfig("cache.ttl", "3600", "Cache TTL",
                        ConfigType.NUMERIC)
                .encrypted()
                .withVersion("1.0-prod")
                .modifiedBy("devops-team")
                .buildConfigMap();
    }
}