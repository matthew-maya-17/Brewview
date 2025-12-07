package com.dto;

import com.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "User Response DTO")
public class ResponseUser {

    @Schema(description = "User's unique ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    @Schema(description = "Username", example = "example-username")
    private String username;
    @Schema(description = "Email", example = "example-username@aol.com")
    private String email;
    @Schema(description = "User role", example = "ROLE_USER")
    private Role roleName;
    @Schema(
            description = "Timestamp indicating when the user account was created",
            example = "2024-01-15T13:45:30"
    )
    private LocalDateTime createdAt;

    // Note: NO passwordHash - never expose passwords!

    // Constructors
    public ResponseUser() {}

    public ResponseUser(UUID id, String username, String email, Role roleName, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRoleName() {
        return roleName;
    }

    public void setRoleName(Role roleName) {
        this.roleName = roleName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}