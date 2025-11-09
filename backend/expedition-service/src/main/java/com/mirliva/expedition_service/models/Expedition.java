package com.mirliva.expedition_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a travel expedition (e.g., bus or train trip)
 * between two cities, managed by a company.
 */
@Entity
@Table(name = "expeditions")
public class Expedition implements Serializable {

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
    @NotNull
    @Column(name = "from_city_id", nullable = false)
    private Integer fromCityId;

    @NotNull
    @Column(name = "to_city_id", nullable = false)
    private Integer toCityId;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @NotNull
    @Column(nullable = false)
    private LocalTime time;

    @NotNull
    @Min(0)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private int duration; // Duration in minutes

    @NotNull
    @Column(name = "comp_id", nullable = false)
    private Integer compId;

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
    public Expedition() {
    }

    public Expedition(Integer fromCityId, Integer toCityId, LocalDate date, LocalTime time, BigDecimal price, int duration, Integer compId) {
        this.fromCityId = fromCityId;
        this.toCityId = toCityId;
        this.date = date;
        this.time = time;
        this.price = price;
        this.duration = duration;
        this.compId = compId;
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

    public Integer getFromCityId() {
        return fromCityId;
    }
    public void setFromCityId(Integer fromCityId) {
        this.fromCityId = fromCityId;
    }

    public Integer getToCityId() {
        return toCityId;
    }
    public void setToCityId(Integer toCityId) {
        this.toCityId = toCityId;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Integer getCompId() {
        return compId;
    }
    public void setCompId(Integer compId) {
        this.compId = compId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // ------------------------
    // Utility Methods
    // ------------------------
    public boolean isSameRoute() {
        return this.fromCityId.equals(this.toCityId);
    }

    // ------------------------
    // Equality & HashCode
    // ------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expedition)) return false;
        Expedition e = (Expedition) o;
        return id == e.id;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    // ------------------------
    // String Representation
    // ------------------------
    @Override
    public String toString() {
        return "Expedition{" +
                "id=" + id +
                ", fromCityId=" + fromCityId +
                ", toCityId=" + toCityId +
                ", date=" + date +
                ", time=" + time +
                ", price=" + price +
                ", duration=" + duration +
                ", compId=" + compId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
