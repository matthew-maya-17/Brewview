package com.controller;

import com.dto.LocationCreateDTO;
import com.dto.LocationUpdateDTO;
import com.dto.ResponseLocation;
import com.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/locations")
@Tag(
        name = "Location Management",
        description = "Endpoints for creating, retrieving, updating, and deleting locations."
)
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(
            summary = "Create a new location",
            description = "Creates a new Location entity using the fields provided in the JSON request body.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created location"),
                    @ApiResponse(responseCode = "400", description = "Bad request – invalid or missing fields"),
                    @ApiResponse(responseCode = "409", description = "Conflict – location already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ResponseLocation> createLocation(@Valid @RequestBody LocationCreateDTO locationRequest) {
        ResponseLocation returnedLocation = locationService.createLocation(locationRequest);
        return new ResponseEntity<>(returnedLocation, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Retrieve all locations",
            description = "Returns a list of all Location entities in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of locations."),
                    @ApiResponse(responseCode = "204", description = "Request was successful but no locations exist in the system."),
                    @ApiResponse(responseCode = "400", description = "Bad request. Invalid query parameters were provided."),
                    @ApiResponse(responseCode = "403", description = "Forbidden. You do not have permission to access this resource."),
                    @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred.")
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @GetMapping()
    public ResponseEntity<List<ResponseLocation>> getAllLocations() {
        List<ResponseLocation> returnedLocationList = locationService.getAllLocations();

        if (returnedLocationList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(returnedLocationList);
    }

    @Operation(
            summary = "Retrieve a location by ID",
            description = "Returns a Location entity that matches the provided UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the location with the given ID."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to view this location."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No location exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseLocation> getLocationById(
            @Parameter(
                    name = "id",
                    description = "UUID of the location to retrieve.",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id
    ) {
        ResponseLocation returnedLocation = locationService.getLocationById(id);
        return ResponseEntity.ok(returnedLocation);
    }


    @Operation(
            summary = "Retrieve a location by name",
            description = "Returns a Location entity that matches the provided location name.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the location with the given name."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided location name is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to access this resource."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to view this location."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No location exists with the provided name."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @GetMapping("/name/{locationName}")
    public ResponseEntity<List<ResponseLocation>> getLocationsByName(
            @Parameter(
                    name = "locationName",
                    description = "Name to search for in locations (case-insensitive, partial match)",
                    required = true,
                    example = "Brew"
            )
            @PathVariable String locationName
    ) {
        List<ResponseLocation> returnedLocationList = locationService.getLocationsByName(locationName);

        if (returnedLocationList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(returnedLocationList);
    }

    @Operation(
            summary = "Retrieve locations by city",
            description = "Returns a list of Location entities that match the provided city.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved locations in the given city."
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Request was successful but no locations exist in the system."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided city is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @GetMapping("/city/{city}")
    public ResponseEntity<List<ResponseLocation>> getLocationsByCity(
            @Parameter(
                    name = "city",
                    description = "City to filter locations by.",
                    required = true,
                    example = "New York"
            )
            @PathVariable String city
    ) {
        List<ResponseLocation> returnedLocationList = locationService.getLocationsByCity(city);

        if (returnedLocationList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(returnedLocationList);
    }

    @Operation(
            summary = "Retrieve locations by country",
            description = "Returns a list of Location entities that match the provided country.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved locations in the given country."
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Request was successful but no locations exist in the system."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided country is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to access this resource."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to view locations."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @GetMapping("/country/{country}")
    public ResponseEntity<List<ResponseLocation>> getLocationsByCountry(
            @Parameter(
                    name = "country",
                    description = "Country to filter locations by.",
                    required = true,
                    example = "USA"
            )
            @PathVariable String country
    ) {
        List<ResponseLocation> returnedLocationList = locationService.getLocationsByCountry(country);

        if (returnedLocationList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(returnedLocationList);
    }

    @Operation(
            summary = "Retrieve locations by city and country",
            description = "Returns a list of Location entities that match both the provided city and country.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved locations in the given city and country."
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Request was successful but no locations exist in the system."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided parameters are invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to access this resource."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to view locations."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @GetMapping("/search/city/country")
    public ResponseEntity<List<ResponseLocation>> getLocationsByCityAndCountry(
            @Parameter(
                    name = "city",
                    description = "City to filter locations by.",
                    required = true,
                    example = "New York"
            )
            @RequestParam String city,
            @Parameter(
                    name = "country",
                    description = "Country to filter locations by.",
                    required = true,
                    example = "USA"
            )
            @RequestParam String country
    ) {
        List<ResponseLocation> returnedLocationList = locationService.getLocationsByCityAndCountry(city, country);

        if (returnedLocationList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(returnedLocationList);
    }

    @Operation(
            summary = "Update a location by ID",
            description = "Updates a Location entity with the specified UUID using the fields provided in the JSON request body.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the location with the given ID."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or the request body contains invalid/malformed fields."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to update the location."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to update this location."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No location exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict. The update would violate a uniqueness constraint (e.g., location name already in use)."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while updating the location."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseLocation> updateLocationById(
            @Parameter(
                    name = "id",
                    description = "UUID of the location to update.",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Valid @RequestBody LocationUpdateDTO updateLocationRequest
    ) {
        ResponseLocation returnedLocation = locationService.updateLocation(id, updateLocationRequest);
        return ResponseEntity.ok(returnedLocation);
    }

    @Operation(
            summary = "Delete a location by ID",
            description = "Deletes the Location entity that matches the provided UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted the location. No content is returned."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to delete the location."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to delete this location."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No location exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while deleting the location."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocationById(
            @Parameter(
                    name = "id",
                    description = "UUID of the location to delete.",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id
    ) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}