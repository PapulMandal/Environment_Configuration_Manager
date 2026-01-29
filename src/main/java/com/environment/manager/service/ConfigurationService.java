package com.environment.manager.service;

import com.environment.manager.model.Environment;
import com.environment.manager.repository.EnvironmentRepository;
import java.util.List;

public class ConfigurationService {
    private final EnvironmentRepository repository;

    public ConfigurationService(EnvironmentRepository repository) {
        this.repository = repository;
    }

    public List<Environment> getEnvironmentsByType(String type) {
        return repository.findAll().stream()
                .filter(env -> env.getType().name().equalsIgnoreCase(type))
                .toList();
    }
}
