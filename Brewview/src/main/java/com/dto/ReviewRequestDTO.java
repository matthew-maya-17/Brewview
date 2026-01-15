package com.dto;

import com.validation.ValidHalfIncrement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class ReviewRequestDTO {

    @Schema(description = "User associated with review")
    @NotNull(message = "User ID is required")
    private UUID userId;

    @Schema(description = "Beverage associated with review")
    @NotNull(message = "Beverage ID is required")
    private UUID beverageId;

    @Schema(description = "Location associated with review")
    @NotNull(message = "Location ID is required")
    private UUID locationId;

    @Schema(description = "Review's rating from 0-5 using only 0.5 increments", example = "4.0")
    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    @ValidHalfIncrement
    private BigDecimal rating;

    @Schema(description = "Review Notes", example = "Beverage AB was super creamy with hints of cacao, making it super delicious. Overall the taste was great")
    private String reviewNotes;

    public ReviewRequestDTO() {
    }

    public ReviewRequestDTO(UUID userId, UUID beverageId, UUID locationId, BigDecimal rating, String reviewNotes) {
        this.userId = userId;
        this.beverageId = beverageId;
        this.locationId = locationId;
        this.rating = rating;
        this.reviewNotes = reviewNotes;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getBeverageId() {
        return beverageId;
    }

    public void setBeverageId(UUID beverageId) {
        this.beverageId = beverageId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getReviewNote() {
        return reviewNotes;
    }

    public void setReviewNote(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }
}
