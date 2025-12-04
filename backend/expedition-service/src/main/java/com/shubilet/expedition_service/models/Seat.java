package com.shubilet.expedition_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.concurrent.TransferQueue;

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
    @Column(name = "expedition_id", nullable = false, updatable = false)
    private Integer expeditionId;

    @NotNull
    @Min(1)
    @Column(name = "seat_no", nullable = false, updatable = false)
    private Integer seatNo;

    @Column(name = "customer_id", nullable = true, updatable = true)
    private Integer customerId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, updatable = true)
    private Status status;

    // ------------------------
    // Constructors
    // ------------------------
    public Seat() {
    }

    public Seat(Integer expeditionId, Integer seatNo) {
        this.expeditionId = expeditionId;
        this.seatNo = seatNo;
        this.status = Status.AVAILABLE;
    }

    public Seat(Integer expeditionId, Integer seatNo, Integer customerId, Status status) {
        this.expeditionId = expeditionId;
        this.seatNo = seatNo;
        this.customerId = customerId;
        this.status = status;
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

    public Integer getExpeditionId() {
        return expeditionId;
    }
    public void setExpeditionId(Integer expeditionId) {
        this.expeditionId = expeditionId;
    }

    public Integer getSeatNo() {
        return seatNo;
    }
    public void setSeatNo(Integer seatNo) {
        this.seatNo = seatNo;
    }

    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }


    // ------------------------
    // Lifecycle Callbacks
    // ------------------------
    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = Status.AVAILABLE;
        }
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
                ", expeditionId=" + expeditionId +
                ", seatNo=" + seatNo +
                ", customerId=" + customerId +
                ", status=" + status.getDisplayName() +
                '}';
    }

    public enum Status {
        AVAILABLE("Available"),
        RESERVED("Reserved");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

