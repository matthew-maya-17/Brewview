package com.service;

import com.dto.CreateUserRequest;
import com.dto.ResponseUser;
import com.dto.UpdateUserRequest;
import com.exception.ResourceNotFoundException;
import com.model.Role;
import com.model.User;
import com.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private  UserService userService;

    // ========== CREATE - Happy Paths ==========

    @Test
    void addUserShouldReturnResponseUserDTO(){
        //Arrange
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "validUserName",
                "validUserName@aol.com",
                "password123"
        );

        UUID expectedUserId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDateTime expectedTimestamp = LocalDateTime.now();
        String hashedPassword = "$2a$10$hashedPasswordString";

        User savedUser = new User(
                "validUserName",
                "validUserName@aol.com",
                hashedPassword,
                Role.ROLE_USER,
                expectedTimestamp
        );
        savedUser.setId(expectedUserId);

        ResponseUser expectedResponseUser = new ResponseUser(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                "validUserName",
                "validUserName@aol.com",
                Role.ROLE_USER,
                expectedTimestamp
        );


        when(userRepository.findUserByUsername("validUserName")).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail("validUserName@aol.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //Act
        ResponseUser actualResponseUser =  userService.addUser(createUserRequest);

        //Assert
        assertNotNull(actualResponseUser);
        assertEquals(expectedResponseUser.getId(), actualResponseUser.getId());
        assertEquals(expectedResponseUser.getUsername(), actualResponseUser.getUsername());
        assertEquals(expectedResponseUser.getEmail(), actualResponseUser.getEmail());
        assertEquals(expectedResponseUser.getRoleName(), actualResponseUser.getRoleName());
        assertEquals(expectedResponseUser.getCreatedAt(), actualResponseUser.getCreatedAt());
    }

    // ========== CREATE - Unhappy Paths ==========

    @Test
    void addUserShouldThrowExceptionWhenUsernameAlreadyExists(){
        //Arrange
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "existingUserName",
                "newemail@aol.com",
                "password123"
        );

        User existingUser = new User(
                "existingUserName",
                "existing@aol.com",
                "hashedPassword",
                Role.ROLE_USER,
                LocalDateTime.now()
        );
        existingUser.setId(UUID.randomUUID());

        when(userRepository.findUserByUsername("existingUserName")).thenReturn(Optional.of(existingUser));

        //Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(createUserRequest)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).findUserByUsername("existingUserName");
        verify(userRepository, never()).findUserByEmail(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addUserShouldThrowExceptionWhenEmailAlreadyExists(){
        //Arrange
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "newUserName",
                "existing@aol.com",
                "password123"
        );

        User existingUser = new User(
                "differentUserName",
                "existing@aol.com",
                "hashedPassword",
                Role.ROLE_USER,
                LocalDateTime.now()
        );
        existingUser.setId(UUID.randomUUID());

        when(userRepository.findUserByUsername("newUserName")).thenReturn(Optional.empty());
        when(userRepository.findUserByEmail("existing@aol.com")).thenReturn(Optional.of(existingUser));

        //Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(createUserRequest)
        );

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).findUserByUsername("newUserName");
        verify(userRepository).findUserByEmail("existing@aol.com");
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== READ - Happy Paths ==========

    @Test
    void findAllUsersShouldReturnListOfResponseUsers(){
        //Arrange
        UUID expectedUser1Id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID expectedUser2Id = UUID.fromString("66d4e198-db85-4c08-97d0-1be99040ad67");
        LocalDateTime expectedTimestamp = LocalDateTime.now();
        String hashedPassword = "$2a$10$hashedPasswordString";

        User user1 = new User(
                "validUserName1",
                "validUserName1@aol.com",
                hashedPassword,
                Role.ROLE_USER,
                expectedTimestamp
        );
        user1.setId(expectedUser1Id);

        User user2 = new User(
                "validUserName2",
                "validUserName2@aol.com",
                hashedPassword,
                Role.ROLE_USER,
                expectedTimestamp
        );
        user2.setId(expectedUser2Id);

        ResponseUser expectedResponseUser1 = new ResponseUser(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                "validUserName1",
                "validUserName1@aol.com",
                Role.ROLE_USER,
                expectedTimestamp
        );

        ResponseUser expectedResponseUser2 = new ResponseUser(
                UUID.fromString("66d4e198-db85-4c08-97d0-1be99040ad67"),
                "validUserName2",
                "validUserName2@aol.com",
                Role.ROLE_USER,
                expectedTimestamp
        );

        List<User> expectedUserList = new ArrayList<>(Arrays.asList(user1, user2));

        // Act
        when(userRepository.findAll()).thenReturn(expectedUserList);
        List<ResponseUser> actualList = userService.findAllUsers();

        //Assert
        assertTrue(!actualList.isEmpty());
        assertEquals(expectedUserList.size(), actualList.size());

        // Compare first user
        ResponseUser actualUser1 = actualList.get(0);
        assertEquals(expectedResponseUser1.getId(), actualUser1.getId());
        assertEquals(expectedResponseUser1.getUsername(), actualUser1.getUsername());
        assertEquals(expectedResponseUser1.getEmail(), actualUser1.getEmail());
        assertEquals(expectedResponseUser1.getRoleName(), actualUser1.getRoleName());
        assertEquals(expectedResponseUser1.getCreatedAt(), actualUser1.getCreatedAt());

        // Compare second user
        ResponseUser actualUser2 = actualList.get(1);
        assertEquals(expectedResponseUser2.getId(), actualUser2.getId());
        assertEquals(expectedResponseUser2.getUsername(), actualUser2.getUsername());
        assertEquals(expectedResponseUser2.getEmail(), actualUser2.getEmail());
        assertEquals(expectedResponseUser2.getRoleName(), actualUser2.getRoleName());
        assertEquals(expectedResponseUser2.getCreatedAt(), actualUser2.getCreatedAt());
    }

    @Test
    void findUserByIdShouldReturnResponseUser(){
        //Arrange
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDateTime expectedTimestamp = LocalDateTime.now();
        String hashedPassword = "$2a$10$hashedPasswordString";

        User user1 = new User(
                "validUserName1",
                "validUserName1@aol.com",
                hashedPassword,
                Role.ROLE_USER,
                expectedTimestamp
        );
        user1.setId(userId);

        ResponseUser expectedResponseUser = new ResponseUser(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                "validUserName1",
                "validUserName1@aol.com",
                Role.ROLE_USER,
                expectedTimestamp
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        //Act
        ResponseUser actualResponseUser = userService.findUserById(userId);

        //Assert
        assertEquals(expectedResponseUser.getId(), actualResponseUser.getId());
        assertEquals(expectedResponseUser.getUsername(), actualResponseUser.getUsername());
        assertEquals(expectedResponseUser.getEmail(), actualResponseUser.getEmail());
        assertEquals(expectedResponseUser.getRoleName(), actualResponseUser.getRoleName());
        assertEquals(expectedResponseUser.getCreatedAt(), actualResponseUser.getCreatedAt());
    }

    @Test
    void findUserByUsernameShouldReturnResponseUser(){
        //Arrange
        String username = "validUserName1";
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDateTime expectedTimestamp = LocalDateTime.now();
        String hashedPassword = "$2a$10$hashedPasswordString";

        User user = new User(
                "validUserName1",
                "validUserName1@aol.com",
                hashedPassword,
                Role.ROLE_USER,
                expectedTimestamp
        );
        user.setId(userId);

        ResponseUser expectedResponseUser = new ResponseUser(
                userId,
                "validUserName1",
                "validUserName1@aol.com",
                Role.ROLE_USER,
                expectedTimestamp
        );

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        //Act
        ResponseUser actualResponseUser = userService.findUserByUsername(username);

        //Assert
        assertEquals(expectedResponseUser.getId(), actualResponseUser.getId());
        assertEquals(expectedResponseUser.getUsername(), actualResponseUser.getUsername());
        assertEquals(expectedResponseUser.getEmail(), actualResponseUser.getEmail());
        assertEquals(expectedResponseUser.getRoleName(), actualResponseUser.getRoleName());
        assertEquals(expectedResponseUser.getCreatedAt(), actualResponseUser.getCreatedAt());
    }

    @Test
    void findUserByEmailShouldReturnResponseUser(){
        //Arrange
        String email = "validUserName1@aol.com";
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDateTime expectedTimestamp = LocalDateTime.now();
        String hashedPassword = "$2a$10$hashedPasswordString";

        User user = new User(
                "validUserName1",
                "validUserName1@aol.com",
                hashedPassword,
                Role.ROLE_USER,
                expectedTimestamp
        );
        user.setId(userId);

        ResponseUser expectedResponseUser = new ResponseUser(
                userId,
                "validUserName1",
                "validUserName1@aol.com",
                Role.ROLE_USER,
                expectedTimestamp
        );

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        //Act
        ResponseUser actualResponseUser = userService.findUserByEmail(email);

        //Assert
        assertEquals(expectedResponseUser.getId(), actualResponseUser.getId());
        assertEquals(expectedResponseUser.getUsername(), actualResponseUser.getUsername());
        assertEquals(expectedResponseUser.getEmail(), actualResponseUser.getEmail());
        assertEquals(expectedResponseUser.getRoleName(), actualResponseUser.getRoleName());
        assertEquals(expectedResponseUser.getCreatedAt(), actualResponseUser.getCreatedAt());
    }

    // ========== READ - Unhappy Paths ==========

    @Test
    void findUserByIdShouldThrowExceptionWhenUserDoesNotExist(){
        //Arrange
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUserById(userId)
        );

        assertEquals("User with ID: " + userId + " does not exist.", exception.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void findUserByUsernameShouldThrowExceptionWhenUserDoesNotExist(){
        //Arrange
        String username = "nonexistentUser";

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        //Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUserByUsername(username)
        );

        assertEquals("User with username: " + username + " does not exist.", exception.getMessage());
        verify(userRepository).findUserByUsername(username);
    }

    @Test
    void findUserByEmailShouldThrowExceptionWhenUserDoesNotExist(){
        //Arrange
        String email = "nonexistent@aol.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        //Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUserByEmail(email)
        );

        assertEquals("User with email: " + email + " does not exist.", exception.getMessage());
        verify(userRepository).findUserByEmail(email);
    }

    // ========== UPDATE - Happy Paths ==========

    @Test
    void updateUserByIdShouldReturnUpdatedResponseUser(){
        //Arrange
        String email = "validUserName@aol.com";
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDateTime expectedTimestamp = LocalDateTime.now();
        String hashedPassword = "$2a$10$hashedPasswordString";

        User user = new User(
                "validUserName",
                "validUserName@aol.com",
                hashedPassword,
                Role.ROLE_USER,
                expectedTimestamp
        );
        user.setId(userId);

        User expectedUpdatedUser = new User(
                "updatedUserName",
                "updatedUserName@aol.com",
                hashedPassword,
                Role.ROLE_USER,
                expectedTimestamp
        );
        expectedUpdatedUser.setId(userId);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "updatedUserName",
                "updatedUserName@aol.com",
                "updatedPassword",
                Role.ROLE_USER
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(expectedUpdatedUser);

        //Act
        ResponseUser actualResponseUser = userService.updateUserById(userId, updateUserRequest);

        //Assert
        assertEquals(expectedUpdatedUser.getId(), actualResponseUser.getId());
        assertEquals(expectedUpdatedUser.getUsername(), actualResponseUser.getUsername());
        assertEquals(expectedUpdatedUser.getEmail(), actualResponseUser.getEmail());
        assertEquals(expectedUpdatedUser.getRoleName(), actualResponseUser.getRoleName());
    }



    @Test
    void updateRoleReturnsUpdatedResponseUser(){
        //Arrange
        String email = "validUserName@aol.com";
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDateTime expectedTimestamp = LocalDateTime.now();
        String hashedPassword = "$2a$10$hashedPasswordString";

        User user = new User(
                "validUserName",
                "validUserName@aol.com",
                hashedPassword,
                Role.ROLE_USER,
                expectedTimestamp
        );
        user.setId(userId);

        User expectedUpdatedUser = new User(
                "validUserName",
                "validUserName@aol.com",
                hashedPassword,
                Role.ROLE_ADMIN,
                expectedTimestamp
        );
        expectedUpdatedUser.setId(userId);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "updatedUserName",
                "updatedUserName@aol.com",
                "updatedPassword",
                Role.ROLE_ADMIN
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(expectedUpdatedUser);

        //Act
        ResponseUser actualResponseUser = userService.updateRole(userId, updateUserRequest);

        //Assert
        assertEquals(expectedUpdatedUser.getId(), actualResponseUser.getId());
        assertEquals(expectedUpdatedUser.getUsername(), actualResponseUser.getUsername());
        assertEquals(expectedUpdatedUser.getEmail(), actualResponseUser.getEmail());
        assertEquals(expectedUpdatedUser.getRoleName(), actualResponseUser.getRoleName());
    }

    // ========== UPDATE - unhappy Paths ==========

    @Test
    void updateUserByIdShouldThrowExceptionWhenUserDoesNotExist(){
        //Arrange
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                "updatedUserName",
                "updated@aol.com",
                "updatedPassword",
                Role.ROLE_USER
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUserById(userId, updateUserRequest)
        );

        assertEquals("User with ID: " + userId + " does not exist.", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateRoleShouldThrowExceptionWhenUserDoesNotExist() {
        //Arrange
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UpdateUserRequest updateUserRequest = new UpdateUserRequest(
                null,
                null,
                null,
                Role.ROLE_ADMIN
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateRole(userId, updateUserRequest)
        );

        assertEquals("User with ID: " + userId + " does not exist.", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    // ========== DELETE - Happy Paths ==========

    @Test
    void deleteUserByIdShouldDeleteUserWhenUserExists(){
        //Arrange
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        when(userRepository.existsById(userId)).thenReturn(true);
        // Note: deleteById is void, so we don't need to mock a return value

        //Act
        userService.deleteUserById(userId);

        //Assert

        // Verify that existsById was called to check if user exists
        verify(userRepository).existsById(userId);
        // Verify that deleteById was called with the correct ID
        verify(userRepository).deleteById(userId);
    }

    // ========== DELETE - Unhappy Paths ==========

    @Test
    void deleteUserByIdShouldThrowExceptionWhenUserDoesNotExist(){
        //Arrange
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

        when(userRepository.existsById(userId)).thenReturn(false);

        //Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUserById(userId)
        );

        assertEquals("User with ID: " + userId + " does not exist.", exception.getMessage());

        // Verify that existsById was called
        verify(userRepository).existsById(userId);
        // Verify that deleteById was NOT called when user doesn't exist
        verify(userRepository, never()).deleteById(userId);
    }
}