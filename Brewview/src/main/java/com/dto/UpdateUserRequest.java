package com.dto;


import com.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Size(min = 6, max = 254, message = "Username must be between 6 and 254 characters")
    private String username;

    @Email(message = "Email must be valid")
    @Size(min = 6, max = 254, message = "Email must be between 6 and 254 characters")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

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
