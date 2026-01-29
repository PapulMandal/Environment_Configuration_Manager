package com.environment.manager;

import com.environment.manager.manager.EnvironmentFactory;
import com.environment.manager.model.*;
import com.environment.manager.repository.InMemoryEnvironmentRepository;
import com.environment.manager.service.DeploymentService;
import com.environment.manager.strategy.BlueGreenDeploymentStrategy;
import com.environment.manager.strategy.CanaryDeploymentStrategy;
import com.environment.manager.observer.LoggingObserver;
import builder.ConfigurationBuilder;
import java.util.Map;

import java.util.List;
import java.util.Scanner;

import com.environment.manager.model.ServiceType;

public class EnvironmentManagerApp {
    private final InMemoryEnvironmentRepository repository;
    private final DeploymentService deploymentService;
    private final Scanner scanner;

    public EnvironmentManagerApp() {
        this.repository = new InMemoryEnvironmentRepository();
        this.deploymentService = new DeploymentService(repository);
        this.scanner = new Scanner(System.in);

        // Add observers
        deploymentService.addObserver(new LoggingObserver());

        // Initialize with sample data
        initializeSampleData();
    }

    private void initializeSampleData() {
        // Create sample environments using Factory
        Environment devEnv = EnvironmentFactory.createEnvironment(
                EnvironmentType.DEVELOPMENT, "Dev-01", "http://localhost:8080"
        );

        Environment qaEnv = EnvironmentFactory.createEnvironment(
                EnvironmentType.QUALITY_ASSURANCE, "QA-01", "https://qa.company.com"
        );

        Environment prodEnv = EnvironmentFactory.createEnvironment(
                EnvironmentType.PRODUCTION, "Prod-01", "https://app.company.com"
        );

        // Add sample services
        Service authService = new Service("auth-001", "Authentication Service", "1.2.0", ServiceType.WEB_SERVICE);
        Service paymentService = new Service("pay-001", "Payment Gateway", "2.0.1", ServiceType.WEB_SERVICE);

        devEnv.addService(authService);
        qaEnv.addService(authService);
        devEnv.addService(paymentService);

        // Create configurations using ConfigurationBuilder
        Map<String, ConfigItem> devConfig = ConfigurationBuilder.createDevelopmentConfig();
        devConfig.values().forEach(devEnv::addConfiguration);

        Map<String, ConfigItem> prodConfig = ConfigurationBuilder.createProductionConfig();
        prodConfig.values().forEach(prodEnv::addConfiguration);

        // Save environments
        repository.save(devEnv);
        repository.save(qaEnv);
        repository.save(prodEnv);

        System.out.println("✅ Sample environments initialized:");
        System.out.println("   - " + devEnv.getName() + " (" + devEnv.getType() + ")");
        System.out.println("   - " + qaEnv.getName() + " (" + qaEnv.getType() + ")");
        System.out.println("   - " + prodEnv.getName() + " (" + prodEnv.getType() + ")");
    }
    public void start() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("        ENVIRONMENT CONFIGURATION MANAGER");
        System.out.println("=".repeat(60));

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Choose option: ");

