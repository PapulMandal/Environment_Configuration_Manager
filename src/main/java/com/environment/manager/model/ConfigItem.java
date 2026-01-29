package com.environment.manager.model;

import java.time.LocalDateTime;

public class ConfigItem {
    private final String key;
    private final String value;
    private final String description;
    private final ConfigType type;
    private final LocalDateTime lastModified;
    private final String modifiedBy;
    private final boolean encrypted;

    public ConfigItem(String key, String value, String description, ConfigType type,
                      LocalDateTime lastModified, String modifiedBy, boolean encrypted) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.type = type;
        this.lastModified = lastModified;
        this.modifiedBy = modifiedBy;
        this.encrypted = encrypted;
    }

    // Static factory methods
    public static ConfigItem featureFlag(String key, boolean value, String description, String modifiedBy) {
        return new ConfigItem(key, String.valueOf(value), description, ConfigType.FEATURE_FLAG,
                LocalDateTime.now(), modifiedBy, false);
    }

    public static ConfigItem apiConfig(String key, String value, String description, String modifiedBy) {
        return new ConfigItem(key, value, description, ConfigType.API_CONFIG,
                LocalDateTime.now(), modifiedBy, false);
    }

    public static ConfigItem dbConfig(String key, String value, String description, String modifiedBy) {
        return new ConfigItem("db." + key, value, description, ConfigType.DB_CONFIG,
                LocalDateTime.now(), modifiedBy, true);
    }

    public static ConfigItem secret(String key, String value, String description, String modifiedBy) {
        return new ConfigItem(key, value, description, ConfigType.SECRET,
                LocalDateTime.now(), modifiedBy, true);
    }

    public static ConfigItem securityConfig(String key, String value, String description, String modifiedBy) {
        return new ConfigItem("security." + key, value, description, ConfigType.SECURITY_CONFIG,
                LocalDateTime.now(), modifiedBy, false);
    }

    // Getters
    public String getKey() { return key; }
    public String getValue() { return value; }
    public String getDescription() { return description; }
    public ConfigType getType() { return type; }
    public LocalDateTime getLastModified() { return lastModified; }
    public String getModifiedBy() { return modifiedBy; }
    public boolean isEncrypted() { return encrypted; }

    @Override
    public String toString() {
        return String.format("%s=%s [%s] - %s", key,
                encrypted ? "***ENCRYPTED***" : value,
                type.getDisplayName(), description);
    }
}
