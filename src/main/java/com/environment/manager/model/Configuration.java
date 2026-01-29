package com.environment.manager.model;

public class Configuration {
    private final String key;
    private final String value;
    private final String description;
    private final ConfigType type;

    public enum ConfigType {
        FEATURE_FLAG, API_CONFIG, DB_CONFIG, SECURITY_CONFIG, GENERAL
    }

    public Configuration(String key, String value, String description, ConfigType type) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.type = type;
    }

    // Static factory methods
    public static Configuration featureFlag(String key, String value, String description) {
        return new Configuration(key, value, description, ConfigType.FEATURE_FLAG);
    }

    public static Configuration apiConfig(String key, String value, String description) {
        return new Configuration(key, value, description, ConfigType.API_CONFIG);
    }

    public static Configuration securityConfig(String key, String value, String description) {
        return new Configuration(key, value, description, ConfigType.SECURITY_CONFIG);
    }

    // Getter methods
    public String getKey() { return key; }
    public String getValue() { return value; }
    public String getDescription() { return description; }
    public ConfigType getType() { return type; }

    @Override
    public String toString() {
        return String.format("%s=%s (%s)", key, value, description);
    }
}