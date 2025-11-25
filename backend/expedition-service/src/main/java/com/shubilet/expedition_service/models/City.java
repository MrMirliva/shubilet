package com.shubilet.expedition_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a city entity that can be used in expedition routes.
 * Example: İzmir, Ankara, İstanbul, etc.
 */
@Entity
@Table(name = "cities")
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    // ------------------------
    // Primary Key
    // ------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // ------------------------
    // Fields
    // ------------------------
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // ------------------------
    // Audit Fields
    // ------------------------
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    // ------------------------
    // Constructors
    // ------------------------
    public City() {
    }

    public City(String name) {
        this.name = name;
    }

    // ------------------------
    // Lifecycle Callbacks
    // ------------------------
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ------------------------
    // Getters and Setters
    // ------------------------
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // ------------------------
    // Equality & HashCode
    // ------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return id == city.id && name.equals(city.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name);
    }

    // ------------------------
    // String Representation
    // ------------------------
    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
