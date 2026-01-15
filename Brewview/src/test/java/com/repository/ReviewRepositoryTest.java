package com.repository;

import com.model.Beverage;
import com.model.Location;
import com.model.Review;
import com.model.Role;
import com.model.User;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BeverageRepository beverageRepository;

    @Autowired
    private LocationRepository locationRepository;

    private User validUser;
    private Beverage validBeverage;
    private Location validLocation;
    private Review validReview;

    @BeforeEach
    void setUp() {
        // Create and save a valid user
        validUser = new User(
                "reviewuser",
                "review@example.com",
                "hashedpassword123",
                Role.ROLE_USER,
                LocalDateTime.now()
        );
        validUser = userRepository.save(validUser);

        // Create and save a valid beverage
        validBeverage = new Beverage(
                null,
                "Test Beer Review",
                "IPA",
                new BigDecimal("6.0"),
                "A great test beer for reviewing purposes here",
                "https://example.com/test-beer.jpg",
                LocalDateTime.now()
        );
        validBeverage = beverageRepository.save(validBeverage);

        // Create and save a valid location
        validLocation = new Location(
                "Test Brewery",
                "123 Test Street",
                "Test City",
                "United States"
        );
        validLocation = locationRepository.save(validLocation);

        // Create a valid review
        validReview = new Review(
                validUser,
                validBeverage,
                validLocation,
                new BigDecimal("4.5"),
                "Great beer, would recommend!",
                LocalDateTime.now()
        );
    }

    // ========== CREATE OPERATIONS ==========

    // CREATE - Happy Paths
    @Test
    void saveShouldPersistReviewWithValidData() {
        // Arrange & Act
        Review savedReview = reviewRepository.save(validReview);

        // Assert
        assertNotNull(savedReview);
        assertNotNull(savedReview.getReviewId());
        assertEquals(validUser.getId(), savedReview.getUser().getId());
        assertEquals(validBeverage.getId(), savedReview.getBeverage().getId());
        assertEquals(validLocation.getId(), savedReview.getLocation().getId());
        assertEquals(new BigDecimal("4.5"), savedReview.getRating());
        assertEquals("Great beer, would recommend!", savedReview.getReviewNote());
        assertNotNull(savedReview.getCreatedAt());
    }

    @Test
    void saveShouldGenerateUniqueId() {
        // Arrange & Act
        Review savedReview = reviewRepository.save(validReview);

        // Assert
        assertNotNull(savedReview.getReviewId());
    }

    @Test
    void saveShouldPersistMultipleReviews() {
        // Arrange
        Review review1 = new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "Good beer", LocalDateTime.now());
        Review review2 = new Review(validUser, validBeverage, validLocation, new BigDecimal("5.0"), "Excellent beer", LocalDateTime.now());

        // Act
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // Assert
        assertEquals(2, reviewRepository.count());
    }

    @Test
    void saveShouldPersistReviewWithMinimumRating() {
        // Arrange
        Review review = new Review(validUser, validBeverage, validLocation, new BigDecimal("0.0"), "Not good", LocalDateTime.now());

        // Act
        Review savedReview = reviewRepository.save(review);

        // Assert
        assertNotNull(savedReview);
        assertEquals(new BigDecimal("0.0"), savedReview.getRating());
    }

    @Test
    void saveShouldPersistReviewWithMaximumRating() {
        // Arrange
        Review review = new Review(validUser, validBeverage, validLocation, new BigDecimal("5.0"), "Perfect!", LocalDateTime.now());

        // Act
        Review savedReview = reviewRepository.save(review);

        // Assert
        assertNotNull(savedReview);
        assertEquals(new BigDecimal("5.0"), savedReview.getRating());
    }

    @Test
    void saveShouldPersistReviewWithHalfRating() {
        // Arrange
        Review review = new Review(validUser, validBeverage, validLocation, new BigDecimal("3.5"), "Decent", LocalDateTime.now());

        // Act
        Review savedReview = reviewRepository.save(review);

        // Assert
        assertNotNull(savedReview);
        assertEquals(new BigDecimal("3.5"), savedReview.getRating());
    }

    @Test
    void saveShouldPersistReviewWithNullNotes() {
        // Arrange
        Review review = new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), null, LocalDateTime.now());

        // Act
        Review savedReview = reviewRepository.save(review);

        // Assert
        assertNotNull(savedReview);
        assertNull(savedReview.getReviewNote());
    }

    @Test
    void saveShouldPersistReviewWithEmptyNotes() {
        // Arrange
        Review review = new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "", LocalDateTime.now());

        // Act
        Review savedReview = reviewRepository.save(review);

        // Assert
        assertNotNull(savedReview);
        assertEquals("", savedReview.getReviewNote());
    }

    // CREATE - Unhappy Paths
    @Test
    void saveShouldThrowExceptionWhenUserIsNull() {
        // Arrange
        Review review = new Review(null, validBeverage, validLocation, new BigDecimal("4.0"), "Notes", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> reviewRepository.saveAndFlush(review));
    }

    @Test
    void saveShouldThrowExceptionWhenBeverageIsNull() {
        // Arrange
        Review review = new Review(validUser, null, validLocation, new BigDecimal("4.0"), "Notes", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> reviewRepository.saveAndFlush(review));
    }

    @Test
    void saveShouldThrowExceptionWhenLocationIsNull() {
        // Arrange
        Review review = new Review(validUser, validBeverage, null, new BigDecimal("4.0"), "Notes", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> reviewRepository.saveAndFlush(review));
    }

    @Test
    void saveShouldThrowExceptionWhenRatingIsNull() {
        // Arrange
        Review review = new Review(validUser, validBeverage, validLocation, null, "Notes", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> reviewRepository.saveAndFlush(review));
    }

    @Test
    void saveShouldThrowExceptionWhenRatingBelowMinimum() {
        // Arrange
        Review review = new Review(validUser, validBeverage, validLocation, new BigDecimal("-0.1"), "Notes", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> reviewRepository.saveAndFlush(review));
    }

    @Test
    void saveShouldThrowExceptionWhenRatingAboveMaximum() {
        // Arrange
        Review review = new Review(validUser, validBeverage, validLocation, new BigDecimal("5.1"), "Notes", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> reviewRepository.saveAndFlush(review));
    }

    // ========== READ OPERATIONS ==========

    // READ - Happy Paths
    @Test
    void findByIdShouldReturnReviewWhenExists() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        Optional<Review> foundReview = reviewRepository.findById(reviewId);

        // Assert
        assertTrue(foundReview.isPresent());
        assertEquals(reviewId, foundReview.get().getReviewId());
        assertEquals(new BigDecimal("4.5"), foundReview.get().getRating());
    }

    @Test
    void findAllShouldReturnAllReviews() {
        // Arrange
        Review review1 = new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "Good", LocalDateTime.now());
        Review review2 = new Review(validUser, validBeverage, validLocation, new BigDecimal("5.0"), "Excellent", LocalDateTime.now());
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // Act
        List<Review> reviews = reviewRepository.findAll();

        // Assert
        assertEquals(2, reviews.size());
    }

    @Test
    void existsByIdShouldReturnTrueWhenReviewExists() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        boolean exists = reviewRepository.existsById(reviewId);

        // Assert
        assertTrue(exists);
    }

    @Test
    void countShouldReturnCorrectNumberOfReviews() {
        // Arrange
        Review review1 = new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "Good", LocalDateTime.now());
        Review review2 = new Review(validUser, validBeverage, validLocation, new BigDecimal("5.0"), "Excellent", LocalDateTime.now());
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // Act
        long count = reviewRepository.count();

        // Assert
        assertEquals(2, count);
    }

    @Test
    void findReviewsByUserShouldReturnReviewsForUser() {
        // Arrange
        User user2 = userRepository.save(new User("TestUser2", "user2@example.com", "hash", Role.ROLE_USER, LocalDateTime.now()));
        Review review1 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "User1 review", LocalDateTime.now()));
        Review review2 = reviewRepository.save(new Review(user2, validBeverage, validLocation, new BigDecimal("5.0"), "User2 review", LocalDateTime.now()));
        Review review3 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("3.0"), "User1 review 2", LocalDateTime.now()));

        // Act
        List<Review> userReviews = reviewRepository.findReviewsByUserId(validUser.getId());

        // Assert
        assertEquals(2, userReviews.size());
        assertTrue(userReviews.stream().allMatch(r -> r.getUser().getId().equals(validUser.getId())));
    }

    @Test
    void findReviewsByBeverageShouldReturnReviewsForBeverage() {
        // Arrange
        Beverage beverage2 = beverageRepository.save(new Beverage(null, "Another Beer", "Stout", new BigDecimal("8.0"), "A different beer for testing reviews here", "https://example.com/another.jpg", LocalDateTime.now()));
        Review review1 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "Review 1", LocalDateTime.now()));
        Review review2 = reviewRepository.save(new Review(validUser, beverage2, validLocation, new BigDecimal("5.0"), "Review 2", LocalDateTime.now()));
        Review review3 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("3.0"), "Review 3", LocalDateTime.now()));

        // Act
        List<Review> beverageReviews = reviewRepository.findReviewsByBeverageId(validBeverage.getId());

        // Assert
        assertEquals(2, beverageReviews.size());
        assertTrue(beverageReviews.stream().allMatch(r -> r.getBeverage().getId().equals(validBeverage.getId())));
    }

    @Test
    void findReviewsByLocationShouldReturnReviewsForLocation() {
        // Arrange
        Location location2 = locationRepository.save(new Location("Another Place", "456 Other St", "Other City", "Canada"));
        Review review1 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "Review 1", LocalDateTime.now()));
        Review review2 = reviewRepository.save(new Review(validUser, validBeverage, location2, new BigDecimal("5.0"), "Review 2", LocalDateTime.now()));
        Review review3 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("3.0"), "Review 3", LocalDateTime.now()));

        // Act
        List<Review> locationReviews = reviewRepository.findReviewsByLocationId(validLocation.getId());

        // Assert
        assertEquals(2, locationReviews.size());
        assertTrue(locationReviews.stream().allMatch(r -> r.getLocation().getId().equals(validLocation.getId())));
    }

    @Test
    void findReviewsByRatingBetweenShouldReturnReviewsInRange() {
        // Arrange
        Review review1 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("2.0"), "Low rating", LocalDateTime.now()));
        Review review2 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("3.5"), "Medium rating", LocalDateTime.now()));
        Review review3 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("4.5"), "High rating", LocalDateTime.now()));
        Review review4 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("5.0"), "Highest rating", LocalDateTime.now()));

        // Act
        List<Review> reviews = reviewRepository.findReviewsByRatingBetween(new BigDecimal("3.0"), new BigDecimal("4.5"));

        // Assert
        assertEquals(2, reviews.size());
        assertTrue(reviews.stream().allMatch(r -> {
            BigDecimal rating = r.getRating();
            return rating.compareTo(new BigDecimal("3.0")) >= 0 && rating.compareTo(new BigDecimal("4.5")) <= 0;
        }));
    }

    @Test
    void findReviewByUserAndBeverageAndLocationShouldReturnReviewWhenExists() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);

        // Act
        Optional<Review> foundReview = reviewRepository.findReviewByUserAndBeverageAndLocation(
                validUser, validBeverage, validLocation);

        // Assert
        assertTrue(foundReview.isPresent());
        assertEquals(savedReview.getReviewId(), foundReview.get().getReviewId());
        assertEquals(validUser.getId(), foundReview.get().getUser().getId());
        assertEquals(validBeverage.getId(), foundReview.get().getBeverage().getId());
        assertEquals(validLocation.getId(), foundReview.get().getLocation().getId());
    }

    @Test
    void findReviewByUserAndBeverageAndLocationShouldReturnCorrectReviewWhenMultipleExist() {
        // Arrange
        User user2 = userRepository.save(new User("TestUser2", "user2@example.com", "hash", Role.ROLE_USER, LocalDateTime.now()));
        Beverage beverage2 = beverageRepository.save(new Beverage(null, "Another Beer", "Stout", new BigDecimal("8.0"), "A different beer for testing reviews here", "https://example.com/another.jpg", LocalDateTime.now()));
        Location location2 = locationRepository.save(new Location("Another Place", "456 Other St", "Other City", "Canada"));

        Review review1 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "Review 1", LocalDateTime.now()));
        Review review2 = reviewRepository.save(new Review(user2, validBeverage, validLocation, new BigDecimal("5.0"), "Review 2", LocalDateTime.now()));
        Review review3 = reviewRepository.save(new Review(validUser, beverage2, validLocation, new BigDecimal("3.0"), "Review 3", LocalDateTime.now()));
        Review review4 = reviewRepository.save(new Review(validUser, validBeverage, location2, new BigDecimal("4.5"), "Review 4", LocalDateTime.now()));

        // Act
        Optional<Review> foundReview = reviewRepository.findReviewByUserAndBeverageAndLocation(
                validUser, validBeverage, validLocation);

        // Assert
        assertTrue(foundReview.isPresent());
        assertEquals(review1.getReviewId(), foundReview.get().getReviewId());
        assertEquals(validUser.getId(), foundReview.get().getUser().getId());
        assertEquals(validBeverage.getId(), foundReview.get().getBeverage().getId());
        assertEquals(validLocation.getId(), foundReview.get().getLocation().getId());
    }

    // READ - Unhappy Paths
    @Test
    void findByIdShouldReturnEmptyWhenReviewDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<Review> foundReview = reviewRepository.findById(nonExistentId);

        // Assert
        assertFalse(foundReview.isPresent());
    }

    @Test
    void findByIdShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            reviewRepository.findById(null);
        });
    }

    @Test
    void existsByIdShouldReturnFalseWhenReviewDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        boolean exists = reviewRepository.existsById(nonExistentId);

        // Assert
        assertFalse(exists);
    }

    @Test
    void findAllShouldReturnEmptyListWhenNoReviewsExist() {
        // Act
        List<Review> reviews = reviewRepository.findAll();

        // Assert
        assertTrue(reviews.isEmpty());
    }

    @Test
    void countShouldReturnZeroWhenNoReviewsExist() {
        // Act
        long count = reviewRepository.count();

        // Assert
        assertEquals(0, count);
    }

    @Test
    void findReviewsByUserShouldReturnEmptyListWhenNoReviewsExist() {
        // Act
        List<Review> reviews = reviewRepository.findReviewsByUserId(validUser.getId());

        // Assert
        assertTrue(reviews.isEmpty());
    }

    @Test
    void findReviewsByBeverageShouldReturnEmptyListWhenNoReviewsExist() {
        // Act
        List<Review> reviews = reviewRepository.findReviewsByBeverageId(validBeverage.getId());

        // Assert
        assertTrue(reviews.isEmpty());
    }

    @Test
    void findReviewsByLocationShouldReturnEmptyListWhenNoReviewsExist() {
        // Act
        List<Review> reviews = reviewRepository.findReviewsByLocationId(validLocation.getId());

        // Assert
        assertTrue(reviews.isEmpty());
    }

    @Test
    void findReviewsByRatingBetweenShouldReturnEmptyListWhenNoReviewsInRange() {
        // Arrange
        reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("1.0"), "Low", LocalDateTime.now()));
        reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("2.0"), "Low", LocalDateTime.now()));

        // Act
        List<Review> reviews = reviewRepository.findReviewsByRatingBetween(new BigDecimal("4.0"), new BigDecimal("5.0"));

        // Assert
        assertTrue(reviews.isEmpty());
    }

    @Test
    void findReviewByUserAndBeverageAndLocationShouldReturnEmptyWhenNoReviewExists() {
        // Arrange - No reviews saved

        // Act
        Optional<Review> foundReview = reviewRepository.findReviewByUserAndBeverageAndLocation(
                validUser, validBeverage, validLocation);

        // Assert
        assertFalse(foundReview.isPresent());
    }

    @Test
    void findReviewByUserAndBeverageAndLocationShouldReturnEmptyWhenUserDoesNotMatch() {
        // Arrange
        User user2 = userRepository.save(new User("TestUser2", "user2@example.com", "hash", Role.ROLE_USER, LocalDateTime.now()));
        reviewRepository.save(new Review(user2, validBeverage, validLocation, new BigDecimal("4.0"), "Review", LocalDateTime.now()));

        // Act
        Optional<Review> foundReview = reviewRepository.findReviewByUserAndBeverageAndLocation(
                validUser, validBeverage, validLocation);

        // Assert
        assertFalse(foundReview.isPresent());
    }

    @Test
    void findReviewByUserAndBeverageAndLocationShouldReturnEmptyWhenBeverageDoesNotMatch() {
        // Arrange
        Beverage beverage2 = beverageRepository.save(new Beverage(null, "Another Beer", "Stout", new BigDecimal("8.0"), "A different beer for testing reviews here", "https://example.com/another.jpg", LocalDateTime.now()));
        reviewRepository.save(new Review(validUser, beverage2, validLocation, new BigDecimal("4.0"), "Review", LocalDateTime.now()));

        // Act
        Optional<Review> foundReview = reviewRepository.findReviewByUserAndBeverageAndLocation(
                validUser, validBeverage, validLocation);

        // Assert
        assertFalse(foundReview.isPresent());
    }

    @Test
    void findReviewByUserAndBeverageAndLocationShouldReturnEmptyWhenLocationDoesNotMatch() {
        // Arrange
        Location location2 = locationRepository.save(new Location("Another Place", "456 Other St", "Other City", "Canada"));
        reviewRepository.save(new Review(validUser, validBeverage, location2, new BigDecimal("4.0"), "Review", LocalDateTime.now()));

        // Act
        Optional<Review> foundReview = reviewRepository.findReviewByUserAndBeverageAndLocation(
                validUser, validBeverage, validLocation);

        // Assert
        assertFalse(foundReview.isPresent());
    }

    @Test
    void findReviewByUserAndBeverageAndLocationShouldReturnEmptyWhenPartialMatchExists() {
        // Arrange
        User user2 = userRepository.save(new User("TestUser2", "user2@example.com", "hash", Role.ROLE_USER, LocalDateTime.now()));
        Beverage beverage2 = beverageRepository.save(new Beverage(null, "Another Beer", "Stout", new BigDecimal("8.0"), "A different beer for testing reviews here", "https://example.com/another.jpg", LocalDateTime.now()));
        Location location2 = locationRepository.save(new Location("Another Place", "456 Other St", "Other City", "Canada"));

        // Create reviews with partial matches but not exact combination
        reviewRepository.save(new Review(validUser, beverage2, validLocation, new BigDecimal("4.0"), "Review 1", LocalDateTime.now())); // User matches, beverage doesn't
        reviewRepository.save(new Review(user2, validBeverage, validLocation, new BigDecimal("5.0"), "Review 2", LocalDateTime.now())); // Beverage matches, user doesn't
        reviewRepository.save(new Review(validUser, validBeverage, location2, new BigDecimal("3.0"), "Review 3", LocalDateTime.now())); // User and beverage match, location doesn't

        // Act
        Optional<Review> foundReview = reviewRepository.findReviewByUserAndBeverageAndLocation(
                validUser, validBeverage, validLocation);

        // Assert
        assertFalse(foundReview.isPresent());
    }

    // ========== UPDATE OPERATIONS ==========

    // UPDATE - Happy Paths
    @Test
    void updateRatingShouldPersistChanges() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        savedReview.setRating(new BigDecimal("5.0"));
        reviewRepository.save(savedReview);
        Review updatedReview = reviewRepository.findById(reviewId).orElseThrow();

        // Assert
        assertEquals(new BigDecimal("5.0"), updatedReview.getRating());
    }

    @Test
    void updateNotesShouldPersistChanges() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        savedReview.setReviewNote("Updated notes");
        reviewRepository.save(savedReview);
        Review updatedReview = reviewRepository.findById(reviewId).orElseThrow();

        // Assert
        assertEquals("Updated notes", updatedReview.getReviewNote());
    }

    @Test
    void updateMultipleFieldsShouldPersistAllChanges() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        savedReview.setRating(new BigDecimal("3.0"));
        savedReview.setReviewNote("Changed my mind");
        reviewRepository.save(savedReview);
        Review updatedReview = reviewRepository.findById(reviewId).orElseThrow();

        // Assert
        assertEquals(new BigDecimal("3.0"), updatedReview.getRating());
        assertEquals("Changed my mind", updatedReview.getReviewNote());
    }

    // UPDATE - Unhappy Paths
    @Test
    void updateRatingToNullShouldThrowException() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);

        // Act & Assert
        savedReview.setRating(null);
        assertThrows(Exception.class, () -> reviewRepository.saveAndFlush(savedReview));
    }

    @Test
    void updateRatingBelowMinimumShouldThrowException() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);

        // Act & Assert
        savedReview.setRating(new BigDecimal("-0.1"));
        assertThrows(ConstraintViolationException.class, () -> reviewRepository.saveAndFlush(savedReview));
    }

    @Test
    void updateRatingAboveMaximumShouldThrowException() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);

        // Act & Assert
        savedReview.setRating(new BigDecimal("5.1"));
        assertThrows(ConstraintViolationException.class, () -> reviewRepository.saveAndFlush(savedReview));
    }

    // ========== DELETE OPERATIONS ==========

    // DELETE - Happy Paths
    @Test
    void deleteByIdShouldRemoveReview() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        reviewRepository.deleteById(reviewId);

        // Assert
        assertFalse(reviewRepository.existsById(reviewId));
        assertEquals(0, reviewRepository.count());
    }

    @Test
    void deleteByEntityShouldRemoveReview() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        reviewRepository.delete(savedReview);

        // Assert
        assertFalse(reviewRepository.existsById(reviewId));
    }

    @Test
    void deleteAllShouldRemoveAllReviews() {
        // Arrange
        Review review1 = new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "Good", LocalDateTime.now());
        Review review2 = new Review(validUser, validBeverage, validLocation, new BigDecimal("5.0"), "Excellent", LocalDateTime.now());
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // Act
        reviewRepository.deleteAll();

        // Assert
        assertEquals(0, reviewRepository.count());
    }

    @Test
    void deleteAllByIdShouldRemoveSelectedReviews() {
        // Arrange
        Review review1 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("4.0"), "Good", LocalDateTime.now()));
        Review review2 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("5.0"), "Excellent", LocalDateTime.now()));
        Review review3 = reviewRepository.save(new Review(validUser, validBeverage, validLocation, new BigDecimal("3.0"), "Okay", LocalDateTime.now()));

        // Act
        reviewRepository.deleteAllById(List.of(review1.getReviewId(), review2.getReviewId()));

        // Assert
        assertEquals(1, reviewRepository.count());
        assertTrue(reviewRepository.existsById(review3.getReviewId()));
    }

    // DELETE - Unhappy Paths
    @Test
    void deleteByIdShouldNotThrowExceptionWhenReviewDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> reviewRepository.deleteById(nonExistentId));
    }

    @Test
    void deleteByIdShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            reviewRepository.deleteById(null);
        });
    }

    @Test
    void deleteAllShouldNotThrowExceptionWhenRepositoryIsEmpty() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> reviewRepository.deleteAll());
    }

    // ========== CASCADE DELETE TESTS ==========

    @Test
    void deletingUserShouldCascadeDeleteReviews() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        userRepository.deleteById(validUser.getId());

        // Assert
        assertFalse(reviewRepository.existsById(reviewId));
    }

    @Test
    void deletingBeverageShouldCascadeDeleteReviews() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        beverageRepository.deleteById(validBeverage.getId());

        // Assert
        assertFalse(reviewRepository.existsById(reviewId));
    }

    @Test
    void deletingLocationShouldCascadeDeleteReviews() {
        // Arrange
        Review savedReview = reviewRepository.save(validReview);
        UUID reviewId = savedReview.getReviewId();

        // Act
        locationRepository.deleteById(validLocation.getId());

        // Assert
        assertFalse(reviewRepository.existsById(reviewId));
    }
}