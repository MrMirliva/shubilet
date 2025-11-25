package com.shubilet.security_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a admin session with a unique session code and expiration time.
 */
@Entity
@Table(name = "admin_sessions")
public class AdminSession implements Serializable {

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
    @Column(name = "admin_id", nullable = false)
    private Integer adminId;

    @NotBlank
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    // ------------------------
    // Constructors
    // ------------------------
    public AdminSession() {
    }

    public AdminSession(Integer adminId, String code, Instant expiresAt) {
        this.adminId = adminId;
        this.code = code;
        this.expiresAt = expiresAt;
    }

    // ------------------------
    // Lifecycle Callbacks
    // ------------------------
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        // expiresAt zaten constructor'dan gelir ama null ise 30 dk default
        if (this.expiresAt == null) {
            this.expiresAt = this.createdAt.plusSeconds(30 * 60);
        }
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

    public Integer getadminId() {
        return adminId;
    }
    public void setadminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    // ------------------------
    // Helper Methods
    // ------------------------
    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    // ------------------------
    // Equality & HashCode
    // ------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminSession)) return false;
        AdminSession that = (AdminSession) o;
        return id == that.id && code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, code);
    }

    // ------------------------
    // String Representation
    // ------------------------
    @Override
    public String toString() {
        return "adminSession{" +
                "id=" + id +
                ", adminId=" + adminId +
                ", code='" + code + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
