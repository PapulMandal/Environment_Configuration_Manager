import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Enum for environment types
enum EnvironmentType {
    QA("Quality Assurance"),
    UAT("User Acceptance Testing"),
    PROD("Production"),
    DEV("Development"),
    STAGING("Staging");

    private final String fullName;

    EnvironmentType(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}

// Enum for deployment status
enum DeploymentStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    SUCCESSFUL("Successful"),
    FAILED("Failed"),
    ROLLED_BACK("Rolled Back");

    private final String displayName;

    DeploymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

// Main Environment class
class Environment {
    private final String name;
    private final EnvironmentType type;
    private final String url;
    private final Map<String, String> configuration;
    private final List<String> deployedServices;

    private String databaseUrl;
    private String apiEndpoint;
    private String version;
    private boolean isActive;
    private LocalDateTime lastDeployment;
    private DeploymentStatus status;

    // Constructor
    public Environment(String name, EnvironmentType type, String url) {
        this.name = name;
        this.type = type;
        this.url = url;
        this.configuration = new HashMap<>();
        this.deployedServices = new ArrayList<>();
        this.status = DeploymentStatus.PENDING;
        this.isActive = false;
        this.version = "1.0.0";
    }

    // Getters (only keep the ones that are actually used)
    public String getName() { return name; }
    public EnvironmentType getType() { return type; }
    public String getVersion() { return version; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setDatabaseUrl(String databaseUrl) { this.databaseUrl = databaseUrl; }
    public void setApiEndpoint(String apiEndpoint) { this.apiEndpoint = apiEndpoint; }
    public void setActive(boolean active) { isActive = active; }

    // Configuration management
    public void addConfiguration(String key, String value) {
        configuration.put(key, value);
    }

    // Service management
    public void deployService(String serviceName) {
        if (!deployedServices.contains(serviceName)) {
            deployedServices.add(serviceName);
        }
    }

    // Deployment operations
    public void deploy(String newVersion) {
        System.out.println("Starting deployment to " + name + "...");
        this.status = DeploymentStatus.IN_PROGRESS;
        this.version = newVersion;

        // Simulate deployment process
        try {
            Thread.sleep(1000); // Simulate deployment time
            this.lastDeployment = LocalDateTime.now();
            this.status = DeploymentStatus.SUCCESSFUL;
            this.isActive = true;
            System.out.println("Deployment to " + name + " completed successfully!");
        } catch (InterruptedException e) {
            this.status = DeploymentStatus.FAILED;
            System.out.println("Deployment to " + name + " failed!");
        }
    }

    public void rollback() {
        System.out.println("Rolling back " + name + "...");
        this.status = DeploymentStatus.ROLLED_BACK;
        this.isActive = false;
        System.out.println("✓ " + name + " rolled back successfully!");
    }

    // Validation
    public List<String> validate() {
        List<String> issues = new ArrayList<>();

        if (url.isEmpty()) {
            issues.add("URL is not set");
        }

        if (databaseUrl == null || databaseUrl.isEmpty()) {
            issues.add("Database URL is not set");
        }

        if (apiEndpoint == null || apiEndpoint.isEmpty()) {
            issues.add("API endpoint is not set");
        }

        if (configuration.isEmpty()) {
            issues.add("No configuration settings found");
        }

        return issues;
    }

    // Display environment info
    public void displayInfo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ENVIRONMENT: " + name);
        System.out.println("=".repeat(50));
        System.out.println("Type: " + type.getFullName() + " (" + type + ")");
        System.out.println("URL: " + url);
        System.out.println("Database: " + (databaseUrl != null ? databaseUrl : "Not configured"));
        System.out.println("API Endpoint: " + (apiEndpoint != null ? apiEndpoint : "Not configured"));
        System.out.println("Version: " + version);
        System.out.println("Status: " + status.getDisplayName());
        System.out.println("Active: " + (isActive ? "Yes" : "No"));

        if (lastDeployment != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("Last Deployment: " + lastDeployment.format(formatter));
        }

        System.out.println("\nServices Deployed (" + deployedServices.size() + "):");
        if (deployedServices.isEmpty()) {
            System.out.println("  No services deployed");
        } else {
            for (String service : deployedServices) {
                System.out.println("  • " + service);
            }
        }

        System.out.println("\nConfiguration Settings (" + configuration.size() + "):");
        if (configuration.isEmpty()) {
            System.out.println("  No configuration settings");
        } else {
            for (Map.Entry<String, String> entry : configuration.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
        }

        // Show validation status
        List<String> validationIssues = validate();
        if (!validationIssues.isEmpty()) {
            System.out.println("\nVALIDATION ISSUES:");
            for (String issue : validationIssues) {
                System.out.println("  • " + issue);
            }
        }
    }
}

// Manager class to handle multiple environments
class EnvironmentManager {
    private final List<Environment> environments;
    private Environment activeEnvironment;

    public EnvironmentManager() {
        this.environments = new ArrayList<>();
    }

