# Environment Configuration Manager

A comprehensive Java application for managing and simulating deployment environments (QA, UAT, PROD) with full configuration tracking and deployment workflows.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Installation](#installation)
- [Usage](#usage)
- [Environment Types](#environment-types)
- [Deployment Workflow](#deployment-workflow)
- [Configuration Management](#configuration-management)
- [API Reference](#api-reference)
- [Examples](#examples)
- [Contributing](#contributing)
- [License](#license)

## Overview

The Environment Configuration Manager is a simulation tool that helps developers, DevOps engineers, and QA teams manage multiple deployment environments. It provides a centralized system to track configurations, deploy services, and maintain environment-specific settings across development lifecycles.

## Features

### Core Features
- **Multi-Environment Management**: Create and manage QA, UAT, PROD, DEV, and STAGING environments
- **Configuration Tracking**: Store and manage environment-specific key-value configurations
- **Service Deployment**: Track deployed services across environments
- **Version Control**: Maintain and update environment versions
- **Status Monitoring**: Real-time deployment status tracking

### Safety Features
- **Production Protection**: Requires explicit confirmation for production deployments
- **Validation Checks**: Automatic validation of environment configurations
- **Rollback Support**: Safe rollback capability for failed deployments
- **Active Environment Management**: Single active environment focus

### Reporting Features
- **Detailed Environment Info**: Complete environment configuration display
- **Summary Dashboard**: Overview of all environments
- **Validation Reports**: Identify configuration issues
- **Deployment History**: Track when deployments occurred

## Architecture


EnvironmentConfigurationManager (Main Application)
        ↓
EnvironmentManager (Controller)
        ↓
┌─────────────────────────────────────────┐
│           Environment Objects            │
├──────────┬──────────┬───────────────────┤
│   QA     │   UAT    │      PROD         │
├──────────┼──────────┼───────────────────┤
│ • Config │ • Config │ • Config          │
│ • Services│ • Services│ • Services      │
│ • Status │ • Status │ • Status          │
│ • URLs   │ • URLs   │ • URLs            │
└──────────┴──────────┴───────────────────┘


## Installation

### Prerequisites
- Java JDK 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code) or command line

### Setup
1. **Clone or download** the project files
2. **Open** in your preferred Java IDE
3. **Compile** the `EnvironmentConfigurationManager.java` file
4. **Run** the main class

### Quick Start

javac EnvironmentConfigurationManager.java
java EnvironmentConfigurationManager


## Usage

### Starting the Application

=== ENVIRONMENT CONFIGURATION MANAGER ===

MENU:
1. View All Environments
2. View Environment Details
3. Set Active Environment
4. Deploy to Environment
5. Deploy to All (Non-Production)
6. Deploy to Production
7. Rollback Environment
8. View Summary
9. Validate Environments
10. Exit
Choose option: 


### Basic Operations

#### 1. View All Environments
Displays complete information for all configured environments including:
- Environment type and name
- URLs and endpoints
- Deployed services
- Configuration settings
- Validation status

#### 2. View Specific Environment

Enter environment name: QA-01

Shows detailed information for a single environment.

#### 3. Set Active Environment

Enter environment name to activate: UAT-01
✓ Set active environment to: UAT-01

Marks one environment as active while deactivating others.

#### 4. Deploy to Single Environment

Enter environment name: QA-01
Enter version: 2.1.0
Starting deployment to QA-01...
✓ Deployment to QA-01 completed successfully!


#### 5. Deploy to All (Non-Production)

Enter version: 2.1.0
=== DEPLOYING TO ALL ENVIRONMENTS ===
✓ Deployment to non-production environments completed!

Deploys to QA and UAT, but skips PROD for safety.

#### 6. Deploy to Production

Enter production version: 2.1.0

WARNING: PRODUCTION DEPLOYMENT
Environment: PROD-01
Version: 2.1.0
Are you sure? (yes/no): yes
✓ Deployment to PROD-01 completed successfully!

Requires explicit confirmation for production deployments.

#### 7. Rollback Environment

Enter environment name to rollback: QA-01
Rolling back QA-01...
✓ QA-01 rolled back successfully!


#### 8. View Summary
Displays a dashboard with:
- Total environment count
- Distribution by type
- Active environment
- Status overview

#### 9. Validate Environments
Checks all environments for common configuration issues:
- Missing URLs
- Empty database connections
- Missing API endpoints
- Configuration completeness

#### 10. Exit
Gracefully exits the application.

## Environment Types

### Default Environments

| Type | Name | URL | Purpose |
|------|------|-----|---------|
| **QA** | QA-01 | https://qa.example.com | Quality Assurance testing |
| **UAT** | UAT-01 | https://uat.example.com | User Acceptance Testing |
| **PROD** | PROD-01 | https://app.example.com | Production deployment |

### Environment Characteristics

#### QA Environment
- **Database**: `jdbc:mysql://qa-db.example.com:3306/qa_db`
- **API Endpoint**: `https://qa-api.example.com/api`
- **Configuration**: Debug mode enabled, detailed logging
- **Services**: AuthService, PaymentService
- **Max Users**: 100

#### UAT Environment
- **Database**: `jdbc:mysql://uat-db.example.com:3306/uat_db`
- **API Endpoint**: `https://uat-api.example.com/api`
- **Configuration**: Info logging, no debug mode
- **Services**: AuthService, PaymentService, ReportingService
- **Max Users**: 1000

#### PROD Environment
- **Database**: `jdbc:mysql://prod-db.example.com:3306/prod_db`
- **API Endpoint**: `https://api.example.com/api`
- **Configuration**: Warning logging, backup enabled
- **Services**: All services including NotificationService
- **Max Users**: 10000

## Deployment Workflow

### Standard Deployment Pipeline

Development → QA Testing → UAT Testing → Production


### Step-by-Step Example

#### 1. Initial Setup

// Creates three pre-configured environments
EnvironmentManager manager = new EnvironmentManager();
manager.addEnvironment(createQAEnvironment());
manager.addEnvironment(createUATEnvironment());
manager.addEnvironment(createProductionEnvironment());


#### 2. QA Testing Phase

# Deploy new version to QA
Choose option: 4
Environment name: QA-01
Version: 2.1.0

# Validate QA configuration
Choose option: 9
✅ QA-01: PASSED


#### 3. UAT Testing Phase

# Deploy to UAT after QA passes
Choose option: 5
Version: 2.1.0

# Set UAT as active for focused testing
Choose option: 3
Environment name: UAT-01


#### 4. Production Deployment

# Deploy to production with confirmation
Choose option: 6
Version: 2.1.0
WARNING: PRODUCTION DEPLOYMENT
Are you sure? (yes/no): yes


#### 5. Post-Deployment Verification

# Check overall status
Choose option: 8
ENVIRONMENT MANAGER SUMMARY
Total Environments: 3
Active Environment: PROD-01
✅ QA-01 (QA) - v2.1.0
✅ UAT-01 (UAT) - v2.1.0
✅ PROD-01 (PROD) - v2.1.0


## Configuration Management

### Configuration Structure
Each environment maintains a key-value configuration store:

// Adding configurations
environment.addConfiguration("DEBUG_MODE", "true");
environment.addConfiguration("LOG_LEVEL", "DEBUG");
environment.addConfiguration("MAX_USERS", "100");

// Configuration types
- DEBUG_MODE: Enable/disable debug features
- LOG_LEVEL: DEBUG/INFO/WARN/ERROR
- MAX_USERS: Concurrent user limits
- BACKUP_ENABLED: Backup system status
- CACHE_SIZE: Cache configuration
- TIMEOUT_SECONDS: Request timeouts


### Service Management

// Deploying services
environment.deployService("AuthService");
environment.deployService("PaymentService");
environment.deployService("ReportingService");

// Service types supported
- AuthService: User authentication
- PaymentService: Payment processing
- ReportingService: Analytics and reports
- NotificationService: Email/SMS notifications
- InventoryService: Product management


## API Reference

### Environment Class

#### Constructor

Environment env = new Environment("QA-01", EnvironmentType.QA, "https://qa.example.com");


#### Key Methods

// Deployment operations
env.deploy("2.1.0");      // Deploy new version
env.rollback();           // Rollback deployment

// Configuration management
env.addConfiguration(key, value);
env.setDatabaseUrl(url);
env.setApiEndpoint(endpoint);

// Service management
env.deployService(serviceName);

// Information display
env.displayInfo();
env.validate();


#### Getters

env.getName();          // Returns environment name
env.getType();          // Returns EnvironmentType
env.getVersion();       // Returns current version
env.isActive();         // Returns active status


### EnvironmentManager Class

#### Key Methods

// Environment management
manager.addEnvironment(env);
manager.getEnvironment(name);
manager.getAllEnvironments();

// Deployment operations
manager.deployToAll(version);
manager.deployToProduction(version);

// Environment control
manager.setActiveEnvironment(name);

// Reporting
manager.displaySummary();


## Examples

### Example 1: Complete Deployment Cycle

// Create manager with default environments
EnvironmentManager manager = createDefaultManager();

// Test in QA
Environment qa = manager.getEnvironment("QA-01");
qa.deploy("2.1.0");

// Validate
List<String> issues = qa.validate();
if (issues.isEmpty()) {
    // Deploy to UAT
    manager.deployToAll("2.1.0");
    
    // Deploy to production
    manager.deployToProduction("2.1.0");
}


### Example 2: Custom Environment Creation

// Create custom staging environment
Environment staging = new Environment("STAGING-01", EnvironmentType.STAGING, "https://staging.example.com");
staging.setDatabaseUrl("jdbc:mysql://staging-db.example.com:3306/staging_db");
staging.setApiEndpoint("https://staging-api.example.com/api");
staging.addConfiguration("DEBUG_MODE", "true");
staging.addConfiguration("LOG_LEVEL", "DEBUG");
staging.deployService("AuthService");
staging.deployService("PaymentService");

// Add to manager
EnvironmentManager manager = new EnvironmentManager();
manager.addEnvironment(staging);


### Example 3: Environment Validation

// Check all environments for issues
for (Environment env : manager.getAllEnvironments()) {
    List<String> issues = env.validate();
    if (!issues.isEmpty()) {
        System.out.println("Issues in " + env.getName() + ":");
        for (String issue : issues) {
            System.out.println("  • " + issue);
        }
    }
}


## Error Handling

### Common Validation Issues
1. **Missing URLs**: Environment URL not configured
2. **Empty Database**: Database connection string missing
3. **No API Endpoint**: API endpoint not specified
4. **Empty Configurations**: No configuration settings defined

### Deployment Statuses
- **PENDING**: Initial state, no deployments
- **IN_PROGRESS**: Deployment currently running
- **SUCCESSFUL**: Deployment completed successfully
- **FAILED**: Deployment encountered errors
- **ROLLED_BACK**: Environment rolled back to previous state

## Extending the Application

### Adding New Environment Types

// In EnvironmentType enum
TEST("Testing"),
PERF("Performance Testing"),
DR("Disaster Recovery");

// Create new environment
Environment perfEnv = new Environment("PERF-01", EnvironmentType.PERF, "https://perf.example.com");


### Custom Configuration Validators

// Extend the Environment class
class CustomEnvironment extends Environment {
    @Override
    public List<String> validate() {
        List<String> issues = super.validate();
        
        // Add custom validations
        if (getConfiguration("CUSTOM_SETTING") == null) {
            issues.add("Custom setting is required");
        }
        
        return issues;
    }
}


## Best Practices

### Environment Management
1. **Always validate** environments before deployment
2. **Use staging environments** for pre-production testing
3. **Maintain separate configurations** for each environment type
4. **Regularly review** environment configurations

### Deployment Safety
1. **Never skip production confirmation**
2. **Deploy to QA and UAT first**
3. **Monitor deployment status**
4. **Have rollback plans ready**

### Configuration Management
1. **Use version control** for configurations
2. **Document all configuration changes**
3. **Regularly audit** environment settings
4. **Implement access controls** for production changes

## Contributing

### Development Process
1. **Fork** the repository
2. **Create a feature branch**
3. **Make changes** with descriptive commits
4. **Test thoroughly**
5. **Submit a pull request**

### Coding Standards
- Follow Java naming conventions
- Add Javadoc comments for public methods
- Write unit tests for new features
- Maintain backward compatibility

### Testing Guidelines
- Test all deployment scenarios
- Verify validation logic
- Test edge cases
- Simulate production deployments

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For questions or issues:
1. Check the examples section
2. Review the API reference
3. Test with the provided examples
4. Submit issues on GitHub
