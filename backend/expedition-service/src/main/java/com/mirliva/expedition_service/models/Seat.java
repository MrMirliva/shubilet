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
import java.time.Instant;

/**
 * Represents a seat in a specific expedition.
 * Each seat belongs to one expedition and can be optionally assigned to a user.
 */
@Entity
@Table(name = "seats")
public class Seat implements Serializable {

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
    @Column(name = "exp_id", nullable = false)
    private Integer expId;

    @NotNull
    @Min(1)
    @Column(name = "seat_no", nullable = false)
    private Integer seatNo;

    @Column(name = "user_id")
    private Integer userId;

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
    public Seat() {
    }

    public Seat(Integer expId, Integer seatNo) {
        this.expId = expId;
        this.seatNo = seatNo;
    }

    public Seat(Integer expId, Integer seatNo, Integer userId) {
        this.expId = expId;
        this.seatNo = seatNo;
        this.userId = userId;
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

    public Integer getExpId() {
        return expId;
    }
    public void setExpId(Integer expId) {
        this.expId = expId;
    }

    public Integer getSeatNo() {
        return seatNo;
    }
    public void setSeatNo(Integer seatNo) {
        this.seatNo = seatNo;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
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
    public boolean isAvailable() {
        return this.userId == null;
    }

    // ------------------------
    // Equality & HashCode
    // ------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat)) return false;
        Seat seat = (Seat) o;
        return id == seat.id;
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
        return "Seat{" +
                "id=" + id +
                ", expId=" + expId +
                ", seatNo=" + seatNo +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