    public void addEnvironment(Environment env) {
        environments.add(env);
        System.out.println("✓ Added environment: " + env.getName());
    }

    public Environment getEnvironment(String name) {
        return environments.stream()
                .filter(env -> env.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Environment> getAllEnvironments() {
        return new ArrayList<>(environments);
    }

    private Optional<Environment> findProductionEnvironment() {
        return environments.stream()
                .filter(env -> env.getType() == EnvironmentType.PROD)
                .findFirst();
    }

    public void setActiveEnvironment(String name) {
        Environment env = getEnvironment(name);
        if (env != null) {
            // Deactivate all environments first
            for (Environment e : environments) {
                e.setActive(false);
            }

            // Activate the selected one
            env.setActive(true);
            activeEnvironment = env;
            System.out.println("✓ Set active environment to: " + name);
        } else {
            System.out.println("✗ Environment not found: " + name);
        }
    }

    // Deployment across multiple environments
    public void deployToAll(String version) {
        System.out.println("\n=== DEPLOYING TO ALL ENVIRONMENTS ===");
        for (Environment env : environments) {
            if (env.getType() != EnvironmentType.PROD) {
                env.deploy(version);
            }
        }
        System.out.println("✓ Deployment to non-production environments completed!");
    }

    // Production deployment with confirmation
    public void deployToProduction(String version) {
        Optional<Environment> prodEnvOpt = findProductionEnvironment();

        if (prodEnvOpt.isEmpty()) {
            System.out.println("No production environment found!");
            return;
        }

        Environment prodEnv = prodEnvOpt.get();

        System.out.println("\nWARNING: PRODUCTION DEPLOYMENT");
        System.out.println("Environment: " + prodEnv.getName());
        System.out.println("Version: " + version);
        System.out.print("Are you sure? (yes/no): ");

        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("yes")) {
            prodEnv.deploy(version);
        } else {
            System.out.println("✗ Production deployment cancelled!");
        }
    }

    // Display summary
    public void displaySummary() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ENVIRONMENT MANAGER SUMMARY");
        System.out.println("=".repeat(60));
        System.out.println("Total Environments: " + environments.size());

        Map<EnvironmentType, Integer> countByType = new HashMap<>();
        for (Environment env : environments) {
            countByType.put(env.getType(), countByType.getOrDefault(env.getType(), 0) + 1);
        }

        System.out.println("\nEnvironment Count by Type:");
        for (EnvironmentType type : EnvironmentType.values()) {
            int count = countByType.getOrDefault(type, 0);
            System.out.println("  " + type + ": " + count);
        }

        if (activeEnvironment != null) {
            System.out.println("\nActive Environment: " + activeEnvironment.getName());
        }

        System.out.println("\nEnvironment Status:");
        for (Environment env : environments) {
            String statusIcon = env.isActive() ? "Yes" : "No";
            System.out.println("  " + statusIcon + " " + env.getName() +
                    " (" + env.getType() + ") - v" + env.getVersion());
        }
    }
}

// Main application with extracted method
public class EnvironmentConfigurationManager {

    // Extracted method to create and configure QA environment
    private static Environment createQAEnvironment() {
        Environment qaEnv = new Environment("QA-01", EnvironmentType.QA, "https://qa.example.com");
        qaEnv.setDatabaseUrl("jdbc:mysql://qa-db.example.com:3306/qa_db");
        qaEnv.setApiEndpoint("https://qa-api.example.com/api");
        qaEnv.addConfiguration("DEBUG_MODE", "true");
        qaEnv.addConfiguration("LOG_LEVEL", "DEBUG");
        qaEnv.addConfiguration("MAX_USERS", "100");
        qaEnv.deployService("AuthService");
        qaEnv.deployService("PaymentService");
        return qaEnv;
    }

    // Extracted method to create and configure UAT environment
    private static Environment createUATEnvironment() {
        Environment uatEnv = new Environment("UAT-01", EnvironmentType.UAT, "https://uat.example.com");
        uatEnv.setDatabaseUrl("jdbc:mysql://uat-db.example.com:3306/uat_db");
        uatEnv.setApiEndpoint("https://uat-api.example.com/api");
        uatEnv.addConfiguration("DEBUG_MODE", "false");
        uatEnv.addConfiguration("LOG_LEVEL", "INFO");
        uatEnv.addConfiguration("MAX_USERS", "1000");
        uatEnv.deployService("AuthService");
        uatEnv.deployService("PaymentService");
        uatEnv.deployService("ReportingService");
        return uatEnv;
    }

    // Extracted method to create and configure Production environment
    private static Environment createProductionEnvironment() {
        Environment prodEnv = new Environment("PROD-01", EnvironmentType.PROD, "https://app.example.com");
        prodEnv.setDatabaseUrl("jdbc:mysql://prod-db.example.com:3306/prod_db");
        prodEnv.setApiEndpoint("https://api.example.com/api");
        prodEnv.addConfiguration("DEBUG_MODE", "false");
        prodEnv.addConfiguration("LOG_LEVEL", "WARN");
        prodEnv.addConfiguration("MAX_USERS", "10000");
        prodEnv.addConfiguration("BACKUP_ENABLED", "true");
        prodEnv.deployService("AuthService");
        prodEnv.deployService("PaymentService");
        prodEnv.deployService("ReportingService");
        prodEnv.deployService("NotificationService");
        return prodEnv;
    }

