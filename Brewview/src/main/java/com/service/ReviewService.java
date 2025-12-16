package com.service;

import com.dto.*;
import com.exception.ResourceConflictException;
import com.exception.ResourceNotFoundException;
import com.model.Beverage;
import com.model.Location;
import com.model.Review;
import com.model.User;
import com.repository.BeverageRepository;
import com.repository.LocationRepository;
import com.repository.ReviewRepository;
import com.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final BeverageRepository beverageRepository;

    private final LocationRepository locationRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, BeverageRepository beverageRepository, LocationRepository locationRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.beverageRepository = beverageRepository;
        this.locationRepository = locationRepository;
    }

    //CREATE
    public ResponseReview addReview(ReviewRequestDTO reviewRequestDTO){
        User user = userRepository.findById(reviewRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + reviewRequestDTO.getUserId() + " does not exist."));

        Beverage beverage = beverageRepository.findById(reviewRequestDTO.getBeverageId())
                .orElseThrow(() -> new ResourceNotFoundException("Beverage with ID: " + reviewRequestDTO.getBeverageId() + " does not exist."));

        Location location = locationRepository.findById(reviewRequestDTO.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location with ID: " + reviewRequestDTO.getLocationId() + " does not exist."));

        if(reviewRepository.findReviewByUserAndBeverageAndLocation(user, beverage, location).isPresent()){
            throw new ResourceConflictException("Review already exists");
        }

        Review newReview = new Review();
        newReview.setUser(user);
        newReview.setBeverage(beverage);
        newReview.setLocation(location);
        newReview.setRating(reviewRequestDTO.getRating());
        newReview.setReviewNote(reviewRequestDTO.getReviewNote());

        Review savedReview = reviewRepository.save(newReview);
        return convertToResponseDto(savedReview);
    }

    //READ
    public List<ResponseReview> findAllReviews(){
        return reviewRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public List<ResponseReview> findAllReviewsByUserId(UUID userId){
        return reviewRepository.findReviewsByUserId(userId)
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public List<ResponseReview> findAllReviewsByBeverageId(UUID beverageId){
        return reviewRepository.findReviewsByBeverageId(beverageId)
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public List<ResponseReview> findAllReviewsByLocationId(UUID locationId){
        return reviewRepository.findReviewsByLocationId(locationId)
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public List<ResponseReview> findReviewsByRatingBetween(BigDecimal minRating, BigDecimal maxRating){
        return reviewRepository.findReviewsByRatingBetween(minRating, maxRating)
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public ResponseReview findReviewById(UUID reviewId){
        // Find existing review
        Review review =  reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review with ID: " + reviewId + " does not exist."));
        
        return convertToResponseDto(review);
    }

    //UPDATE
    public ResponseReview updateReviewById(UUID reviewId, ReviewRequestDTO updateRequest){
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review with ID: " + reviewId + " does not exist."));

        // Determine final values: use existing if not provided in update request
        User finalUser = existingReview.getUser();
        Beverage finalBeverage = existingReview.getBeverage();
        Location finalLocation = existingReview.getLocation();

        // Update user if provided
        if (updateRequest.getUserId() != null) {
            User newUser = userRepository.findById(updateRequest.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + updateRequest.getUserId() + " does not exist."));

            // Only update if different
            if (!newUser.getId().equals(existingReview.getUser().getId())) {
                finalUser = newUser;
            }
        }

        // Update beverage if provided
        if (updateRequest.getBeverageId() != null) {
            Beverage newBeverage = beverageRepository.findById(updateRequest.getBeverageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Beverage with ID: " + updateRequest.getBeverageId() + " does not exist."));

            // Only update if different
            if (!newBeverage.getId().equals(existingReview.getBeverage().getId())) {
                finalBeverage = newBeverage;
            }
        }

        // Update location if provided
        if (updateRequest.getLocationId() != null) {
            Location newLocation = locationRepository.findById(updateRequest.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location with ID: " + updateRequest.getLocationId() + " does not exist."));

            // Only update if different
            if (!newLocation.getId().equals(existingReview.getLocation().getId())) {
                finalLocation = newLocation;
            }
        }

        // Check if the final combination would create a duplicate (excluding current review)
        // Only check if any of the relationships changed
        boolean combinationChanged = !finalUser.getId().equals(existingReview.getUser().getId()) ||
                !finalBeverage.getId().equals(existingReview.getBeverage().getId()) ||
                !finalLocation.getId().equals(existingReview.getLocation().getId());

        if (combinationChanged) {
            Optional<Review> duplicateReview = reviewRepository
                    .findReviewByUserAndBeverageAndLocation(finalUser, finalBeverage, finalLocation);

            // Check if duplicate exists and it's not the current review
            if (duplicateReview.isPresent() && !duplicateReview.get().getReviewId().equals(reviewId)) {
                throw new ResourceConflictException("A review already exists for this combination of User, Beverage, and Location");
            }
        }

        // Update the review entity with final values
        existingReview.setUser(finalUser);
        existingReview.setBeverage(finalBeverage);
        existingReview.setLocation(finalLocation);

        // Update rating if provided
        if (updateRequest.getRating() != null) {
            existingReview.setRating(updateRequest.getRating());
        }

        // Update review notes if provided
        if (updateRequest.getReviewNote() != null) {
            existingReview.setReviewNote(updateRequest.getReviewNote());
        }

        Review updatedReview = reviewRepository.save(existingReview);
        return convertToResponseDto(updatedReview);
    }


    //DELETE
    public void deleteReviewById(UUID reviewId){
        if(!reviewRepository.existsById(reviewId)){
            throw new ResourceNotFoundException("User with ID: " + reviewId + " does not exist.");
        }
        reviewRepository.deleteById(reviewId);
    }

    private ResponseReview convertToResponseDto(Review review){
        ResponseUser user = new ResponseUser(
                review.getUser().getId(),
                review.getUser().getUsername(),
                review.getUser().getEmail(),
                review.getUser().getRoleName(),
                review.getUser().getTimestamp()
        );

        ResponseBeverage beverage = new ResponseBeverage(
                review.getBeverage().getId(),
                review.getBeverage().getBeverageName(),
                review.getBeverage().getType(),
                review.getBeverage().getAbv(),
                review.getBeverage().getDescription(),
                review.getBeverage().getImageUrl(),
                review.getBeverage().getCreatedAt()
        );

        ResponseLocation location = new ResponseLocation(
                review.getLocation().getId(),
                review.getLocation().getLocationName(),
                review.getLocation().getAddress(),
                review.getLocation().getCity(),
                review.getLocation().getCountry()
        );

        return new ResponseReview(
                review.getReviewId(),
                user,
                beverage,
                location,
                review.getRating(),
                review.getReviewNote(),
                review.getCreatedAt()
        );
    }
}
