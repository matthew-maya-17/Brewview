package com.dto;

import java.time.LocalDateTime;

public class ResponseUser {

    private String id;
    private String username;
    private String email;
    private String roleName;
    private LocalDateTime createdAt;

    // Note: NO passwordHash - never expose passwords!

    // Constructors
    public ResponseUser() {}

    public ResponseUser(String id, String username, String email, String roleName, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleName = roleName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}