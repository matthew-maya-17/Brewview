package com.dto;

import com.model.Location;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ResponseReview {

    @Schema(description = "Review's unique ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID reviewId;

    @Schema(description = "User associated with review")
    private ResponseUser user;

    @Schema(description = "Beverage associated with review")
    private ResponseBeverage beverage;

    @Schema(description = "Location associated with review")
    private ResponseLocation location;

    @Schema(description = "Review's rating from 0-5 using only 0.5 increments", example = "4.0")
    private BigDecimal rating;

    @Schema(description = "Review Notes", example = "Beverage AB was super creamy with hints of cacao, making it super delicious. Overall the taste was great")
    private String reviewNotes;

    @Schema(
            description = "Timestamp indicating when the user account was created",
            example = "2024-01-15T13:45:30"
    )
    private LocalDateTime createdAt;

    public ResponseReview() {
    }

    public ResponseReview(UUID reviewId, ResponseUser user, ResponseBeverage beverage, ResponseLocation location, BigDecimal rating, String reviewNotes, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.user = user;
        this.beverage = beverage;
        this.location = location;
        this.rating = rating;
        this.reviewNotes = reviewNotes;
        this.createdAt = createdAt;
    }

    public UUID getReviewId() {
        return reviewId;
    }

    public void setReviewId(UUID reviewId) {
        this.reviewId = reviewId;
    }

    public ResponseUser getUser() {
            return user;
        }

    public void setUser(ResponseUser user) {
        this.user = user;
    }

    public ResponseBeverage getBeverage() {
        return beverage;
    }

    public void setBeverage(ResponseBeverage beverage) {
        this.beverage = beverage;
    }

    public ResponseLocation getLocation() {
        return location;
    }

    public void setLocation(ResponseLocation location) {
        this.location = location;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
