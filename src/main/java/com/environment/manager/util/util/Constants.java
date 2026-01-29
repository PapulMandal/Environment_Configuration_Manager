package com.environment.manager.util.util;

/**
 * Application-wide constants.
 */
public final class Constants {

    private Constants() {
        // Utility class - prevent instantiation
    }

    // Application Information
    public static final String APP_NAME = "Environment Configuration Manager";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_DESCRIPTION = "A comprehensive environment management system";
    public static final String APP_AUTHOR = "DevOps Team";
    public static final String APP_COPYRIGHT = "© 2024 Company Inc.";

    // File Paths and Directories
    public static final String CONFIG_DIR = "config";
    public static final String LOG_DIR = "logs";
    public static final String DATA_DIR = "data";
    public static final String BACKUP_DIR = "backups";
    public static final String TEMP_DIR = "temp";

    public static final String CONFIG_FILE = "environment-config.json";
    public static final String LOG_FILE = "environment-manager.log";
    public static final String BACKUP_FILE_PREFIX = "env_backup_";

    // Environment Configuration Constants
    public static final int MAX_ENVIRONMENT_NAME_LENGTH = 100;
    public static final int MIN_ENVIRONMENT_NAME_LENGTH = 3;
    public static final int MAX_ENVIRONMENT_ID_LENGTH = 50;
    public static final int MIN_ENVIRONMENT_ID_LENGTH = 3;

    public static final int MAX_SERVICE_NAME_LENGTH = 200;
    public static final int MIN_SERVICE_NAME_LENGTH = 2;
    public static final int MAX_SERVICE_ID_LENGTH = 100;
    public static final int MIN_SERVICE_ID_LENGTH = 3;

    public static final int MAX_URL_LENGTH = 500;
    public static final int MAX_VERSION_LENGTH = 50;

    // Validation Patterns
    public static final String ENVIRONMENT_ID_PATTERN = "^[A-Z0-9][A-Z0-9-_]{2,49}$";
    public static final String ENVIRONMENT_NAME_PATTERN = "^[A-Za-z0-9][A-Za-z0-9\\s-_]{2,99}$";
    public static final String SERVICE_ID_PATTERN = "^[a-z0-9][a-z0-9-]{2,99}$";
    public static final String SERVICE_NAME_PATTERN = "^[A-Za-z0-9][A-Za-z0-9\\s-_]{1,199}$";
    public static final String VERSION_PATTERN = "^\\d+\\.\\d+\\.\\d+(-[A-Za-z0-9]+(\\.\\d+)?)?$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String URL_PATTERN = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";

    // Default Values
    public static final String DEFAULT_DEVELOPMENT_BASE_URL = "http://localhost:8080";
    public static final String DEFAULT_QA_BASE_URL = "https://qa.company.com";
    public static final String DEFAULT_STAGING_BASE_URL = "https://staging.company.com";
    public static final String DEFAULT_UAT_BASE_URL = "https://uat.company.com";
    public static final String DEFAULT_PRODUCTION_BASE_URL = "https://app.company.com";

    public static final String DEFAULT_DATABASE_URL = "jdbc:mysql://localhost:3306/app";
    public static final String DEFAULT_API_ENDPOINT = "/api/v1";

    public static final int DEFAULT_HEALTH_CHECK_TIMEOUT = 30; // seconds
    public static final int DEFAULT_DEPLOYMENT_TIMEOUT = 300; // seconds
    public static final int DEFAULT_VALIDATION_TIMEOUT = 60; // seconds

    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final int RETRY_DELAY_MS = 1000;

    // Colors for Console Output (ANSI codes)
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_UNDERLINE = "\u001B[4m";

