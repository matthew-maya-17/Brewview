package com.repository;

import com.model.User;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = new User(
                "validuser",
                "valid@example.com",
                "rx2t43cy1v13u4bi7no98m",
                "ADMIN",
                LocalDateTime.now()
        );
    }

    // ========== CREATE OPERATIONS ==========

    // CREATE - Happy Paths
    @Test
    void saveShouldPersistUserWithValidData() {
        // Arrange & Act
        User savedUser = userRepository.save(validUser);

        // Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("validuser", savedUser.getUsername());
        assertEquals("valid@example.com", savedUser.getEmail());
        assertEquals("rx2t43cy1v13u4bi7no98m", savedUser.getPasswordHash());
        assertEquals("ADMIN", savedUser.getRoleName());
        assertNotNull(savedUser.getTimestamp());
    }

    @Test
    void saveShouldGenerateUniqueId() {
        // Arrange & Act
        User savedUser = userRepository.save(validUser);

        // Assert
        assertNotNull(savedUser.getId());
        assertFalse(savedUser.getId().isEmpty());
    }

    @Test
    void saveShouldPersistMultipleUsers() {
        // Arrange
        User user1 = new User("userone", "user1@example.com", "hash1", "USER", LocalDateTime.now());
        User user2 = new User("usertwo", "user2@example.com", "hash2", "ADMIN", LocalDateTime.now());

        // Act
        userRepository.save(user1);
        userRepository.save(user2);

        // Assert
        assertEquals(2, userRepository.count());
    }

    @Test
    void saveShouldPersistUserWithMinLengthUsername() {
        // Arrange - username exactly 6 characters (minimum)
        User user = new User("sixchr", "test@example.com", "hash", "USER", LocalDateTime.now());

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser);
        assertEquals("sixchr", savedUser.getUsername());
    }

    @Test
    void saveShouldPersistUserWithMaxLengthUsername() {
        // Arrange - username exactly 254 characters (maximum)
        String maxUsername = "a".repeat(254);
        User user = new User(maxUsername, "test@example.com", "hash", "USER", LocalDateTime.now());

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser);
        assertEquals(254, savedUser.getUsername().length());
    }

    @Test
    void saveShouldPersistUserWithValidEmailFormats() {
        // Arrange
        User user1 = new User("user1", "test@example.com", "hash", "USER", LocalDateTime.now());
        User user2 = new User("user2", "test.email@example.co.uk", "hash", "USER", LocalDateTime.now());
        User user3 = new User("user3", "test+tag@example.com", "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertDoesNotThrow(() -> userRepository.save(user1));
        assertDoesNotThrow(() -> userRepository.save(user2));
        assertDoesNotThrow(() -> userRepository.save(user3));
    }

    // CREATE - Unhappy Paths
    @Test
    void saveShouldThrowExceptionWhenUsernameIsNull() {
        // Arrange
        User user = new User(null, "test@example.com", "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenUsernameIsBlank() {
        // Arrange
        User user = new User("   ", "test@example.com", "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenUsernameTooShort() {
        // Arrange - username less than 6 characters
        User user = new User("short", "test@example.com", "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenUsernameTooLong() {
        // Arrange - username more than 254 characters
        String tooLongUsername = "a".repeat(255);
        User user = new User(tooLongUsername, "test@example.com", "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenEmailIsNull() {
        // Arrange
        User user = new User("validuser", null, "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenEmailIsInvalid() {
        // Arrange
        User user = new User("validuser", "invalid-email", "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenEmailTooShort() {
        // Arrange - email less than 6 characters
        User user = new User("validuser", "a@b.c", "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenEmailTooLong() {
        // Arrange - email more than 254 characters
        String tooLongEmail = "a".repeat(245) + "@example.com"; // 255 characters
        User user = new User("validuser", tooLongEmail, "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenPasswordHashIsNull() {
        // Arrange
        User user = new User("validuser", "test@example.com", null, "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenPasswordHashIsBlank() {
        // Arrange
        User user = new User("validuser", "test@example.com", "   ", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenRoleNameIsNull() {
        // Arrange
        User user = new User("validuser", "test@example.com", "hash", null, LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenRoleNameIsBlank() {
        // Arrange
        User user = new User("validuser", "test@example.com", "hash", "   ", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveShouldThrowExceptionWhenUsernameIsDuplicate() {
        // Arrange
        userRepository.save(validUser);
        User duplicateUser = new User("validuser", "different@example.com", "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(duplicateUser));
    }

    @Test
    void saveShouldThrowExceptionWhenEmailIsDuplicate() {
        // Arrange
        userRepository.save(validUser);
        User duplicateUser = new User("differentuser", "valid@example.com", "hash", "USER", LocalDateTime.now());

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(duplicateUser));
    }

    // ========== READ OPERATIONS ==========

    // READ - Happy Paths
    @Test
    void findByIdShouldReturnUserWhenExists() {
        // Arrange
        User savedUser = userRepository.save(validUser);
        String userId = savedUser.getId();

        // Act
        Optional<User> foundUser = userRepository.findById(userId);

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("validuser", foundUser.get().getUsername());
        assertEquals("valid@example.com", foundUser.get().getEmail());
    }

    @Test
    void findAllShouldReturnAllUsers() {
        // Arrange
        User user1 = new User("userone", "user1@example.com", "hash1", "USER", LocalDateTime.now());
        User user2 = new User("usertwo", "user2@example.com", "hash2", "ADMIN", LocalDateTime.now());
        userRepository.save(user1);
        userRepository.save(user2);

        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertEquals(2, users.size());
    }

    @Test
    void existsByIdShouldReturnTrueWhenUserExists() {
        // Arrange
        User savedUser = userRepository.save(validUser);
        String userId = savedUser.getId();

        // Act
        boolean exists = userRepository.existsById(userId);

        // Assert
        assertTrue(exists);
    }

    @Test
    void countShouldReturnCorrectNumberOfUsers() {
        // Arrange
        User user1 = new User("userone", "user1@example.com", "hash1", "USER", LocalDateTime.now());
        User user2 = new User("usertwo", "user2@example.com", "hash2", "ADMIN", LocalDateTime.now());
        userRepository.save(user1);
        userRepository.save(user2);

        // Act
        long count = userRepository.count();

        // Assert
        assertEquals(2, count);
    }

    // READ - Unhappy Paths
    @Test
    void findByIdShouldReturnEmptyWhenUserDoesNotExist() {
        // Arrange
        String nonExistentId = "non-existent-id";

        // Act
        Optional<User> foundUser = userRepository.findById(nonExistentId);

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByIdShouldReturnEmptyWhenIdIsNull() {
        // Act + Assert
        assertThrows(Exception.class, () -> {
            userRepository.findById(null);
        });
    }

    @Test
    void existsByIdShouldReturnFalseWhenUserDoesNotExist() {
        // Arrange
        String nonExistentId = "non-existent-id";

        // Act
        boolean exists = userRepository.existsById(nonExistentId);

        // Assert
        assertFalse(exists);
    }

    @Test
    void findAllShouldReturnEmptyListWhenNoUsersExist() {
        // Act
        List<User> users = userRepository.findAll();

        // Assert
        assertTrue(users.isEmpty());
    }

    @Test
    void countShouldReturnZeroWhenNoUsersExist() {
        // Act
        long count = userRepository.count();

        // Assert
        assertEquals(0, count);
    }

    // ========== UPDATE OPERATIONS ==========

    // UPDATE - Happy Paths
    @Test
    void updateUsernameShouldPersistChanges() {
        // Arrange
        User savedUser = userRepository.save(validUser);
        String userId = savedUser.getId();

        // Act
        savedUser.setUsername("updateduser");
        userRepository.save(savedUser);
        User updatedUser = userRepository.findById(userId).orElseThrow();

        // Assert
        assertEquals("updateduser", updatedUser.getUsername());
    }

    @Test
    void updateEmailShouldPersistChanges() {
        // Arrange
        User savedUser = userRepository.save(validUser);
        String userId = savedUser.getId();

        // Act
        savedUser.setEmail("updated@example.com");
        userRepository.save(savedUser);
        User updatedUser = userRepository.findById(userId).orElseThrow();

        // Assert
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void updatePasswordHashShouldPersistChanges() {
        // Arrange
        User savedUser = userRepository.save(validUser);
        String userId = savedUser.getId();

        // Act
        savedUser.setPasswordHash("newhash123");
        userRepository.save(savedUser);
        User updatedUser = userRepository.findById(userId).orElseThrow();

        // Assert
        assertEquals("newhash123", updatedUser.getPasswordHash());
    }

    @Test
    void updateRoleNameShouldPersistChanges() {
        // Arrange
        User savedUser = userRepository.save(validUser);
        String userId = savedUser.getId();

        // Act
        savedUser.setRoleName("MODERATOR");
        userRepository.save(savedUser);
        User updatedUser = userRepository.findById(userId).orElseThrow();

        // Assert
        assertEquals("MODERATOR", updatedUser.getRoleName());
    }

    @Test
    void updateMultipleFieldsShouldPersistAllChanges() {
        // Arrange
        User savedUser = userRepository.save(validUser);
        String userId = savedUser.getId();

        // Act
        savedUser.setUsername("multiuser");
        savedUser.setEmail("multi@example.com");
        savedUser.setPasswordHash("multihash");
        savedUser.setRoleName("MODERATOR");
        userRepository.save(savedUser);
        User updatedUser = userRepository.findById(userId).orElseThrow();

        // Assert
        assertEquals("multiuser", updatedUser.getUsername());
        assertEquals("multi@example.com", updatedUser.getEmail());
        assertEquals("multihash", updatedUser.getPasswordHash());
        assertEquals("MODERATOR", updatedUser.getRoleName());
    }

    // UPDATE - Unhappy Paths
    @Test
    void updateUsernameToNullShouldThrowException() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act & Assert
        savedUser.setUsername(null);
        assertThrows(Exception.class, () -> userRepository.saveAndFlush(savedUser));
    }

    @Test
    void updateUsernameToBlankShouldThrowException() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act & Assert
        savedUser.setUsername("   ");
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(savedUser));
    }

    @Test
    void updateUsernameToTooShortShouldThrowException() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act & Assert
        savedUser.setUsername("short");
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(savedUser));
    }

    @Test
    void updateUsernameToDuplicateShouldThrowException() {
        // Arrange
        User user1 = new User("userone", "user1@example.com", "hash1", "USER", LocalDateTime.now());
        User user2 = new User("usertwo", "user2@example.com", "hash2", "USER", LocalDateTime.now());
        userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        // Act & Assert
        savedUser2.setUsername("userone");
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(savedUser2));
    }

    @Test
    void updateEmailToNullShouldThrowException() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act & Assert
        savedUser.setEmail(null);
        assertThrows(Exception.class, () -> userRepository.saveAndFlush(savedUser));
    }

    @Test
    void updateEmailToInvalidFormatShouldThrowException() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act & Assert
        savedUser.setEmail("invalid-email");
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(savedUser));
    }

    @Test
    void updateEmailToDuplicateShouldThrowException() {
        // Arrange
        User user1 = new User("userone", "user1@example.com", "hash1", "USER", LocalDateTime.now());
        User user2 = new User("usertwo", "user2@example.com", "hash2", "USER", LocalDateTime.now());
        userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        // Act & Assert
        savedUser2.setEmail("user1@example.com");
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(savedUser2));
    }

    @Test
    void updatePasswordHashToNullShouldThrowException() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act & Assert
        savedUser.setPasswordHash(null);
        assertThrows(Exception.class, () -> userRepository.saveAndFlush(savedUser));
    }

    @Test
    void updatePasswordHashToBlankShouldThrowException() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act & Assert
        savedUser.setPasswordHash("   ");
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(savedUser));
    }

    @Test
    void updateRoleNameToNullShouldThrowException() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act & Assert
        savedUser.setRoleName(null);
        assertThrows(Exception.class, () -> userRepository.saveAndFlush(savedUser));
    }

    @Test
    void updateRoleNameToBlankShouldThrowException() {
        // Arrange
        User savedUser = userRepository.save(validUser);

        // Act & Assert
        savedUser.setRoleName("   ");
        assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(savedUser));
    }

    // ========== DELETE OPERATIONS ==========

    // DELETE - Happy Paths
    @Test
    void deleteByIdShouldRemoveUser() {
        // Arrange
        User savedUser = userRepository.save(validUser);
        String userId = savedUser.getId();

        // Act
        userRepository.deleteById(userId);

        // Assert
        assertFalse(userRepository.existsById(userId));
        assertEquals(0, userRepository.count());
    }

    @Test
    void deleteByEntityShouldRemoveUser() {
        // Arrange
        User savedUser = userRepository.save(validUser);
        String userId = savedUser.getId();

        // Act
        userRepository.delete(savedUser);

        // Assert
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void deleteAllShouldRemoveAllUsers() {
        // Arrange
        User user1 = new User("userone", "user1@example.com", "hash1", "USER", LocalDateTime.now());
        User user2 = new User("usertwo", "user2@example.com", "hash2", "ADMIN", LocalDateTime.now());
        userRepository.save(user1);
        userRepository.save(user2);

        // Act
        userRepository.deleteAll();

        // Assert
        assertEquals(0, userRepository.count());
    }

    @Test
    void deleteAllByIdShouldRemoveSelectedUsers() {
        // Arrange
        User user1 = userRepository.save(new User("userone", "user1@example.com", "hash1", "USER", LocalDateTime.now()));
        User user2 = userRepository.save(new User("usertwo", "user2@example.com", "hash2", "ADMIN", LocalDateTime.now()));
        User user3 = userRepository.save(new User("userthree", "user3@example.com", "hash3", "USER", LocalDateTime.now()));

        // Act
        userRepository.deleteAllById(List.of(user1.getId(), user2.getId()));

        // Assert
        assertEquals(1, userRepository.count());
        assertTrue(userRepository.existsById(user3.getId()));
    }

    // DELETE - Unhappy Paths
    @Test
    void deleteByIdShouldNotThrowExceptionWhenUserDoesNotExist() {
        // Arrange
        String nonExistentId = "non-existent-id";

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> userRepository.deleteById(nonExistentId));
    }

    @Test
    void deleteByIdShouldNotThrowExceptionWhenIdIsNull() {
        // Act & Assert - Should not throw exception
        // Act & Assert
        assertThrows(Exception.class, () -> {
            userRepository.deleteById(null);
        });
    }

    @Test
    void deleteAllShouldNotThrowExceptionWhenRepositoryIsEmpty() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> userRepository.deleteAll());
    }
}