package com.controller;

import com.dto.BeverageRequestDTO;
import com.dto.ResponseBeverage;
import com.service.BeverageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/beverages")
@Tag(
        name = "Beverage Management",
        description = "Endpoints for creating, retrieving, updating, and deleting beverages"
)

public class BeverageController {

    private final BeverageService beverageService;

    public BeverageController(BeverageService beverageService) {
        this.beverageService = beverageService;
    }

    @Operation(
            summary = "Create a new beverage",
            description = "Creates a new Beverage entity using the fields provided in the JSON request body.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created beverage"),
                    @ApiResponse(responseCode = "400", description = "Bad request – invalid or missing fields"),
                    @ApiResponse(responseCode = "409", description = "Conflict – beverage already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseBeverage> createBeverage(@Valid @RequestBody BeverageRequestDTO requestDTO) {
        ResponseBeverage createdBeverage = beverageService.createBeverage(requestDTO);
        return new ResponseEntity<>(createdBeverage, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create multiple beverages",
            description = "Creates multiple Beverage entities using a list of beverages provided in the JSON request body.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created beverages"),
                    @ApiResponse(responseCode = "400", description = "Bad request – invalid or missing fields"),
                    @ApiResponse(responseCode = "409", description = "Conflict – one or more beverages already exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/create/batch")
    public ResponseEntity<List<ResponseBeverage>> createBeverages(@Valid @RequestBody List<BeverageRequestDTO> requestDTOs) {
        List<ResponseBeverage> createdBeverages = beverageService.createBeverages(requestDTOs);
        return new ResponseEntity<>(createdBeverages, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Retrieve all beverages",
            description = "Returns a list of all Beverage entities in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of beverages."),
                    @ApiResponse(responseCode = "204", description = "Request was successful but no beverages exist in the system."),
                    @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred.")
            }
    )
    @GetMapping()
    public ResponseEntity<List<ResponseBeverage>> getAllBeverages() {
        List<ResponseBeverage> beverages = beverageService.getAllBeverages();
        return ResponseEntity.ok(beverages);
    }

