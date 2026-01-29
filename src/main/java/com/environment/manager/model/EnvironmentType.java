package com.environment.manager.model;

public enum EnvironmentType {
    DEVELOPMENT("DEV", "Development", 10, false),
    QUALITY_ASSURANCE("QA", "Quality Assurance", 5, false),
    USER_ACCEPTANCE("UAT", "User Acceptance Testing", 3, true),
    STAGING("STG", "Staging", 2, true),
    PRODUCTION("PROD", "Production", 1, true);

    private final String code;
    private final String description;
    private final int priority;
    private final boolean requiresApproval;

    EnvironmentType(String code, String description, int priority, boolean requiresApproval) {
        this.code = code;
        this.description = description;
        this.priority = priority;
        this.requiresApproval = requiresApproval;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
    public int getPriority() { return priority; }
    public boolean requiresApproval() { return requiresApproval; }

    public static EnvironmentType fromCode(String code) {
        for (EnvironmentType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown environment type code: " + code);
    }
}
