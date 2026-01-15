package com.repository;

import com.model.Beverage;
import com.model.Location;
import com.model.Review;
import com.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findReviewsByUserId(UUID userId);
    List<Review> findReviewsByBeverageId(UUID beverageId);
    List<Review> findReviewsByLocationId(UUID locationId);
    List<Review> findReviewsByRatingBetween(BigDecimal minRating, BigDecimal maxRating);
    Optional<Review> findReviewByUserAndBeverageAndLocation(User user, Beverage beverage, Location location);

}
