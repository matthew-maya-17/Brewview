package com.dto;


import com.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for updating a user")
public class UpdateUserRequest {

    @Schema(description = "Updated username", example = "updated-username")
    @Size(min = 6, max = 254, message = "Username must be between 6 and 254 characters")
    private String username;

    @Schema(description = "Updated email address", example = "updated-email@gmail.com")
    @Email(message = "Email must be valid")
    @Size(min = 6, max = 254, message = "Email must be between 6 and 254 characters")
    private String email;

    @Schema(description = "Updated password", example = "updated-password123")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Schema(description = "Updated user role", example = "ROLE_ADMIN")
    private Role roleName;

    // Constructors
    public UpdateUserRequest() {}

    public UpdateUserRequest(String username, String email, String password, Role roleName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roleName = roleName;
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRoleName() {
        return roleName;
    }

    public void setRoleName(Role roleName) {
        this.roleName = roleName;
    }
}
