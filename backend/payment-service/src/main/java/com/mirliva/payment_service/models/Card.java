package com.mirliva.payment_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a credit/debit card registered by a user for payment operations.
 */
@Entity
@Table(name = "cards")
public class Card implements Serializable {

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
    @Pattern(regexp = "\\d{16}", message = "Card number must be exactly 16 digits")
    @Column(name = "card_no", nullable = false, length = 16, unique = true)
    private String cardNo;

    @NotBlank
    @Pattern(regexp = "(0[1-9]|1[0-2])/([0-9]{2})", message = "SKT must be in MM/YY format")
    @Column(nullable = false, length = 5)
    private String skt; // Son Kullanma Tarihi (Expiration Date)

    @NotBlank
    @Pattern(regexp = "\\d{3}", message = "CVC must be exactly 3 digits")
    @Column(nullable = false, length = 3)
    private String cvc;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String surname;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    // ------------------------
    // Constructors
    // ------------------------
    public Card() {
    }

    public Card(String cardNo, String skt, String cvc, String name, String surname, Integer userId) {
        this.cardNo = cardNo;
        this.skt = skt;
        this.cvc = cvc;
        this.name = name;
        this.surname = surname;
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

    public String getCardNo() {
        return cardNo;
    }
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getSkt() {
        return skt;
    }
    public void setSkt(String skt) {
        this.skt = skt;
    }

    public String getCvc() {
        return cvc;
    }
    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
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
    public String getMaskedCardNo() {
        // Returns something like **** **** **** 1234
        return "**** **** **** " + cardNo.substring(cardNo.length() - 4);
    }

    // ------------------------
    // Equality & HashCode
    // ------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return id == card.id && cardNo.equals(card.cardNo);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, cardNo);
    }

    // ------------------------
    // String Representation
    // ------------------------
    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardNo='" + getMaskedCardNo() + '\'' +
                ", skt='" + skt + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
