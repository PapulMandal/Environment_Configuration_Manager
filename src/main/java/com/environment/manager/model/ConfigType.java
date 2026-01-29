package com.environment.manager.model;

public enum ConfigType {
    FEATURE_FLAG("Feature Flag", "boolean"),
    API_CONFIG("API Configuration", "string"),
    DB_CONFIG("Database Configuration", "string"),
    SECURITY_CONFIG("Security Setting", "string"),
    GENERAL("General Setting", "string"),
    NUMERIC("Numeric Value", "number"),
    SECRET("Secret Value", "password");

    private final String displayName;
    private final String dataType;

    ConfigType(String displayName, String dataType) {
        this.displayName = displayName;
        this.dataType = dataType;
    }

    public String getDisplayName() { return displayName; }
    public String getDataType() { return dataType; }
}