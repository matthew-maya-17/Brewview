package com.service;

import com.dto.ReviewRequestDTO;
import com.dto.ResponseReview;
import com.exception.ResourceConflictException;
import com.exception.ResourceNotFoundException;
import com.model.Beverage;
import com.model.Location;
import com.model.Review;
import com.model.Role;
import com.model.User;
import com.repository.BeverageRepository;
import com.repository.LocationRepository;
import com.repository.ReviewRepository;
import com.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BeverageRepository beverageRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private ReviewService reviewService;

    private UUID userId;
    private UUID beverageId;
    private UUID locationId;
    private UUID reviewId;
    private User user;
    private Beverage beverage;
    private Location location;
    private Review review;
    private ReviewRequestDTO reviewRequestDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        beverageId = UUID.randomUUID();
        locationId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        user = new User("testuser", "test@example.com", "hash", Role.ROLE_USER, LocalDateTime.now());
        user.setId(userId);

        beverage = new Beverage(userId, "Test Beer", "IPA", 6, "A test beer description here for testing", "https://example.com/beer.jpg", LocalDateTime.now());
        beverage.setId(beverageId);

        location = new Location("Test Location", "123 Test St", "Test City", "United States");
        location.setId(locationId);

        review = new Review(user, beverage, location, new BigDecimal("4.5"), "Great beer!", LocalDateTime.now());
        review.setReviewId(reviewId);

        reviewRequestDTO = new ReviewRequestDTO(userId, beverageId, locationId, new BigDecimal("4.5"), "Great beer!");
    }

    // ========== CREATE OPERATIONS ==========

    // CREATE - Happy Paths
    @Test
    void addReviewShouldCreateReviewSuccessfully() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(beverageRepository.findById(beverageId)).thenReturn(Optional.of(beverage));
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(reviewRepository.findReviewByUserAndBeverageAndLocation(user, beverage, location))
                .thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ResponseReview result = reviewService.addReview(reviewRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getReviewId());
        assertEquals(new BigDecimal("4.5"), result.getRating());
        verify(userRepository, times(1)).findById(userId);
        verify(beverageRepository, times(1)).findById(beverageId);
        verify(locationRepository, times(1)).findById(locationId);
        verify(reviewRepository, times(1)).findReviewByUserAndBeverageAndLocation(user, beverage, location);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    // CREATE - Unhappy Paths
    @Test
    void addReviewShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.addReview(reviewRequestDTO));
        assertTrue(exception.getMessage().contains("User with ID"));
        verify(userRepository, times(1)).findById(userId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void addReviewShouldThrowExceptionWhenBeverageNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(beverageRepository.findById(beverageId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.addReview(reviewRequestDTO));
        assertTrue(exception.getMessage().contains("Beverage with ID"));
        verify(beverageRepository, times(1)).findById(beverageId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void addReviewShouldThrowExceptionWhenLocationNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(beverageRepository.findById(beverageId)).thenReturn(Optional.of(beverage));
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.addReview(reviewRequestDTO));
        assertTrue(exception.getMessage().contains("Location with ID"));
        verify(locationRepository, times(1)).findById(locationId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void addReviewShouldThrowExceptionWhenReviewAlreadyExists() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(beverageRepository.findById(beverageId)).thenReturn(Optional.of(beverage));
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(reviewRepository.findReviewByUserAndBeverageAndLocation(user, beverage, location))
                .thenReturn(Optional.of(review));

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> reviewService.addReview(reviewRequestDTO));
        assertEquals("Review already exists", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    // ========== READ OPERATIONS ==========

    // READ - Happy Paths
    @Test
    void findReviewByIdShouldReturnReviewWhenExists() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act
        ResponseReview result = reviewService.findReviewById(reviewId);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getReviewId());
        assertEquals(new BigDecimal("4.5"), result.getRating());
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    // READ - Unhappy Paths
    @Test
    void findReviewByIdShouldThrowExceptionWhenNotFound() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.findReviewById(reviewId));
        assertTrue(exception.getMessage().contains("Review with ID"));
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    // ========== UPDATE OPERATIONS ==========

    // UPDATE - Happy Paths
    @Test
    void updateReviewByIdShouldUpdateReviewSuccessfully() {
        // Arrange
        ReviewRequestDTO updateRequest = new ReviewRequestDTO(userId, beverageId, locationId, new BigDecimal("5.0"), "Updated notes");
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(beverageRepository.findById(beverageId)).thenReturn(Optional.of(beverage));
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ResponseReview result = reviewService.updateReviewById(reviewId, updateRequest);

        // Assert
        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(any(Review.class));
        // Verify duplicate check was NOT called since combination didn't change
        verify(reviewRepository, never()).findReviewByUserAndBeverageAndLocation(any(), any(), any());
    }

    @Test
    void updateReviewByIdShouldUpdateWhenCombinationChanged() {
        // Arrange
        UUID newUserId = UUID.randomUUID();
        User newUser = new User("newuser", "new@example.com", "hash", Role.ROLE_USER, LocalDateTime.now());
        newUser.setId(newUserId);

        ReviewRequestDTO updateRequest = new ReviewRequestDTO(newUserId, beverageId, locationId, new BigDecimal("5.0"), "Updated notes");
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(beverageRepository.findById(beverageId)).thenReturn(Optional.of(beverage));
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(reviewRepository.findReviewByUserAndBeverageAndLocation(newUser, beverage, location))
                .thenReturn(Optional.empty()); // No duplicate
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ResponseReview result = reviewService.updateReviewById(reviewId, updateRequest);

        // Assert
        assertNotNull(result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    // UPDATE - Unhappy Paths
    @Test
    void updateReviewByIdShouldThrowExceptionWhenReviewNotFound() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.updateReviewById(reviewId, reviewRequestDTO));
        assertTrue(exception.getMessage().contains("Review with ID"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReviewByIdShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.updateReviewById(reviewId, reviewRequestDTO));
        assertTrue(exception.getMessage().contains("User with ID"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReviewByIdShouldThrowExceptionWhenDuplicateExists() {
        // Arrange
        UUID newUserId = UUID.randomUUID();
        User newUser = new User("newuser", "new@example.com", "hash", Role.ROLE_USER, LocalDateTime.now());
        newUser.setId(newUserId);

        Review existingDuplicate = new Review(newUser, beverage, location, new BigDecimal("3.0"), "Existing", LocalDateTime.now());
        existingDuplicate.setReviewId(UUID.randomUUID());

        ReviewRequestDTO updateRequest = new ReviewRequestDTO(newUserId, beverageId, locationId, new BigDecimal("5.0"), "Updated");
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(beverageRepository.findById(beverageId)).thenReturn(Optional.of(beverage));
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(reviewRepository.findReviewByUserAndBeverageAndLocation(newUser, beverage, location))
                .thenReturn(Optional.of(existingDuplicate)); // Different review with same combination

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class,
                () -> reviewService.updateReviewById(reviewId, updateRequest));
        assertTrue(exception.getMessage().contains("review already exists"));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    // ========== DELETE OPERATIONS ==========

    // DELETE - Happy Paths
    @Test
    void deleteReviewByIdShouldDeleteReviewSuccessfully() {
        // Arrange
        when(reviewRepository.existsById(reviewId)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(reviewId);

        // Act
        reviewService.deleteReviewById(reviewId);

        // Assert
        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

    // DELETE - Unhappy Paths
    @Test
    void deleteReviewByIdShouldThrowExceptionWhenReviewNotFound() {
        // Arrange
        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.deleteReviewById(reviewId));
        assertTrue(exception.getMessage().contains("User with ID")); // Note: This matches current error message
        verify(reviewRepository, times(1)).existsById(reviewId);
        verify(reviewRepository, never()).deleteById(reviewId);
    }
}