    // Icons and Symbols
    public static final String ICON_SUCCESS = "✅";
    public static final String ICON_ERROR = "❌";
    public static final String ICON_WARNING = "⚠️";
    public static final String ICON_INFO = "ℹ️";
    public static final String ICON_DEBUG = "🐛";
    public static final String ICON_DEPLOY = "🚀";
    public static final String ICON_ROLLBACK = "↩️";
    public static final String ICON_ENVIRONMENT = "🏗️";
    public static final String ICON_SERVICE = "⚙️";
    public static final String ICON_CONFIG = "🔧";
    public static final String ICON_VALIDATE = "✅";
    public static final String ICON_HEALTH = "❤️";

    // Date and Time Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    // Database Constants
    public static final int DEFAULT_DB_PORT = 3306;
    public static final int DEFAULT_REDIS_PORT = 6379;
    public static final int DEFAULT_MONGODB_PORT = 27017;
    public static final int DEFAULT_ELASTICSEARCH_PORT = 9200;

    // Security Constants
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 128;
    public static final int API_KEY_LENGTH = 32;
    public static final int SESSION_TIMEOUT_MINUTES = 30;

    // HTTP Status Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_ERROR = 500;
    public static final int HTTP_SERVICE_UNAVAILABLE = 503;

    // Environment Type Codes
    public static final String ENV_TYPE_DEV = "DEV";
    public static final String ENV_TYPE_QA = "QA";
    public static final String ENV_TYPE_UAT = "UAT";
    public static final String ENV_TYPE_STAGING = "STG";
    public static final String ENV_TYPE_PROD = "PROD";

    // Service Type Codes
    public static final String SERVICE_TYPE_WEB = "WEB";
    public static final String SERVICE_TYPE_API = "API";
    public static final String SERVICE_TYPE_DB = "DB";
    public static final String SERVICE_TYPE_CACHE = "CACHE";
    public static final String SERVICE_TYPE_MESSAGE = "MESSAGE";
    public static final String SERVICE_TYPE_AUTH = "AUTH";
    public static final String SERVICE_TYPE_PAYMENT = "PAYMENT";

    // Log Levels
    public static final String LOG_LEVEL_DEBUG = "DEBUG";
    public static final String LOG_LEVEL_INFO = "INFO";
    public static final String LOG_LEVEL_WARN = "WARN";
    public static final String LOG_LEVEL_ERROR = "ERROR";
    public static final String LOG_LEVEL_FATAL = "FATAL";

    // Exit Codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_ERROR = 1;
    public static final int EXIT_VALIDATION_ERROR = 2;
    public static final int EXIT_CONFIG_ERROR = 3;
    public static final int EXIT_DATABASE_ERROR = 4;
    public static final int EXIT_NETWORK_ERROR = 5;

    // Memory Constants
    public static final long MAX_MEMORY_USAGE_MB = 1024; // 1GB
    public static final long MAX_FILE_SIZE_MB = 10; // 10MB per config file

    // Backup Constants
    public static final int MAX_BACKUP_FILES = 30; // Keep last 30 backups
    public static final long BACKUP_INTERVAL_HOURS = 24; // Daily backups

    // Notification Constants
    public static final String NOTIFICATION_EMAIL_SUBJECT_PREFIX = "[Env Manager]";
    public static final int NOTIFICATION_RETRY_ATTEMPTS = 3;
    public static final int NOTIFICATION_RETRY_DELAY_MS = 5000;

    // Feature Flags
    public static final boolean FEATURE_HEALTH_CHECKS_ENABLED = true;
    public static final boolean FEATURE_AUTO_BACKUP_ENABLED = true;
    public static final boolean FEATURE_EMAIL_NOTIFICATIONS_ENABLED = false;
    public static final boolean FEATURE_SLACK_NOTIFICATIONS_ENABLED = true;
    public static final boolean FEATURE_METRICS_COLLECTION_ENABLED = true;

    // Performance Constants
    public static final int MAX_CONCURRENT_DEPLOYMENTS = 5;
    public static final int MAX_CONCURRENT_VALIDATIONS = 10;
    public static final int CACHE_TTL_SECONDS = 300; // 5 minutes
}