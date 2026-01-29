package com.environment.manager.model;

public enum DeploymentStatus {
    PENDING("⏳", "Pending", false),
    IN_PROGRESS("⚡", "In Progress", false),
    SUCCESS("✅", "Success", true),
    FAILED("❌", "Failed", true),
    ROLLED_BACK("↩️", "Rolled Back", true);

    private final String emoji;
    private final String displayName;
    private final boolean isCompleted;

    DeploymentStatus(String emoji, String displayName, boolean isCompleted) {
        this.emoji = emoji;
        this.displayName = displayName;
        this.isCompleted = isCompleted;
    }

    public String getEmoji() { return emoji; }
    public String getDisplayName() { return displayName; }
    public boolean isCompleted() { return isCompleted; }
    public boolean isSuccessful() { return this == SUCCESS; }
}