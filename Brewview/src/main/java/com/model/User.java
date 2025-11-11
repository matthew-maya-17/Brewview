package com.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Size(message = "Username must be between 6 and 254 characters!", min = 6, max = 254)
    private String username;

    @Column(unique = true, nullable = false)
    @Email
    @Size(message = "Email must be between 6 and 254 characters!", min = 6, max = 254)
    private String email;

    @Column(name = "password_hash", nullable = false)
    @NotBlank
    private String passwordHash;

    @Column(name = "role_name", nullable = false)
    @NotBlank
    private String roleName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() {}

    public User(String username, String email, String passwordHash, String roleName, LocalDateTime createdAt) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public LocalDateTime getTimestamp() {
        return createdAt;
    }
}
