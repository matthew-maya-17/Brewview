package com.repository;

import com.model.Beverage;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BeverageRepositoryTest {

    @Autowired
    private BeverageRepository beverageRepository;

    private Beverage validBeverage;

    @BeforeEach
    void setUp() {
        validBeverage = new Beverage(
                null,
                "Sierra Nevada Pale Ale",
                "Pale Ale",
                5,
                "A classic American pale ale with citrus and pine hop flavors",
                "https://example.com/sierra-nevada.jpg",
                LocalDateTime.now()
        );
    }

    // ========== CREATE OPERATIONS ==========

    // CREATE - Happy Paths
    @Test
    void saveShouldPersistBeverageWithValidData() {
        // Arrange & Act
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Assert
        assertNotNull(savedBeverage);
        assertNotNull(savedBeverage.getId());
        assertEquals("Sierra Nevada Pale Ale", savedBeverage.getBeverageName());
        assertEquals("Pale Ale", savedBeverage.getType());
        assertEquals(5, savedBeverage.getAbv());
        assertEquals("A classic American pale ale with citrus and pine hop flavors", savedBeverage.getDescription());
        assertEquals("https://example.com/sierra-nevada.jpg", savedBeverage.getImageUrl());
        assertNotNull(savedBeverage.getCreatedAt());
    }

    @Test
    void saveShouldGenerateUniqueId() {
        // Arrange & Act
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Assert
        assertNotNull(savedBeverage.getId());
    }

    @Test
    void saveShouldPersistMultipleBeverages() {
        // Arrange
        Beverage beverage1 = new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());

        // Act
        beverageRepository.save(beverage1);
        beverageRepository.save(beverage2);

        // Assert
        assertEquals(2, beverageRepository.count());
    }

    @Test
    void saveShouldPersistBeverageWithMinLengthName() {
        // Arrange - beverageName exactly 6 characters (minimum)
        Beverage beverage = new Beverage(null, "Beer01", "IPA", 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act
        Beverage savedBeverage = beverageRepository.save(beverage);

        // Assert
        assertNotNull(savedBeverage);
        assertEquals("Beer01", savedBeverage.getBeverageName());
    }

    @Test
    void saveShouldPersistBeverageWithMaxLengthName() {
        // Arrange - beverageName exactly 254 characters (maximum)
        String maxName = "a".repeat(254);
        Beverage beverage = new Beverage(null, maxName, "IPA", 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act
        Beverage savedBeverage = beverageRepository.save(beverage);

        // Assert
        assertNotNull(savedBeverage);
        assertEquals(254, savedBeverage.getBeverageName().length());
    }

    @Test
    void saveShouldPersistBeverageWithMinLengthType() {
        // Arrange - type exactly 3 characters (minimum)
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act
        Beverage savedBeverage = beverageRepository.save(beverage);

        // Assert
        assertNotNull(savedBeverage);
        assertEquals("IPA", savedBeverage.getType());
    }

    @Test
    void saveShouldPersistBeverageWithMinLengthDescription() {
        // Arrange - description exactly 25 characters (minimum)
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, "A wonderful craft beer!!", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act
        Beverage savedBeverage = beverageRepository.save(beverage);

        // Assert
        assertNotNull(savedBeverage);
        assertEquals(25, savedBeverage.getDescription().length());
    }

    @Test
    void saveShouldPersistBeverageWithZeroAbv() {
        // Arrange - ABV of 0 for non-alcoholic beverages
        Beverage beverage = new Beverage(null, "Non-Alcoholic Beer", "Non-Alcoholic", 0, "A great tasting non-alcoholic beverage option", "https://example.com/na-beer.jpg", LocalDateTime.now());

        // Act
        Beverage savedBeverage = beverageRepository.save(beverage);

        // Assert
        assertNotNull(savedBeverage);
        assertEquals(0, savedBeverage.getAbv());
    }

    // CREATE - Unhappy Paths
    @Test
    void saveShouldThrowExceptionWhenBeverageNameIsNull() {
        // Arrange
        Beverage beverage = new Beverage(null, null, "IPA", 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenBeverageNameIsBlank() {
        // Arrange
        Beverage beverage = new Beverage(null, "   ", "IPA", 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenBeverageNameTooShort() {
        // Arrange - beverageName less than 6 characters
        Beverage beverage = new Beverage(null, "Beer", "IPA", 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenBeverageNameTooLong() {
        // Arrange - beverageName more than 254 characters
        String tooLongName = "a".repeat(255);
        Beverage beverage = new Beverage(null, tooLongName, "IPA", 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenTypeIsNull() {
        // Arrange
        Beverage beverage = new Beverage(null, "Test Beer Name", null, 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenTypeIsBlank() {
        // Arrange
        Beverage beverage = new Beverage(null, "Test Beer Name", "   ", 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenTypeTooShort() {
        // Arrange - type less than 3 characters
        Beverage beverage = new Beverage(null, "Test Beer Name", "IP", 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenTypeTooLong() {
        // Arrange - type more than 254 characters
        String tooLongType = "a".repeat(255);
        Beverage beverage = new Beverage(null, "Test Beer Name", tooLongType, 6, "A wonderful craft beer with amazing flavors", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenDescriptionIsNull() {
        // Arrange
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, null, "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenDescriptionIsBlank() {
        // Arrange
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, "   ", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenDescriptionTooShort() {
        // Arrange - description less than 25 characters
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, "Short description", "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenDescriptionTooLong() {
        // Arrange - description more than 254 characters
        String tooLongDescription = "a".repeat(255);
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, tooLongDescription, "https://example.com/beer.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenImgUrlIsNull() {
        // Arrange
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, "A wonderful craft beer with amazing flavors", null, LocalDateTime.now());

        // Act & Assert
        assertThrows(Exception.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenImgUrlIsBlank() {
        // Arrange
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, "A wonderful craft beer with amazing flavors", "   ", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenImgUrlTooShort() {
        // Arrange - img_url less than 6 characters
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, "A wonderful craft beer with amazing flavors", "a.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenImgUrlTooLong() {
        // Arrange - img_url more than 500 characters
        String tooLongUrl = "https://example.com/" + "a".repeat(500);
        Beverage beverage = new Beverage(null, "Test Beer Name", "IPA", 6, "A wonderful craft beer with amazing flavors", tooLongUrl, LocalDateTime.now());

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(beverage));
    }

    @Test
    void saveShouldThrowExceptionWhenBeverageNameIsDuplicate() {
        // Arrange
        beverageRepository.save(validBeverage);
        Beverage duplicateBeverage = new Beverage(null, "Sierra Nevada Pale Ale", "Different Type", 7, "Different description here now", "https://example.com/different.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> beverageRepository.saveAndFlush(duplicateBeverage));
    }

    @Test
    void saveShouldThrowExceptionWhenTypeIsDuplicate() {
        // Arrange
        beverageRepository.save(validBeverage);
        Beverage duplicateBeverage = new Beverage(null, "Different Beer Name", "Pale Ale", 7, "Different description here now", "https://example.com/different.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> beverageRepository.saveAndFlush(duplicateBeverage));
    }

    @Test
    void saveShouldThrowExceptionWhenDescriptionIsDuplicate() {
        // Arrange
        beverageRepository.save(validBeverage);
        Beverage duplicateBeverage = new Beverage(null, "Different Beer Name", "Different Type", 7, "A classic American pale ale with citrus and pine hop flavors", "https://example.com/different.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> beverageRepository.saveAndFlush(duplicateBeverage));
    }

    @Test
    void saveShouldThrowExceptionWhenImgUrlIsDuplicate() {
        // Arrange
        beverageRepository.save(validBeverage);
        Beverage duplicateBeverage = new Beverage(null, "Different Beer Name", "Different Type", 7, "Different description here now", "https://example.com/sierra-nevada.jpg", LocalDateTime.now());

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> beverageRepository.saveAndFlush(duplicateBeverage));
    }

    // ========== READ OPERATIONS ==========

    // READ - Happy Paths
    @Test
    void findByIdShouldReturnBeverageWhenExists() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        Optional<Beverage> foundBeverage = beverageRepository.findById(beverageId);

        // Assert
        assertTrue(foundBeverage.isPresent());
        assertEquals("Sierra Nevada Pale Ale", foundBeverage.get().getBeverageName());
        assertEquals("Pale Ale", foundBeverage.get().getType());
    }

    @Test
    void findAllShouldReturnAllBeverages() {
        // Arrange
        Beverage beverage1 = new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        beverageRepository.save(beverage1);
        beverageRepository.save(beverage2);

        // Act
        List<Beverage> beverages = beverageRepository.findAll();

        // Assert
        assertEquals(2, beverages.size());
    }

    @Test
    void existsByIdShouldReturnTrueWhenBeverageExists() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        boolean exists = beverageRepository.existsById(beverageId);

        // Assert
        assertTrue(exists);
    }

    @Test
    void countShouldReturnCorrectNumberOfBeverages() {
        // Arrange
        Beverage beverage1 = new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        beverageRepository.save(beverage1);
        beverageRepository.save(beverage2);

        // Act
        long count = beverageRepository.count();

        // Assert
        assertEquals(2, count);
    }

    // READ - Unhappy Paths
    @Test
    void findByIdShouldReturnEmptyWhenBeverageDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<Beverage> foundBeverage = beverageRepository.findById(nonExistentId);

        // Assert
        assertFalse(foundBeverage.isPresent());
    }

    @Test
    void findByIdShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            beverageRepository.findById(null);
        });
    }

    @Test
    void existsByIdShouldReturnFalseWhenBeverageDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        boolean exists = beverageRepository.existsById(nonExistentId);

        // Assert
        assertFalse(exists);
    }

    @Test
    void findAllShouldReturnEmptyListWhenNoBeveragesExist() {
        // Act
        List<Beverage> beverages = beverageRepository.findAll();

        // Assert
        assertTrue(beverages.isEmpty());
    }

    @Test
    void countShouldReturnZeroWhenNoBeveragesExist() {
        // Act
        long count = beverageRepository.count();

        // Assert
        assertEquals(0, count);
    }

    // ========== UPDATE OPERATIONS ==========

    // UPDATE - Happy Paths
    @Test
    void updateBeverageNameShouldPersistChanges() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        savedBeverage.setBeverageName("Updated Beer Name");
        beverageRepository.save(savedBeverage);
        Beverage updatedBeverage = beverageRepository.findById(beverageId).orElseThrow();

        // Assert
        assertEquals("Updated Beer Name", updatedBeverage.getBeverageName());
    }

    @Test
    void updateTypeShouldPersistChanges() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        savedBeverage.setType("Updated Type");
        beverageRepository.save(savedBeverage);
        Beverage updatedBeverage = beverageRepository.findById(beverageId).orElseThrow();

        // Assert
        assertEquals("Updated Type", updatedBeverage.getType());
    }

    @Test
    void updateAbvShouldPersistChanges() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        savedBeverage.setAbv(8);
        beverageRepository.save(savedBeverage);
        Beverage updatedBeverage = beverageRepository.findById(beverageId).orElseThrow();

        // Assert
        assertEquals(8, updatedBeverage.getAbv());
    }

    @Test
    void updateDescriptionShouldPersistChanges() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        savedBeverage.setDescription("This is an updated description for the beverage");
        beverageRepository.save(savedBeverage);
        Beverage updatedBeverage = beverageRepository.findById(beverageId).orElseThrow();

        // Assert
        assertEquals("This is an updated description for the beverage", updatedBeverage.getDescription());
    }

    @Test
    void updateImgUrlShouldPersistChanges() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        savedBeverage.setImageUrl("https://example.com/updated-image.jpg");
        beverageRepository.save(savedBeverage);
        Beverage updatedBeverage = beverageRepository.findById(beverageId).orElseThrow();

        // Assert
        assertEquals("https://example.com/updated-image.jpg", updatedBeverage.getImageUrl());
    }

    @Test
    void updateMultipleFieldsShouldPersistAllChanges() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        savedBeverage.setBeverageName("Multi Update Beer");
        savedBeverage.setType("Multi Type");
        savedBeverage.setAbv(10);
        savedBeverage.setDescription("This beverage has been updated in multiple ways");
        savedBeverage.setImageUrl("https://example.com/multi-update.jpg");
        beverageRepository.save(savedBeverage);
        Beverage updatedBeverage = beverageRepository.findById(beverageId).orElseThrow();

        // Assert
        assertEquals("Multi Update Beer", updatedBeverage.getBeverageName());
        assertEquals("Multi Type", updatedBeverage.getType());
        assertEquals(10, updatedBeverage.getAbv());
        assertEquals("This beverage has been updated in multiple ways", updatedBeverage.getDescription());
        assertEquals("https://example.com/multi-update.jpg", updatedBeverage.getImageUrl());
    }

    // UPDATE - Unhappy Paths
    @Test
    void updateBeverageNameToNullShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setBeverageName(null);
        assertThrows(Exception.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateBeverageNameToBlankShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setBeverageName("   ");
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateBeverageNameToTooShortShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setBeverageName("Short");
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateBeverageNameToDuplicateShouldThrowException() {
        // Arrange
        Beverage beverage1 = new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        beverageRepository.save(beverage1);
        Beverage savedBeverage2 = beverageRepository.save(beverage2);

        // Act & Assert
        savedBeverage2.setBeverageName("Guinness Stout");
        assertThrows(DataIntegrityViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage2));
    }

    @Test
    void updateTypeToNullShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setType(null);
        assertThrows(Exception.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateTypeToBlankShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setType("   ");
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateTypeToTooShortShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setType("IP");
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateTypeToDuplicateShouldThrowException() {
        // Arrange
        Beverage beverage1 = new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        beverageRepository.save(beverage1);
        Beverage savedBeverage2 = beverageRepository.save(beverage2);

        // Act & Assert
        savedBeverage2.setType("Stout");
        assertThrows(DataIntegrityViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage2));
    }

    @Test
    void updateDescriptionToNullShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setDescription(null);
        assertThrows(Exception.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateDescriptionToBlankShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setDescription("   ");
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateDescriptionToTooShortShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setDescription("Too short");
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateDescriptionToDuplicateShouldThrowException() {
        // Arrange
        Beverage beverage1 = new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        beverageRepository.save(beverage1);
        Beverage savedBeverage2 = beverageRepository.save(beverage2);

        // Act & Assert
        savedBeverage2.setDescription("Rich and creamy Irish stout with roasted flavors");
        assertThrows(DataIntegrityViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage2));
    }

    @Test
    void updateImgUrlToNullShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setImageUrl(null);
        assertThrows(Exception.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateImgUrlToBlankShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setImageUrl("   ");
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateImgUrlToTooShortShouldThrowException() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);

        // Act & Assert
        savedBeverage.setImageUrl("a.jpg");
        assertThrows(ConstraintViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage));
    }

    @Test
    void updateImgUrlToDuplicateShouldThrowException() {
        // Arrange
        Beverage beverage1 = new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        beverageRepository.save(beverage1);
        Beverage savedBeverage2 = beverageRepository.save(beverage2);

        // Act & Assert
        savedBeverage2.setImageUrl("https://example.com/guinness.jpg");
        assertThrows(DataIntegrityViolationException.class, () -> beverageRepository.saveAndFlush(savedBeverage2));
    }

    // ========== DELETE OPERATIONS ==========

    // DELETE - Happy Paths
    @Test
    void deleteByIdShouldRemoveBeverage() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        beverageRepository.deleteById(beverageId);

        // Assert
        assertFalse(beverageRepository.existsById(beverageId));
        assertEquals(0, beverageRepository.count());
    }

    @Test
    void deleteByEntityShouldRemoveBeverage() {
        // Arrange
        Beverage savedBeverage = beverageRepository.save(validBeverage);
        UUID beverageId = savedBeverage.getId();

        // Act
        beverageRepository.delete(savedBeverage);

        // Assert
        assertFalse(beverageRepository.existsById(beverageId));
    }

    @Test
    void deleteAllShouldRemoveAllBeverages() {
        // Arrange
        Beverage beverage1 = new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        beverageRepository.save(beverage1);
        beverageRepository.save(beverage2);

        // Act
        beverageRepository.deleteAll();

        // Assert
        assertEquals(0, beverageRepository.count());
    }

    @Test
    void deleteAllByIdShouldRemoveSelectedBeverages() {
        // Arrange
        Beverage beverage1 = beverageRepository.save(new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now()));
        Beverage beverage2 = beverageRepository.save(new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now()));
        Beverage beverage3 = beverageRepository.save(new Beverage(null, "Heineken Premium", "Premium Lager", 5, "Crisp and refreshing premium lager from Holland", "https://example.com/heineken.jpg", LocalDateTime.now()));

        // Act
        beverageRepository.deleteAllById(List.of(beverage1.getId(), beverage2.getId()));

        // Assert
        assertEquals(1, beverageRepository.count());
        assertTrue(beverageRepository.existsById(beverage3.getId()));
    }

    // DELETE - Unhappy Paths
    @Test
    void deleteByIdShouldNotThrowExceptionWhenBeverageDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> beverageRepository.deleteById(nonExistentId));
    }

    @Test
    void deleteByIdShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            beverageRepository.deleteById(null);
        });
    }

    @Test
    void deleteAllShouldNotThrowExceptionWhenRepositoryIsEmpty() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> beverageRepository.deleteAll());
    }
}