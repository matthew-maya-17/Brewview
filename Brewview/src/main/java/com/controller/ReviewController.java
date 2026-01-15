package com.controller;

import com.dto.ResponseReview;
import com.dto.ReviewRequestDTO;
import com.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/reviews")
@Tag(
        name = "Review Management",
        description = "Endpoints for creating, retrieving, updating, and deleting reviews"
)
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @Operation(
            summary = "Create a new review",
            description = "Creates a new Review entity using the fields provided in the JSON request body.",
            responses = {
                @ApiResponse(responseCode = "201", description = "Successfully created review"),
                @ApiResponse(responseCode = "400", description = "Bad request – invalid or missing fields"),
                @ApiResponse(responseCode = "409", description = "Conflict – review already exists"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseReview> addReview(@Valid @RequestBody ReviewRequestDTO reviewReqest){
        ResponseReview returnedReview = reviewService.addReview(reviewReqest);
        return new ResponseEntity<>(returnedReview, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Retrieve all reviews,",
            description = "Returns a list of all review entities in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of reviews."),
                    @ApiResponse(responseCode = "204", description = "Request was successful but no reviews exist in the system."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Invalid query parameters were provided."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication is required."),
                    @ApiResponse(responseCode = "403", description = "Forbidden. You do not have permission to access this resource."),
                    @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred.")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<ResponseReview>> getAllReview(){
        List<ResponseReview> returnedReviewList = reviewService.findAllReviews();
        return ResponseEntity.ok(returnedReviewList);
    }

    @Operation(
            summary = "Retrieve all reviews by User ID",
            description = "Returns a list of reviews with the matching User ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of reviews with the given user ID."
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Request was successful but no reviews exist in the system."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided user UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to access this resource."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to view this review."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No users exists with the provided User ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("all/user/{id}")
    public ResponseEntity<List<ResponseReview>> getAllReviewsByUserId(
            @Parameter(
                    name = "id",
                    description = "The UUID of the user whose associated reviews will be returned.",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID userId){
        List<ResponseReview> returnedReviewList = reviewService.findAllReviewsByUserId(userId);
        return ResponseEntity.ok(returnedReviewList);
    }

    @Operation(
            summary = "Retrieve all reviews by beverage ID",
            description = "Returns a list of reviews with the matching beverage ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of reviews with the given beverage ID."
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Request was successful but no reviews exist in the system."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided beverage UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to access this resource."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to view this review."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No review exists with the provided beverage ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("all/beverage/{id}")
    public ResponseEntity<List<ResponseReview>> getAllReviewsByBeverageId(
            @Parameter(
                    name = "id",
                    description = "The UUID of the beverage whose associated reviews will be returned.",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID beverageId){
        List<ResponseReview> returnedReviewList = reviewService.findAllReviewsByBeverageId(beverageId);
        return ResponseEntity.ok(returnedReviewList);
    }

    @Operation(
            summary = "Retrieve all reviews by location ID",
            description = "Returns a list of reviews with the matching location ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of reviews with the given location ID."
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Request was successful but no reviews exist in the system."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided location UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to access this resource."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to view this review."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No user exists with the provided location ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("all/location/{id}")
    public ResponseEntity<List<ResponseReview>> getAllReviewsByLocationId(
            @Parameter(
                    name = "id",
                    description = "The UUID of the location whose associated reviews will be returned.",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID locationId){
        List<ResponseReview> returnedReviewList = reviewService.findAllReviewsByLocationId(locationId);
        return ResponseEntity.ok(returnedReviewList);
    }

    @GetMapping("all/ratings")
    public ResponseEntity<List<ResponseReview>> getAllReviewsByRatingBetween(
            @Parameter(
                    name = "minimum rating",
                    description = "The lower bound of the rating range used to filter results (inclusive).",
                    required = true,
                    example = "1.0"
            )
            @RequestParam BigDecimal minRating,
            @Parameter(
                    name = "maximum rating",
                    description = "The upper bound of the rating range used to filter results (inclusive).",
                    required = true,
                    example = "5.0"
            )
            @RequestParam BigDecimal maxRating){
        List<ResponseReview> returnedReviewList = reviewService.findReviewsByRatingBetween(minRating, maxRating);
        return ResponseEntity.ok(returnedReviewList);
    }

    @Operation(
            summary = "Retrieve a Review by ID",
            description = "Returns a Review entity that matches the provided UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the user with the given ID."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to access this resource."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to view this user."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No user exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ResponseReview> getReviewById(
            @Parameter(
                    name = "id",
                    description = "UUID of the review to retrieve",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reviewId
    ){
        ResponseReview returnedReview = reviewService.findReviewById(reviewId);
        return ResponseEntity.ok(returnedReview);
    }

    @Operation(
            summary = "Update a review by review ID",
            description = "Updates a review.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the review with the given ID."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided review UUID is invalid or the request body contains invalid/malformed fields."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to update the review."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to update this review."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No review exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict. The update would violate a uniqueness constraint."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while updating the review."
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ResponseReview> updateReviewById(
            @Parameter(
                    name = "id",
                    description = "UUID of the review to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewRequestDTO updateRequest){
        ResponseReview returnedReview = reviewService.updateReviewById(reviewId, updateRequest);
        return ResponseEntity.ok(returnedReview);
    }

    @Operation(
            summary = "Delete a review by ID",
            description = "Deletes the Review entity that matches the provided UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted the review. No content is returned."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to delete the review."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to delete this review."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No user exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while deleting the review."
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReviewById(
            @Parameter(
                    name = "id",
                    description = "UUID of the review to delete",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            UUID reviewId){
        reviewService.deleteReviewById(reviewId);
        return ResponseEntity.noContent().build();
    }
}