            switch (choice) {
                case 1:
                    listAllEnvironments();
                    break;
                case 2:
                    viewEnvironmentDetails();
                    break;
                case 3:
                    createNewEnvironment();
                    break;
                case 4:
                    deployToEnvironment();
                    break;
                case 5:
                    deployToAllTesting();
                    break;
                case 6:
                    deployToProduction();
                    break;
                case 7:
                    rollbackDeployment();
                    break;
                case 8:
                    validateEnvironments();
                    break;
                case 9:
                    setDeploymentStrategy();
                    break;
                case 10:
                    viewDeploymentHistory();
                    break;
                case 11:
                    displayStatistics();
                    break;
                case 0:
                    running = false;
                    System.out.println("👋 Goodbye!");
                    break;
                default:
                    System.out.println("❌ Invalid option!");
                    break;
            }
        }

        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(40));
        System.out.println("1. 📋 List All Environments");
        System.out.println("2. 🔍 View Environment Details");
        System.out.println("3. 🆕 Create New Environment");
        System.out.println("4. 🚀 Deploy to Environment");
        System.out.println("5. 📦 Deploy to All Testing");
        System.out.println("6. ⚡ Deploy to Production");
        System.out.println("7. ↩️ Rollback Deployment");
        System.out.println("8. ✅ Validate Environments");
        System.out.println("9. ⚙️ Set Deployment Strategy");
        System.out.println("10. 📊 View Deployment History");
        System.out.println("11. 📈 Display Statistics");
        System.out.println("0. 🚪 Exit");
    }

    private void listAllEnvironments() {
        System.out.println("\n📋 ALL ENVIRONMENTS");
        System.out.println("-".repeat(40));

        List<Environment> environments = repository.findAll();
        if (environments.isEmpty()) {
            System.out.println("No environments found.");
            return;
        }

        environments.forEach(env -> {
            String status = env.isActive() ? "✅ Active" : "❌ Inactive";
            System.out.printf("%s [%s] - %s - v%s - %s%n",
                    env.getName(), env.getType().getCode(),
                    env.getBaseUrl(), env.getCurrentVersion(), status);
        });
    }

    private void viewEnvironmentDetails() {
        String envName = getStringInput("Enter environment name: ");

        repository.findByName(envName).ifPresentOrElse(
                env -> System.out.println(env.getDetailedInfo()),
                () -> System.out.println("❌ Environment not found: " + envName)
        );
    }

    private void createNewEnvironment() {
        System.out.println("\n🆕 CREATE NEW ENVIRONMENT");
        System.out.println("-".repeat(40));

        System.out.println("Available environment types:");
        for (EnvironmentType type : EnvironmentType.values()) {
            System.out.printf("  %s - %s%n", type.getCode(), type.getDescription());
        }

        String typeCode = getStringInput("Enter environment type code: ");
        String name = getStringInput("Enter environment name: ");
        String baseUrl = getStringInput("Enter base URL: ");

        try {
            EnvironmentType type = EnvironmentType.fromCode(typeCode);
            Environment env = EnvironmentFactory.createEnvironment(type, name, baseUrl);
            repository.save(env);
            System.out.println("✅ Environment created successfully: " + env.getName());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void deployToEnvironment() {
        System.out.println("\n🚀 DEPLOY TO ENVIRONMENT");
        System.out.println("-".repeat(40));

        String envName = getStringInput("Enter environment name: ");
        String serviceName = getStringInput("Enter service name: ");
        String version = getStringInput("Enter version: ");
        String deployedBy = getStringInput("Deployed by: ");

        Service service = new Service(
                "svc-" + System.currentTimeMillis(),
                serviceName,
                version,
                com.environment.manager.model.ServiceType.WEB_SERVICE
        );

        boolean success = deploymentService.deployToEnvironment(envName, service, version, deployedBy);
        System.out.println(success ? "✅ Deployment successful!" : "❌ Deployment failed!");
    }

    private void deployToAllTesting() {
        System.out.println("\n📦 DEPLOY TO ALL TESTING ENVIRONMENTS");
        System.out.println("-".repeat(40));

        String serviceName = getStringInput("Enter service name: ");
        String version = getStringInput("Enter version: ");
        String deployedBy = getStringInput("Deployed by: ");

        Service service = new Service(
                "svc-batch-" + System.currentTimeMillis(),
                serviceName,
                version,
                ServiceType.WEB_SERVICE
        );

        boolean success = deploymentService.deployToAllTesting(service, version, deployedBy);
        System.out.println(success ? "✅ All deployments successful!" : "❌ Some deployments failed!");
    }

    private void deployToProduction() {
        System.out.println("\n⚡ DEPLOY TO PRODUCTION");
        System.out.println("-".repeat(40));
        System.out.println("⚠️  WARNING: Production deployment requires careful consideration!");

        String confirm = getStringInput("Are you sure you want to proceed? (yes/no): ");
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("🚫 Production deployment cancelled.");
            return;
        }

        String serviceName = getStringInput("Enter service name: ");
        String version = getStringInput("Enter version: ");
        String deployedBy = getStringInput("Deployed by: ");

        Service service = new Service(
                "svc-prod-" + System.currentTimeMillis(),
                serviceName,
                version,
                ServiceType.WEB_SERVICE
        );

        boolean success = deploymentService.deployToEnvironment("Prod-01", service, version, deployedBy);
        System.out.println(success ? "✅ Production deployment successful!" : "❌ Production deployment failed!");
    }

    private void rollbackDeployment() {
        System.out.println("\n↩️ ROLLBACK DEPLOYMENT");
        System.out.println("-".repeat(40));

        String envName = getStringInput("Enter environment name: ");
        String serviceId = getStringInput("Enter service ID: ");

        boolean success = deploymentService.rollback(envName, serviceId);
        System.out.println(success ? "✅ Rollback successful!" : "❌ Rollback failed!");
    }

    private void validateEnvironments() {
        System.out.println("\n✅ ENVIRONMENT VALIDATION");
        System.out.println("-".repeat(40));

        List<Environment> environments = repository.findAll();
        if (environments.isEmpty()) {
            System.out.println("No environments to validate.");
            return;
        }

        int totalIssues = 0;
        for (Environment env : environments) {
            List<String> issues = env.validate();
            if (issues.isEmpty()) {
                System.out.println("✅ " + env.getName() + ": PASSED");
            } else {
                System.out.println("❌ " + env.getName() + ": " + issues.size() + " issues");
                issues.forEach(issue -> System.out.println("   • " + issue));
                totalIssues += issues.size();
            }
        }

        System.out.println("\n📊 Validation Summary:");
        System.out.println("   Environments checked: " + environments.size());
        System.out.println("   Total issues found: " + totalIssues);
    }

    private void setDeploymentStrategy() {
        System.out.println("\n⚙️ SET DEPLOYMENT STRATEGY");
        System.out.println("-".repeat(40));

        System.out.println("Available strategies:");
        System.out.println("1. Blue-Green Deployment (Zero downtime)");
        System.out.println("2. Canary Deployment (Gradual rollout)");
        System.out.println("3. Default Deployment");

        int choice = getIntInput("Choose strategy (1-3): ");
        switch (choice) {
            case 1:
                deploymentService.setDeploymentStrategy(new BlueGreenDeploymentStrategy());
                System.out.println("✅ Blue-Green deployment strategy set");
                break;
            case 2:
                deploymentService.setDeploymentStrategy(new CanaryDeploymentStrategy());
                System.out.println("✅ Canary deployment strategy set");
                break;
            case 3:
                deploymentService.setDeploymentStrategy(null);
                System.out.println("✅ Default deployment strategy set");
                break;
            default:
                System.out.println("❌ Invalid choice");
                break;
        }
    }

    private void viewDeploymentHistory() {
        System.out.println("\n📊 DEPLOYMENT HISTORY");
        System.out.println("-".repeat(40));

        List<Environment> environments = repository.findAll();
        if (environments.isEmpty()) {
            System.out.println("No environments found.");
            return;
        }

        for (Environment env : environments) {
            System.out.println("\n" + env.getName() + " (" + env.getType() + "):");
            List<DeploymentHistory> history = env.getDeploymentHistory();
            if (history.isEmpty()) {
                System.out.println("   No deployments yet");
            } else {
                history.forEach(h -> System.out.println("   " + h));
            }
        }
    }

    private void displayStatistics() {
        System.out.println("\n📈 ENVIRONMENT STATISTICS");
        System.out.println("-".repeat(40));

        long totalEnvironments = repository.count();
        List<Environment> allEnvironments = repository.findAll();

        System.out.println("Total Environments: " + totalEnvironments);
        System.out.println("\nBy Type:");

        for (EnvironmentType type : EnvironmentType.values()) {
            long count = allEnvironments.stream().filter(env -> env.getType() == type).count();
            System.out.printf("  %s: %d%n", type.getDescription(), count);
        }

        System.out.println("\nBy Status:");
        long activeCount = allEnvironments.stream().filter(Environment::isActive).count();
        long inactiveCount = totalEnvironments - activeCount;
        System.out.printf("  ✅ Active: %d%n", activeCount);
        System.out.printf("  ❌ Inactive: %d%n", inactiveCount);

        System.out.println("\nTotal Services Deployed: " +
                allEnvironments.stream().mapToInt(env -> env.getServices().size()).sum());

        System.out.println("Total Deployments: " +
                allEnvironments.stream().mapToInt(env -> env.getDeploymentHistory().size()).sum());
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static void main(String[] args) {
        EnvironmentManagerApp app = new EnvironmentManagerApp();
        app.start();
    }
}