    @Operation(
            summary = "Retrieve a beverage by ID",
            description = "Returns a Beverage entity that matches the provided UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the beverage with the given ID."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No beverage currently exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ResponseBeverage> getBeverageById(
            @Parameter(
                    name = "id",
                    description = "UUID of the beverage to retrieve",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id
    ) {
        ResponseBeverage beverage = beverageService.getBeverageById(id);
        return ResponseEntity.ok(beverage);
    }

    @Operation(
            summary = "Retrieve a beverage by name",
            description = "Returns a Beverage entity that matches the provided beverage name.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the beverage with the given name."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided name is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No beverage exists with the provided name."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("/name")
    public ResponseEntity<ResponseBeverage> getBeverageByName(
            @Parameter(
                    name = "name",
                    description = "Name of the beverage to retrieve",
                    required = true,
                    example = "Sierra Nevada Pale Ale"
            )
            @RequestParam String name
    ) {
        Optional<ResponseBeverage> beverage = beverageService.getBeverageByName(name);
        return beverage.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Retrieve beverages by type",
            description = "Returns a list of Beverage entities that match the provided type.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved beverages with the given type."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided type is invalid."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ResponseBeverage>> getBeveragesByType(
            @Parameter(
                    name = "type",
                    description = "Type of beverages to retrieve",
                    required = true,
                    example = "IPA"
            )
            @PathVariable String type
    ) {
        List<ResponseBeverage> beverages = beverageService.getBeveragesByType(type);
        return ResponseEntity.ok(beverages);
    }

    @Operation(
            summary = "Retrieve beverages by ABV range",
            description = "Returns a list of Beverage entities with ABV between the specified minimum and maximum values.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved beverages within the ABV range."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Invalid ABV range provided."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("/abv")
    public ResponseEntity<List<ResponseBeverage>> getBeveragesByAbvRange(
            @Parameter(
                    name = "min",
                    description = "Minimum ABV value",
                    required = true,
                    example = "4"
            )
            @RequestParam int min,
            @Parameter(
                    name = "max",
                    description = "Maximum ABV value",
                    required = true,
                    example = "8"
            )
            @RequestParam int max
    ) {
        List<ResponseBeverage> beverages = beverageService.getBeveragesByAbvRange(min, max);
        return ResponseEntity.ok(beverages);
    }

    @Operation(
            summary = "Retrieve beverages by type and ABV range",
            description = "Returns a list of Beverage entities matching both the type and ABV range criteria.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved beverages matching the criteria."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Invalid parameters provided."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("/type/{type}/abv")
    public ResponseEntity<List<ResponseBeverage>> getBeveragesByTypeAndAbvRange(
            @Parameter(
                    name = "type",
                    description = "Type of beverages to retrieve",
                    required = true,
                    example = "IPA"
            )
            @PathVariable String type,
            @Parameter(
                    name = "min",
                    description = "Minimum ABV value",
                    required = true,
                    example = "5"
            )
            @RequestParam int min,
            @Parameter(
                    name = "max",
                    description = "Maximum ABV value",
                    required = true,
                    example = "7"
            )
            @RequestParam int max
    ) {
        List<ResponseBeverage> beverages = beverageService.getBeveragesByTypeAndAbvRange(type, min, max);
        return ResponseEntity.ok(beverages);
    }

    @Operation(
            summary = "Check if beverage exists by ID",
            description = "Returns a boolean indicating whether a beverage with the given ID exists.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully checked beverage existence."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> beverageExists(
            @Parameter(
                    name = "id",
                    description = "UUID of the beverage to check",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id
    ) {
        boolean exists = beverageService.beverageExists(id);
        return ResponseEntity.ok(exists);
    }

    @Operation(
            summary = "Check if beverage exists by name",
            description = "Returns a boolean indicating whether a beverage with the given name exists.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully checked beverage name existence."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided name is invalid."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("/name/{name}/exists")
    public ResponseEntity<Boolean> beverageNameExists(
            @Parameter(
                    name = "name",
                    description = "Name of the beverage to check",
                    required = true,
                    example = "Sierra Nevada Pale Ale"
            )
            @PathVariable String name
    ) {
        boolean exists = beverageService.beverageNameExists(name);
        return ResponseEntity.ok(exists);
    }

    @Operation(
            summary = "Get total beverage count",
            description = "Returns the total number of beverages in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved beverage count."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalBeverageCount() {
        long count = beverageService.getTotalBeverageCount();
        return ResponseEntity.ok(count);
    }

    @Operation(
            summary = "Update a beverage by ID",
            description = "Updates all fields of the Beverage entity with the specified UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the beverage with the given ID."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or the request body contains invalid/malformed fields."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No beverage exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict. The update would violate a uniqueness constraint."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while updating the beverage."
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ResponseBeverage> updateBeverage(
            @Parameter(
                    name = "id",
                    description = "UUID of the beverage to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Valid @RequestBody BeverageRequestDTO requestDTO
    ) {
        ResponseBeverage updatedBeverage = beverageService.updateBeverage(id, requestDTO);
        return ResponseEntity.ok(updatedBeverage);
    }

    @Operation(
            summary = "Update beverage name",
            description = "Updates only the name of the Beverage entity with the specified UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the beverage name."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Invalid UUID or name provided."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No beverage exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict. The name is already in use."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error."
                    )
            }
    )
    @PatchMapping("/{id}/name")
    public ResponseEntity<ResponseBeverage> updateBeverageName(
            @Parameter(
                    name = "id",
                    description = "UUID of the beverage to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Parameter(
                    name = "name",
                    description = "New name for the beverage",
                    required = true,
                    example = "Updated Beer Name"
            )
            @RequestParam String name
    ) {
        ResponseBeverage updatedBeverage = beverageService.updateBeverageName(id, name);
        return ResponseEntity.ok(updatedBeverage);
    }

    @Operation(
            summary = "Update beverage type",
            description = "Updates only the type of the Beverage entity with the specified UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the beverage type."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Invalid UUID or type provided."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No beverage exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error."
                    )
            }
    )
    @PatchMapping("/{id}/type")
    public ResponseEntity<ResponseBeverage> updateBeverageType(
            @Parameter(
                    name = "id",
                    description = "UUID of the beverage to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Parameter(
                    name = "type",
                    description = "New type for the beverage",
                    required = true,
                    example = "Stout"
            )
            @RequestParam String type
    ) {
        ResponseBeverage updatedBeverage = beverageService.updateBeverageType(id, type);
        return ResponseEntity.ok(updatedBeverage);
    }

    @Operation(
            summary = "Update beverage ABV",
            description = "Updates only the ABV of the Beverage entity with the specified UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the beverage ABV."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Invalid UUID or ABV provided."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No beverage exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error."
                    )
            }
    )
    @PatchMapping("/{id}/abv")
    public ResponseEntity<ResponseBeverage> updateBeverageAbv(
            @Parameter(
                    name = "id",
                    description = "UUID of the beverage to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Parameter(
                    name = "abv",
                    description = "New ABV for the beverage",
                    required = true,
                    example = "7"
            )
            @RequestParam int abv
    ) {
        ResponseBeverage updatedBeverage = beverageService.updateBeverageAbv(id, abv);
        return ResponseEntity.ok(updatedBeverage);
    }

    @Operation(
            summary = "Update beverage description",
            description = "Updates only the description of the Beverage entity with the specified UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the beverage description."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Invalid UUID or description provided."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No beverage exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error."
                    )
            }
    )
    @PatchMapping("/{id}/description")
    public ResponseEntity<ResponseBeverage> updateBeverageDescription(
            @Parameter(
                    name = "id",
                    description = "UUID of the beverage to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Parameter(
                    name = "description",
                    description = "New description for the beverage",
                    required = true,
                    example = "An updated description for this delicious beverage"
            )
            @RequestParam String description
    ) {
        ResponseBeverage updatedBeverage = beverageService.updateBeverageDescription(id, description);
        return ResponseEntity.ok(updatedBeverage);
    }

    @Operation(
            summary = "Update beverage image URL",
            description = "Updates only the image URL of the Beverage entity with the specified UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the beverage image URL."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Invalid UUID or image URL provided."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No beverage exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error."
                    )
            }
    )
    @PatchMapping("/{id}/image")
    public ResponseEntity<ResponseBeverage> updateBeverageImageUrl(
            @Parameter(
                    name = "id",
                    description = "UUID of the beverage to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Parameter(
                    name = "imageUrl",
                    description = "New image URL for the beverage",
                    required = true,
                    example = "https://example.com/new-image.jpg"
            )
            @RequestParam String imageUrl
    ) {
        ResponseBeverage updatedBeverage = beverageService.updateBeverageImageUrl(id, imageUrl);
        return ResponseEntity.ok(updatedBeverage);
    }

    @Operation(
            summary = "Delete a beverage by ID",
            description = "Deletes the Beverage entity that matches the provided UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted the beverage. No content is returned."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No beverage exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while deleting the beverage."
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeverage(
            @Parameter(
                    name = "id",
                    description = "UUID of the beverage to delete",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id
    ) {
        beverageService.deleteBeverage(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete all beverages",
            description = "Deletes all Beverage entities in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted all beverages. No content is returned."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while deleting beverages."
                    )
            }
    )
    @DeleteMapping()
    public ResponseEntity<Void> deleteAllBeverages() {
        beverageService.deleteAllBeverages();
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Delete multiple beverages by IDs",
            description = "Deletes multiple Beverage entities that match the provided list of UUIDs.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted the beverages. No content is returned."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided list is invalid or empty."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while deleting beverages."
                    )
            }
    )
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteBeveragesByIds(
            @Parameter(
                    name = "ids",
                    description = "List of UUIDs of beverages to delete",
                    required = true
            )
            @RequestBody List<UUID> ids
    ) {
        beverageService.deleteBeveragesByIds(ids);
        return ResponseEntity.noContent().build();
    }
}
