package com.controller;

import com.dto.CreateUserRequest;
import com.dto.ResponseUser;
import com.dto.UpdateUserRequest;
import com.service.UserService;
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
@RequestMapping(path = "/api/users")
@Tag(
        name = "User Management",
        description = "Endpoints for creating, retrieving, updating, and deleting users"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new User entity using the fields provided in the JSON request body.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created user"),
                    @ApiResponse(responseCode = "400", description = "Bad request – invalid or missing fields"),
                    @ApiResponse(responseCode = "409", description = "Conflict – user already exists"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseUser> addNewUser(@Valid @RequestBody CreateUserRequest userRequest) {
        ResponseUser returnedUser = userService.addUser(userRequest);
        return new ResponseEntity<>(returnedUser, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Retrieve all users",
            description = "Returns a list of all User entities in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. Authentication is required."),
                    @ApiResponse(responseCode = "403", description = "Forbidden. You do not have permission to access this resource."),
                    @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred.")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<ResponseUser>> getAllUsers(){
        List<ResponseUser> returnedUserList = userService.findAllUsers();
        return ResponseEntity.ok(returnedUserList);
    }

    @Operation(
            summary = "Retrieve a user by ID",
            description = "Returns a User entity that matches the provided UUID.",
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseUser> getUserById(
            @Parameter(
                    name = "id",
                    description = "UUID of the user to retrieve",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id
    ){
        ResponseUser returnedUser = userService.findUserById(id);
        return ResponseEntity.ok(returnedUser);
    }

    @Operation(
            summary = "Retrieve a user by username",
            description = "Returns a User entity that matches the provided username.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the user with the given username."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided username is invalid or malformed."
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
                            description = "Not Found. No user exists with the provided username."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/username/{username}")
    public ResponseEntity<ResponseUser> getUserByUsername(
            @Parameter(
                    name = "username",
                    description = "username of the user to retrieve",
                    required = true,
                    example = "example-username123"
            )
            @PathVariable String username
    ){
        ResponseUser returnedUser = userService.findUserByUsername(username);
        return ResponseEntity.ok(returnedUser);
    }

    @Operation(
            summary = "Retrieve a user by email",
            description = "Returns a User entity that matches the provided email address.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the user with the given email."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided email is invalid or malformed."
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
                            description = "Not Found. No user exists with the provided email."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email")
    public ResponseEntity<ResponseUser> getUserByEmail(
            @Parameter(
                    name = "email",
                    description = "email of the user to retrieve",
                    required = true,
                    example = "example-username123@aol.com"
            )
            @RequestParam String email
    ){
        ResponseUser returnedUser = userService.findUserByEmail(email);
        return ResponseEntity.ok(returnedUser);
    }

    @Operation(
            summary = "Update a user by ID",
            description = "Updates user profile fields (username, email, password). Available to all authenticated users. To update role, use PUT /api/users/{id}/role endpoint.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the user with the given ID."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or the request body contains invalid/malformed fields."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to update the user."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to update this user."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No user exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict. The update would violate a uniqueness constraint (e.g., email or username already in use)."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while updating the user."
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseUser> updateUserById(
            @Parameter(
                    name = "id",
                    description = "UUID of the user to update",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest){
        ResponseUser returnedUser = userService.updateUserById(id, updateUserRequest);
        return ResponseEntity.ok(returnedUser);
    }

    @Operation(
            summary = "Update a user's role",
            description = "Updates only the role of the User entity with the specified UUID. ADMIN only.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the user with the given ID."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or the request body contains invalid/malformed fields."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to update the user."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to update this user."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No user exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while updating the user."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<ResponseUser> updateUserRole(
            @Parameter(
                    name = "id",
                    description = "UUID of the user whose role will be updated",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest){
        ResponseUser returnedUser = userService.updateRole(id, updateUserRequest);
        return ResponseEntity.ok(returnedUser);
    }

    @Operation(
            summary = "Delete a user by ID",
            description = "Deletes the User entity that matches the provided UUID.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted the user. No content is returned."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. The provided UUID is invalid or malformed."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Authentication is required to delete the user."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden. You do not have permission to delete this user."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found. No user exists with the provided ID."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred while deleting the user."
                    )
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(
            @Parameter(
                    name = "id",
                    description = "UUID of the user to delete",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID id
    ){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
