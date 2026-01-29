package com.environment.manager.model;

import java.util.Objects;

public class Service {
    private final String id;
    private final String name;
    private final String version;
    private final ServiceType type;

    // Fixed constructor - removed extra parameter
    public Service(String id, String name, String version, ServiceType type) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.type = type;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public ServiceType getType() { return type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return id.equals(service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s v%s [%s]", name, version, type);
    }
}