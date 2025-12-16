package com.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "beverages")
public class Beverage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "beverage_id")
    private UUID id;

    @Column(unique = true, nullable = false, name = "beverage_name")
    @NotBlank
    @Size(message = "The Beverage name must be between 6 and 254 characters!", min = 6, max = 254)
    private String beverageName;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(message = "The Beverage Type must be between 3 and 254 characters!", min = 3, max = 254)
    private String type;

    @Column(name = "abv", nullable = false)
    @NotNull(message = "ABV cannot be null")
    @Min(value = 0, message = "ABV must be 0 or greater")
    private BigDecimal abv;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(message = "The Beverage Description must be between 25 and 254 characters", min = 25, max = 254)
    private String description;

    @Column(unique = true, nullable = false, name = "image_url")
    @NotBlank
    @Size(message = "The Beverage url must be a valid link and between 6 and 500 characters", min = 6, max = 500)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Beverage() {
    }

    public Beverage(UUID id, String beverageName, String type, BigDecimal abv, String description, String imageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.beverageName = beverageName;
        this.type = type;
        this.abv = abv;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBeverageName() {
        return beverageName;
    }

    public void setBeverageName(String beverageName) {
        this.beverageName = beverageName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAbv() {
        return abv;
    }

    public void setAbv(BigDecimal abv) {
        this.abv = abv;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
