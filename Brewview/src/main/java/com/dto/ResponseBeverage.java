package com.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResponseBeverage {

    private UUID id;

    @NotBlank(message = "Beverage name cannot be blank")
    @Size(min = 6, max = 254, message = "The Beverage name must be between 6 and 254 characters!")
    private String beverageName;

    @NotBlank(message = "Beverage type cannot be blank")
    @Size(min = 3, max = 254, message = "The Beverage Type must be between 3 and 254 characters!")
    private String type;

    @NotNull(message = "ABV cannot be null")
    @Min(value = 0, message = "ABV must be 0 or greater")
    private int abv;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 25, max = 254, message = "The Beverage Description must be between 25 and 254 characters")
    private String description;

    @NotBlank(message = "Image URL cannot be blank")
    @Size(min = 6, max = 500, message = "The Beverage url must be a valid link and between 6 and 500 characters")
    private String imageUrl;

    private LocalDateTime createdAt;

    public ResponseBeverage() {
    }

    public ResponseBeverage(UUID id, String beverageName, String type, int abv, String description, String imageUrl, LocalDateTime createdAt) {
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
