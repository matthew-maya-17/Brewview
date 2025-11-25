package com.dto;

import com.model.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResponseUser {

    private UUID id;
    private String username;
    private String email;
    private Role roleName;
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