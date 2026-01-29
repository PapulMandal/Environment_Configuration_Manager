# Environment Configuration Manager

A comprehensive Java application for managing environment configurations, deployments, and services across different environment types (Development, QA, Production, etc.).

## Features

- **Builder Pattern**: Flexible environment and configuration builders
- **Multiple Environment Types**: DEV, QA, UAT, Staging, Production
- **Deployment Strategies**: Blue-Green, Canary, Rolling deployments
- **Observer Pattern**: Real-time notifications for deployments
- **Validation System**: Comprehensive environment validation
- **Console Interface**: User-friendly menu-driven application

## Project Structure

src/main/java/
├── builder/                    # Builder pattern classes
├── com/environment/manager/
│   ├── model/                 # Domain models (Environment, Service, ConfigItem)
│   ├── service/               # Business logic services
│   ├── repository/            # Data access layer
│   ├── strategy/              # Deployment strategies
│   ├── observer/              # Observer pattern implementation
│   ├── util/                  # Utility classes
│   └── EnvironmentManagerApp.java  # Main application
└── demo/                      # Demo and examples


## Requirements

- Java 17 or higher
- Maven 3.6+

## How to Run

### Using Maven:

# Compile
mvn clean compile

# Run the main application
mvn exec:java -Dexec.mainClass="com.environment.manager.EnvironmentManagerApp"

# Run the demo
mvn exec:java -Dexec.mainClass="demo.EnvironmentManagerDemo"


### Using compiled classes:

java -cp "target/classes" com.environment.manager.EnvironmentManagerApp


## Build Commands

# Compile and package
mvn clean package

# Run tests
mvn test

# Create executable JAR
mvn clean compile assembly:single


## Examples

### Creating an Environment:

Environment devEnv = EnvironmentBuilder.createDevelopment("My Dev");
Environment prodEnv = EnvironmentBuilder.createProduction("My Prod");


### Creating Configurations:

Map<String, ConfigItem> configs = ConfigurationBuilder.createDevelopmentConfig();


### Deploying Services:

Service webService = new Service("web-001", "Web App", "1.0.0", ServiceType.WEB_SERVICE);
deploymentService.deployToEnvironment("Dev-01", webService, "2.0.0", "admin");


## Design Patterns Used

1. **Builder Pattern** - EnvironmentBuilder, ConfigurationBuilder
2. **Strategy Pattern** - Deployment strategies
3. **Observer Pattern** - Deployment notifications
4. **Factory Pattern** - EnvironmentFactory
5. **Repository Pattern** - EnvironmentRepository
