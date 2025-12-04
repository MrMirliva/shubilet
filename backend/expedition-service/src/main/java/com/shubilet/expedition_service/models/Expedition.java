package com.shubilet.expedition_service.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

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
    @Column(name = "departure_city_id", nullable = false, updatable = false)
    private Integer departureCityId;

    @NotNull
    @Column(name = "arrival_city_id", nullable = false, updatable = false)
    private Integer arrivalCityId;

    ///TODO: Tek bir değişkende toplamak mantıklı mı bilmiyorum.
    @NotNull
    @Column(nullable = false, updatable = false)
    private Instant dateAndTime;

    @NotNull
    @Min(0)
    @Column(nullable = false, updatable = true, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = true, updatable = true)
    private int duration; // Duration in minutes

    @NotNull
    @Column(name = "company_id", nullable = false, updatable = false)
    private Integer companyId;

    // ------------------------
    // Constructors
    // ------------------------
    public Expedition() {
    }

    public Expedition(Integer departureCityId, 
                    Integer arrivalCityId, 
                    Instant dateAndTime,
                    BigDecimal price,
                    int duration,
                    Integer companyId
    ) {
        this.departureCityId = departureCityId;
        this.arrivalCityId = arrivalCityId;
        this.dateAndTime = dateAndTime;
        this.price = price;
        this.duration = duration;
        this.companyId = companyId;
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

    public Integer getDepartureCityId() {
        return departureCityId;
    }
    public void setDepartureCityId(Integer departureCityId) {
        this.departureCityId = departureCityId;
    }

    public Integer getArrivalCityId() {
        return arrivalCityId;
    }
    public void setArrivalCityId(Integer arrivalCityId) {
        this.arrivalCityId = arrivalCityId;
    }

    public Instant getDateAndTime() {
        return dateAndTime;
    }
    public void setDateAndTime(Instant dateAndTime) {
        this.dateAndTime = dateAndTime;
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

    public Integer getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    // ------------------------
    // Utility Methods
    // ------------------------
    public boolean isSameRoute() {
        return this.departureCityId.equals(this.arrivalCityId);
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
                ", departureCityId=" + departureCityId +
                ", arrivalCityId=" + arrivalCityId +
                ", dateAndTime=" + dateAndTime +
                ", price=" + price +
                ", duration=" + duration +
                ", companyId=" + companyId +
                '}';
    }
}
