package com.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for creating a new user")
public class CreateUserRequest {

    @Schema(description = "Unique username for the user", example = "unique-username-637")
    @NotBlank(message = "Username is required")
    @Size(min = 6, max = 254, message = "Username must be between 6 and 254 characters")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9._]{5,253}$",
            message = "Username must start with a letter and can contain letters, numbers, dots, or underscores"
    )
    private String username;


    @Schema(description = "User's email address", example = "example@hotmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(min = 6, max = 254, message = "Email must be between 6 and 254 characters")
    private String email;

    @Schema(description = "Plaintext password, will be hashed", example = "strongPassword123")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."
    )
    private String password;  // Plain password, not hash

    // Constructors
    public CreateUserRequest() {}

    public CreateUserRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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
}