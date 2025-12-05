package com.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "beverages")
public class Beverage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "beverage_id")
    private UUID id;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(message = "The Beverage name must be between 6 and 254 characters!", min = 6, max = 254)
    private String beverageName;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(message = "The Beverage Type must be between 3 and 254 characters!", min = 3, max = 254)
    private String type;

    @Column(name = "abv", nullable = false)
    @NotBlank
    private int abv;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(message = "The Beverage Description must be between 25 and 254 characters", min = 25, max = 254)
    private String description;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(message = "The Beverage url must be a valid link and between 6 and 500 characters", min = 6, max = 500)
    private String img_url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Beverage() {
    }

    public Beverage(UUID id, String beverageName, String type, int abv, String description, String img_url, LocalDateTime createdAt) {
        this.id = id;
        this.beverageName = beverageName;
        this.type = type;
        this.abv = abv;
        this.description = description;
        this.img_url = img_url;
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

    public int getAbv() {
        return abv;
    }

    public void setAbv(int abv) {
        this.abv = abv;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
