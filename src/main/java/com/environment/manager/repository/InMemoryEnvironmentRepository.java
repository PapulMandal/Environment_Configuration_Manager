package com.environment.manager.repository;

import com.environment.manager.model.Environment;
import com.environment.manager.model.EnvironmentType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEnvironmentRepository implements EnvironmentRepository {
    private final Map<String, Environment> environments = new ConcurrentHashMap<>();

    @Override
    public void save(Environment environment) {
        environments.put(environment.getId(), environment);
    }

    @Override
    public Optional<Environment> findById(String id) {
        return Optional.ofNullable(environments.get(id));
    }

    @Override
    public Optional<Environment> findByName(String name) {
        return environments.values().stream()
                .filter(env -> env.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<Environment> findAll() {
        return new ArrayList<>(environments.values());
    }

    @Override
    public List<Environment> findByType(EnvironmentType type) {
        return environments.values().stream()
                .filter(env -> env.getType() == type)
                .toList();
    }

    @Override
    public void delete(String id) {
        environments.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return environments.containsKey(id);
    }

    @Override
    public long count() {
        return environments.size();
    }
}