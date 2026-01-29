package com.environment.manager.observer;

import com.environment.manager.model.Environment;
import com.environment.manager.model.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer for sending notifications about environment and deployment events.
 * Supports multiple notification channels (email, slack, etc.)
 */
public class NotificationObserver {
    private final List<NotificationChannel> channels = new ArrayList<>();
    private boolean enabled = true;

    public NotificationObserver() {
        // Initialize with default channels
        channels.add(new EmailNotificationChannel());
        channels.add(new SlackNotificationChannel());
    }

    public void addChannel(NotificationChannel channel) {
        channels.add(channel);
    }

    public void removeChannel(NotificationChannel channel) {
        channels.remove(channel);
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    // Deployment Events
    public void onDeploymentStarted(Environment environment, Service service, String version, String deployedBy) {
        if (!enabled) return;

        String message = String.format(
                "🚀 Deployment Started\n" +
                        "Environment: %s (%s)\n" +
                        "Service: %s v%s\n" +
                        "Deployed by: %s\n" +
                        "Time: %s",
                environment.getName(),
                environment.getType(),
                service.getName(),
                version,
                deployedBy,
                java.time.LocalDateTime.now()
        );

        sendNotification("DEPLOYMENT_STARTED", message, NotificationPriority.INFO);
    }

    public void onDeploymentCompleted(Environment environment, Service service, String version, boolean success) {
        if (!enabled) return;

        String status = success ? "✅ SUCCESS" : "❌ FAILED";
        String message = String.format(
                "%s Deployment Completed\n" +
                        "Environment: %s\n" +
                        "Service: %s v%s\n" +
                        "Status: %s\n" +
                        "Time: %s",
                success ? "✅" : "❌",
                environment.getName(),
                service.getName(),
                version,
                status,
                java.time.LocalDateTime.now()
        );

        NotificationPriority priority = success ? NotificationPriority.INFO : NotificationPriority.ERROR;
        sendNotification("DEPLOYMENT_COMPLETED", message, priority);
    }

    public void onDeploymentFailed(Environment environment, Service service, String version, String error) {
        if (!enabled) return;

        String message = String.format(
                "❌ Deployment Failed\n" +
                        "Environment: %s\n" +
                        "Service: %s v%s\n" +
                        "Error: %s\n" +
                        "Time: %s",
                environment.getName(),
                service.getName(),
                version,
                error,
                java.time.LocalDateTime.now()
        );

        sendNotification("DEPLOYMENT_FAILED", message, NotificationPriority.ERROR);
    }

    // Environment Events
    public void onEnvironmentCreated(Environment environment, String createdBy) {
        if (!enabled) return;

        String message = String.format(
                "🆕 Environment Created\n" +
                        "Name: %s\n" +
                        "Type: %s\n" +
                        "URL: %s\n" +
                        "Created by: %s\n" +
                        "Time: %s",
                environment.getName(),
                environment.getType(),
                environment.getBaseUrl(),
                createdBy,
                java.time.LocalDateTime.now()
        );

        sendNotification("ENVIRONMENT_CREATED", message, NotificationPriority.INFO);
    }

    public void onEnvironmentUpdated(Environment environment, String updatedBy, String changes) {
        if (!enabled) return;

        String message = String.format(
                "✏️ Environment Updated\n" +
                        "Name: %s\n" +
                        "Changes: %s\n" +
                        "Updated by: %s\n" +
                        "Time: %s",
                environment.getName(),
                changes,
                updatedBy,
                java.time.LocalDateTime.now()
        );

        sendNotification("ENVIRONMENT_UPDATED", message, NotificationPriority.INFO);
    }

    public void onEnvironmentError(Environment environment, String error) {
        if (!enabled) return;

        String message = String.format(
                "⚠️ Environment Error\n" +
                        "Environment: %s\n" +
                        "Error: %s\n" +
                        "Time: %s",
                environment.getName(),
                error,
                java.time.LocalDateTime.now()
        );

        sendNotification("ENVIRONMENT_ERROR", message, NotificationPriority.WARNING);
    }

    // Rollback Events
    public void onRollbackStarted(Environment environment, String serviceId, String rolledBackBy) {
        if (!enabled) return;

        String message = String.format(
                "↩️ Rollback Started\n" +
                        "Environment: %s\n" +
                        "Service ID: %s\n" +
                        "Initiated by: %s\n" +
                        "Time: %s",
                environment.getName(),
                serviceId,
                rolledBackBy,
                java.time.LocalDateTime.now()
        );

        sendNotification("ROLLBACK_STARTED", message, NotificationPriority.WARNING);
    }

    public void onRollbackCompleted(Environment environment, String serviceId, boolean success) {
        if (!enabled) return;

        String message = String.format(
                "%s Rollback Completed\n" +
                        "Environment: %s\n" +
                        "Service ID: %s\n" +
                        "Status: %s\n" +
                        "Time: %s",
                success ? "✅" : "❌",
                environment.getName(),
                serviceId,
                success ? "SUCCESS" : "FAILED",
                java.time.LocalDateTime.now()
        );

        NotificationPriority priority = success ? NotificationPriority.INFO : NotificationPriority.ERROR;
        sendNotification("ROLLBACK_COMPLETED", message, priority);
    }

    // Validation Events
    public void onValidationFailed(Environment environment, List<String> issues) {
        if (!enabled) return;

        StringBuilder issuesText = new StringBuilder();
        for (int i = 0; i < Math.min(issues.size(), 5); i++) {
            issuesText.append("\n  • ").append(issues.get(i));
        }

        if (issues.size() > 5) {
            issuesText.append("\n  ... and ").append(issues.size() - 5).append(" more issues");
        }

        String message = String.format(
                "❌ Validation Failed\n" +
                        "Environment: %s\n" +
                        "Issues found: %d\n" +
                        "Sample issues:%s\n" +
                        "Time: %s",
                environment.getName(),
                issues.size(),
                issuesText.toString(),
                java.time.LocalDateTime.now()
        );

        sendNotification("VALIDATION_FAILED", message, NotificationPriority.WARNING);
    }

    // Health Check Events
    public void onHealthCheckWarning(Environment environment, String warning) {
        if (!enabled) return;

        String message = String.format(
                "⚠️ Health Check Warning\n" +
                        "Environment: %s\n" +
                        "Warning: %s\n" +
                        "Time: %s",
                environment.getName(),
                warning,
                java.time.LocalDateTime.now()
        );

        sendNotification("HEALTH_CHECK_WARNING", message, NotificationPriority.WARNING);
    }

    public void onHealthCheckCritical(Environment environment, String error) {
        if (!enabled) return;

        String message = String.format(
                "🚨 Health Check Critical\n" +
                        "Environment: %s\n" +
                        "Error: %s\n" +
                        "Time: %s\n" +
                        "ACTION REQUIRED!",
                environment.getName(),
                error,
                java.time.LocalDateTime.now()
        );

        sendNotification("HEALTH_CHECK_CRITICAL", message, NotificationPriority.CRITICAL);
    }

    // Manual notification method
    public void sendManualNotification(String title, String message, NotificationPriority priority) {
        if (!enabled) return;

        String fullMessage = String.format(
                "📢 %s\n%s\nTime: %s",
                title,
                message,
                java.time.LocalDateTime.now()
        );

        sendNotification("MANUAL_NOTIFICATION", fullMessage, priority);
    }

    // Private helper method
    private void sendNotification(String eventType, String message, NotificationPriority priority) {
        for (NotificationChannel channel : channels) {
            try {
                if (channel.isEnabled() && channel.supportsPriority(priority)) {
                    channel.sendNotification(eventType, message, priority);
                }
            } catch (Exception e) {
                System.err.println("Failed to send notification via channel " +
                        channel.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    // Channel interfaces and implementations

    public interface NotificationChannel {
        void sendNotification(String eventType, String message, NotificationPriority priority);
        boolean supportsPriority(NotificationPriority priority);
        boolean isEnabled();
        void setEnabled(boolean enabled);
    }

    public enum NotificationPriority {
        INFO,       // Informational messages
        WARNING,    // Warning messages
        ERROR,      // Error messages
        CRITICAL    // Critical/urgent messages
    }

    // Example implementation: Email Notification Channel
    public static class EmailNotificationChannel implements NotificationChannel {
        private boolean enabled = true;
        private String recipient = "devops@company.com";

        @Override
        public void sendNotification(String eventType, String message, NotificationPriority priority) {
            System.out.println("[EMAIL] Priority: " + priority + " | To: " + recipient);
            System.out.println("Subject: [" + eventType + "] Environment Manager Notification");
            System.out.println("Body:\n" + message + "\n");

            // In real implementation, this would send actual email
            // using JavaMail or similar library
        }

        @Override
        public boolean supportsPriority(NotificationPriority priority) {
            // Email supports all priority levels
            return true;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }
    }

    // Example implementation: Slack Notification Channel
    public static class SlackNotificationChannel implements NotificationChannel {
        private boolean enabled = true;
        private String webhookUrl = "https://hooks.slack.com/services/...";
        private String channel = "#deployments";

        @Override
        public void sendNotification(String eventType, String message, NotificationPriority priority) {
            String icon = getIconForPriority(priority);
            String color = getColorForPriority(priority);

            System.out.println("[SLACK] Channel: " + channel);
            System.out.println("Webhook: " + webhookUrl);
            System.out.println("Message: " + icon + " " + message + "\n");

            // In real implementation, this would POST to Slack webhook
            // with proper JSON payload
        }

        @Override
        public boolean supportsPriority(NotificationPriority priority) {
            // Slack supports all priority levels
            return true;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        private String getIconForPriority(NotificationPriority priority) {
            switch (priority) {
                case INFO: return "ℹ️";
                case WARNING: return "⚠️";
                case ERROR: return "❌";
                case CRITICAL: return "🚨";
                default: return "📢";
            }
        }

        private String getColorForPriority(NotificationPriority priority) {
            switch (priority) {
                case INFO: return "#36a64f";      // Green
                case WARNING: return "#ff9900";   // Orange
                case ERROR: return "#ff0000";     // Red
                case CRITICAL: return "#8b0000";  // Dark Red
                default: return "#757575";        // Gray
            }
        }

        public void setWebhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }
    }

    // Example implementation: Console Notification Channel (for debugging)
    public static class ConsoleNotificationChannel implements NotificationChannel {
        private boolean enabled = true;

        @Override
        public void sendNotification(String eventType, String message, NotificationPriority priority) {
            String prefix;
            switch (priority) {
                case CRITICAL: prefix = "[🚨 CRITICAL] "; break;
                case ERROR: prefix = "[❌ ERROR] "; break;
                case WARNING: prefix = "[⚠️ WARNING] "; break;
                default: prefix = "[ℹ️ INFO] ";
            }

            System.out.println(prefix + "[" + eventType + "] " + message);
        }

        @Override
        public boolean supportsPriority(NotificationPriority priority) {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}