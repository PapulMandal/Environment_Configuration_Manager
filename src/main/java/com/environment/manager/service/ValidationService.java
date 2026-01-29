package com.environment.manager.service;

import com.environment.manager.model.Environment;
import com.environment.manager.repository.EnvironmentRepository;
import java.util.ArrayList;
import java.util.List;

public class ValidationService {
    private final EnvironmentRepository repository;

    public ValidationService(EnvironmentRepository repository) {
        this.repository = repository;
    }

    public List<String> validateAllEnvironments() {
        List<String> allIssues = new ArrayList<>();
        List<Environment> environments = repository.findAll();

        for (Environment env : environments) {
            List<String> issues = env.validate();
            if (!issues.isEmpty()) {
                allIssues.add("Environment: " + env.getName());
                allIssues.addAll(issues);
            }
        }

        return allIssues;
    }

    public boolean isValidForDeployment(String environmentId) {
        return repository.findById(environmentId)
                .map(env -> env.validate().isEmpty())
                .orElse(false);
    }
}