    private static EnvironmentManager createDefaultManager() {
        EnvironmentManager manager = new EnvironmentManager();

        // Create and add environments using extracted methods
        manager.addEnvironment(createQAEnvironment());
        manager.addEnvironment(createUATEnvironment());
        manager.addEnvironment(createProductionEnvironment());

        return manager;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EnvironmentManager manager = createDefaultManager();

        System.out.println("=== ENVIRONMENT CONFIGURATION MANAGER ===");

        boolean running = true;

        while (running) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            running = handleUserChoice(choice, scanner, manager);
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\nMENU:");
        System.out.println("1. View All Environments");
        System.out.println("2. View Environment Details");
        System.out.println("3. Set Active Environment");
        System.out.println("4. Deploy to Environment");
        System.out.println("5. Deploy to All (Non-Production)");
        System.out.println("6. Deploy to Production");
        System.out.println("7. Rollback Environment");
        System.out.println("8. View Summary");
        System.out.println("9. Validate Environments");
        System.out.println("10. Exit");
        System.out.print("Choose option: ");
    }

    private static boolean handleUserChoice(int choice, Scanner scanner, EnvironmentManager manager) {
        switch (choice) {
            case 1:
                displayAllEnvironments(manager);
                break;

            case 2:
                displayEnvironmentDetails(scanner, manager);
                break;

            case 3:
                setActiveEnvironment(scanner, manager);
                break;

            case 4:
                deployToEnvironment(scanner, manager);
                break;

            case 5:
                deployToAllEnvironments(scanner, manager);
                break;

            case 6:
                performProductionDeployment(scanner, manager);
                break;

            case 7:
                rollbackEnvironment(scanner, manager);
                break;

            case 8:
                manager.displaySummary();
                break;

            case 9:
                validateEnvironments(manager);
                break;

            case 10:
                System.out.println("Goodbye!");
                return false;

            default:
                System.out.println("Invalid option!");
        }
        return true;
    }

    private static void displayAllEnvironments(EnvironmentManager manager) {
        System.out.println("\n=== ALL ENVIRONMENTS ===");
        for (Environment env : manager.getAllEnvironments()) {
            env.displayInfo();
        }
    }

    private static void displayEnvironmentDetails(Scanner scanner, EnvironmentManager manager) {
        System.out.print("Enter environment name: ");
        String envName = scanner.nextLine();
        Environment env = manager.getEnvironment(envName);
        if (env != null) {
            env.displayInfo();
        } else {
            System.out.println("✗ Environment not found: " + envName);
        }
    }

    private static void setActiveEnvironment(Scanner scanner, EnvironmentManager manager) {
        System.out.print("Enter environment name to activate: ");
        String activeName = scanner.nextLine();
        manager.setActiveEnvironment(activeName);
    }

    private static void deployToEnvironment(Scanner scanner, EnvironmentManager manager) {
        System.out.print("Enter environment name: ");
        String deployEnvName = scanner.nextLine();
        System.out.print("Enter version: ");
        String version = scanner.nextLine();

        Environment deployEnv = manager.getEnvironment(deployEnvName);
        if (deployEnv != null) {
            deployEnv.deploy(version);
        } else {
            System.out.println("✗ Environment not found: " + deployEnvName);
        }
    }

    private static void deployToAllEnvironments(Scanner scanner, EnvironmentManager manager) {
        System.out.print("Enter version: ");
        String allVersion = scanner.nextLine();
        manager.deployToAll(allVersion);
    }

    private static void performProductionDeployment(Scanner scanner, EnvironmentManager manager) {
        System.out.print("Enter production version: ");
        String prodVersion = scanner.nextLine();
        manager.deployToProduction(prodVersion);
    }

    private static void rollbackEnvironment(Scanner scanner, EnvironmentManager manager) {
        System.out.print("Enter environment name to rollback: ");
        String rollbackName = scanner.nextLine();
        Environment rollbackEnv = manager.getEnvironment(rollbackName);
        if (rollbackEnv != null) {
            rollbackEnv.rollback();
        } else {
            System.out.println("Environment not found: " + rollbackName);
        }
    }

    private static void validateEnvironments(EnvironmentManager manager) {
        System.out.println("\n=== ENVIRONMENT VALIDATION ===");
        for (Environment environment : manager.getAllEnvironments()) {
            List<String> issues = environment.validate();
            if (issues.isEmpty()) {
                System.out.println(environment.getName() + ": PASSED");
            } else {
                System.out.println(environment.getName() + ": " + issues.size() + " issues");
                for (String issue : issues) {
                    System.out.println("   • " + issue);
                }
            }
        }
    }
}