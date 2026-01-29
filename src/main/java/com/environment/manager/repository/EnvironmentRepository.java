package com.environment.manager.repository;

import com.environment.manager.model.Environment;
import com.environment.manager.model.EnvironmentType;
import java.util.List;
import java.util.Optional;

public interface EnvironmentRepository {
    void save(Environment environment);
    Optional<Environment> findById(String id);
    Optional<Environment> findByName(String name);
    List<Environment> findAll();
    List<Environment> findByType(EnvironmentType type);
    void delete(String id);
    boolean exists(String id);
    long count();
}