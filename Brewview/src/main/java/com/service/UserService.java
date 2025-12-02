package com.service;

import com.dto.CreateUserRequest;
import com.dto.ResponseUser;
import com.dto.UpdateUserRequest;
import com.exception.ResourceConflictException;
import com.exception.ResourceNotFoundException;
import com.model.Role;
import com.model.User;
import com.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //CREATE
    public ResponseUser addUser(CreateUserRequest createUserRequest){
        // Check if username already exists
        if (userRepository.findUserByUsername(createUserRequest.getUsername()).isPresent()) {
            throw new ResourceConflictException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findUserByEmail(createUserRequest.getEmail()).isPresent()) {
            throw new ResourceConflictException("Email already exists");
        }

        // Create new user entity
        User newUser = convertToEntity(createUserRequest);
        newUser.setPasswordHash(passwordEncoder.encode(createUserRequest.getPassword()));
        newUser.setRoleName(Role.ROLE_USER); // Default role for new users
        newUser.setCreatedAt(LocalDateTime.now());

        // Save and return
        User savedUser = userRepository.save(newUser);
        return convertToResponseDto(savedUser);
    }

    //READ
    @PreAuthorize("hasRole('ADMIN')")
    public List<ResponseUser> findAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseUser findUserById(UUID id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + " does not exist."));
        return convertToResponseDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseUser findUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username: " + username + " does not exist."));
        return convertToResponseDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseUser findUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " does not exist."));
        return convertToResponseDto(user);
    }

    //UPDATE
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public ResponseUser updateUserById(UUID id, UpdateUserRequest updateRequest){
        // Find existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + id + " does not exist."));

        // Update username (if provided and different)
        if (updateRequest.getUsername() != null &&
                !updateRequest.getUsername().trim().isEmpty() &&
                !updateRequest.getUsername().trim().equals(existingUser.getUsername())) {

            // Validate uniqueness
            if (userRepository.findUserByUsername(updateRequest.getUsername().trim()).isPresent()) {
                throw new ResourceConflictException("Username already exists");
            }
            existingUser.setUsername(updateRequest.getUsername().trim());
        }

        // Update email (if provided and different)
        if (updateRequest.getEmail() != null &&
                !updateRequest.getEmail().trim().isEmpty() &&
                !updateRequest.getEmail().trim().equals(existingUser.getEmail())) {

            // Validate uniqueness
            if (userRepository.findUserByEmail(updateRequest.getEmail().trim()).isPresent()) {
                throw new ResourceConflictException("Email already exists");
            }
            existingUser.setEmail(updateRequest.getEmail().trim());
        }

        // Update password (if provided)
        if (updateRequest.getPassword() != null &&
                !updateRequest.getPassword().trim().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(updateRequest.getPassword().trim()));
        }

        // Save and return Response DTO
        User updatedUser = userRepository.save(existingUser);
        return convertToResponseDto(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseUser updateRole(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " does not exist."));

        user.setRoleName(request.getRoleName()); // e.g., ROLE_ADMIN
        return convertToResponseDto(userRepository.save(user));
    }

    //DELETE
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(UUID id){
        if(!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User with ID: " + id + " does not exist.");
        }
        userRepository.deleteById(id);
    }

    // Helper Methods for Conversion
    private User convertToEntity(CreateUserRequest dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        // Note: passwordHash will be set separately after hashing
        return user;
    }

    private ResponseUser convertToResponseDto(User user) {
        return new ResponseUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoleName(),
                user.getTimestamp() // or user.getCreatedAt() if you add that getter
        );
    }
}
