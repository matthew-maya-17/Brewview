package com.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id")
    private UUID reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_review_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beverage_id", nullable = false, foreignKey = @ForeignKey(name = "fk_review_beverage"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private Beverage beverage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_review_location"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private Location location;

    @Column(name = "rating", precision = 2, scale = 1, nullable = false)
    @NotNull(message = "Rating cannot be null")
    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    private BigDecimal rating;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;


    public Review() {
    }

    public Review(User user, Beverage beverage, Location location, BigDecimal rating, String reviewNotes, LocalDateTime createdAt) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Beverage getBeverage() {
        return beverage;
    }

    public void setBeverage(Beverage beverage) {
        this.beverage = beverage;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public void setReviewNote(String reviewNote) {
        this.reviewNotes = reviewNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}