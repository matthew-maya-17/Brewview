package com.service;

import com.dto.*;
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
import java.util.UUID;

@Service
public class ReviewService {

    private ReviewRepository reviewRepository;

    private UserRepository userRepository;

    private BeverageRepository beverageRepository;

    private LocationRepository locationRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, BeverageRepository beverageRepository, LocationRepository locationRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.beverageRepository = beverageRepository;
        this.locationRepository = locationRepository;
    }

    //CREATE
    public ResponseReview addReview(ReviewRequestDTO reviewRequestDTO){
        return null;
    }

    //READ
    public List<ResponseReview> findAllReviews(){
        return reviewRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public List<ResponseReview> findAllReviewsByUser(User user){
        return reviewRepository.findReviewsByUser(user)
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public List<ResponseReview> findAllReviewsByBeverage(Beverage beverage){
        return reviewRepository.findReviewsByBeverage(beverage)
                .stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    public List<ResponseReview> findAllReviewsByLocation(Location location){
        return reviewRepository.findReviewsByLocation(location)
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

    public ResponseReview findReviewById(UUID id){
        Review review =  reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review with ID: " + id + " does not exist."));
        return convertToResponseDto(review);
    }

    //UPDATE

    //DELETE
    public void deleteReviewById(UUID reviewId){
        if(!reviewRepository.existsById(reviewId)){
            throw new ResourceNotFoundException("User with ID: " + reviewId + " does not exist.");
        }
        userRepository.deleteById(reviewId);
    }

    // Helper Methods for Conversion
    private Review convertToEntity(ReviewRequestDTO dto){
        Review review = new Review();
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        Beverage beverage = beverageRepository.findById(dto.getBeverageId())
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + dto.getBeverageId()));

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + dto.getLocationId()));

        review.setUser(user);
        review.setBeverage(beverage);
        review.setLocation(location);
        review.setRating(dto.getRating());
        review.setReviewNote(dto.getReviewNote());

        return review;
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
