package com.service;

import com.model.Beverage;
import com.repository.BeverageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeverageServiceTest {

    @Mock
    private BeverageRepository beverageRepository;

    @InjectMocks
    private BeverageService beverageService;

    private Beverage validBeverage;
    private Beverage savedBeverage;

    @BeforeEach
    void setUp() {
        validBeverage = new Beverage(
                null,
                "Sierra Nevada Pale Ale",
                "Pale Ale",
                5,
                "A classic American pale ale with citrus and pine hop flavors",
                "https://example.com/sierra-nevada.jpg",
                null
        );

        savedBeverage = new Beverage(
                "test-id-123",
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
    void createBeverageShouldSaveBeverageSuccessfully() {
        // Arrange
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        Beverage result = beverageService.createBeverage(validBeverage);

        // Assert
        assertNotNull(result);
        assertEquals("test-id-123", result.getId());
        assertEquals("Sierra Nevada Pale Ale", result.getBeverageName());
        assertNotNull(result.getCreatedAt());
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void createBeverageShouldSetCreatedAtTimestamp() {
        // Arrange
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        beverageService.createBeverage(validBeverage);

        // Assert
        assertNotNull(validBeverage.getCreatedAt());
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void createBeveragesShouldSaveMultipleBeverages() {
        // Arrange
        Beverage beverage1 = new Beverage(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", null);
        Beverage beverage2 = new Beverage(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", null);
        List<Beverage> beverages = Arrays.asList(beverage1, beverage2);

        when(beverageRepository.saveAll(anyList())).thenReturn(beverages);

        // Act
        List<Beverage> result = beverageService.createBeverages(beverages);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(beverage1.getCreatedAt());
        assertNotNull(beverage2.getCreatedAt());
        verify(beverageRepository, times(1)).saveAll(anyList());
    }

    // CREATE - Unhappy Paths
    @Test
    void createBeverageShouldThrowExceptionWhenBeverageIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.createBeverage(null));
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void createBeveragesShouldThrowExceptionWhenListIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.createBeverages(null));
        verify(beverageRepository, never()).saveAll(anyList());
    }

    @Test
    void createBeveragesShouldThrowExceptionWhenListIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.createBeverages(List.of()));
        verify(beverageRepository, never()).saveAll(anyList());
    }

    // ========== READ OPERATIONS ==========

    // READ - Happy Paths
    @Test
    void getAllBeveragesShouldReturnAllBeverages() {
        // Arrange
        List<Beverage> beverages = Arrays.asList(savedBeverage);
        when(beverageRepository.findAll()).thenReturn(beverages);

        // Act
        List<Beverage> result = beverageService.getAllBeverages();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeverageByIdShouldReturnBeverageWhenExists() {
        // Arrange
        when(beverageRepository.findById("test-id-123")).thenReturn(Optional.of(savedBeverage));

        // Act
        Optional<Beverage> result = beverageService.getBeverageById("test-id-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Sierra Nevada Pale Ale", result.get().getBeverageName());
        verify(beverageRepository, times(1)).findById("test-id-123");
    }

    @Test
    void getBeverageByIdShouldReturnEmptyWhenNotExists() {
        // Arrange
        when(beverageRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act
        Optional<Beverage> result = beverageService.getBeverageById("non-existent");

        // Assert
        assertFalse(result.isPresent());
        verify(beverageRepository, times(1)).findById("non-existent");
    }

    @Test
    void getBeverageByNameShouldReturnBeverageWhenExists() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        Optional<Beverage> result = beverageService.getBeverageByName("Sierra Nevada Pale Ale");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Sierra Nevada Pale Ale", result.get().getBeverageName());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByTypeShouldReturnMatchingBeverages() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        List<Beverage> result = beverageService.getBeveragesByType("Pale Ale");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pale Ale", result.get(0).getType());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByAbvRangeShouldReturnMatchingBeverages() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        List<Beverage> result = beverageService.getBeveragesByAbvRange(4, 6);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getAbv());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByTypeAndAbvRangeShouldReturnMatchingBeverages() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        List<Beverage> result = beverageService.getBeveragesByTypeAndAbvRange("Pale Ale", 4, 6);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pale Ale", result.get(0).getType());
        assertEquals(5, result.get(0).getAbv());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void beverageExistsShouldReturnTrueWhenExists() {
        // Arrange
        when(beverageRepository.existsById("test-id-123")).thenReturn(true);

        // Act
        boolean result = beverageService.beverageExists("test-id-123");

        // Assert
        assertTrue(result);
        verify(beverageRepository, times(1)).existsById("test-id-123");
    }

    @Test
    void beverageExistsShouldReturnFalseWhenNotExists() {
        // Arrange
        when(beverageRepository.existsById("non-existent")).thenReturn(false);

        // Act
        boolean result = beverageService.beverageExists("non-existent");

        // Assert
        assertFalse(result);
        verify(beverageRepository, times(1)).existsById("non-existent");
    }

    @Test
    void beverageNameExistsShouldReturnTrueWhenExists() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        boolean result = beverageService.beverageNameExists("Sierra Nevada Pale Ale");

        // Assert
        assertTrue(result);
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void beverageNameExistsShouldReturnFalseWhenNotExists() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        boolean result = beverageService.beverageNameExists("Non-existent Beer");

        // Assert
        assertFalse(result);
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getTotalBeverageCountShouldReturnCorrectCount() {
        // Arrange
        when(beverageRepository.count()).thenReturn(5L);

        // Act
        long result = beverageService.getTotalBeverageCount();

        // Assert
        assertEquals(5L, result);
        verify(beverageRepository, times(1)).count();
    }

    // READ - Unhappy Paths
    @Test
    void getBeverageByIdShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.getBeverageById(null));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void getBeverageByIdShouldThrowExceptionWhenIdIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.getBeverageById("   "));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void getBeverageByNameShouldThrowExceptionWhenNameIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.getBeverageByName(null));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeverageByNameShouldThrowExceptionWhenNameIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.getBeverageByName("   "));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByTypeShouldThrowExceptionWhenTypeIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.getBeveragesByType(null));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByTypeShouldThrowExceptionWhenTypeIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.getBeveragesByType("   "));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByAbvRangeShouldThrowExceptionWhenMinAbvIsNegative() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.getBeveragesByAbvRange(-1, 10));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByAbvRangeShouldThrowExceptionWhenMaxAbvLessThanMinAbv() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.getBeveragesByAbvRange(10, 5));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void beverageExistsShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.beverageExists(null));
        verify(beverageRepository, never()).existsById(anyString());
    }

    @Test
    void beverageExistsShouldThrowExceptionWhenIdIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.beverageExists("   "));
        verify(beverageRepository, never()).existsById(anyString());
    }

    // ========== UPDATE OPERATIONS ==========

    // UPDATE - Happy Paths
    @Test
    void updateBeverageShouldUpdateAllFieldsSuccessfully() {
        // Arrange
        Beverage updatedBeverage = new Beverage(null, "Updated Beer Name", "Updated Type", 8, "This is an updated description for the beverage", "https://example.com/updated.jpg", null);
        when(beverageRepository.findById("test-id-123")).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        Beverage result = beverageService.updateBeverage("test-id-123", updatedBeverage);

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById("test-id-123");
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageNameShouldUpdateNameSuccessfully() {
        // Arrange
        when(beverageRepository.findById("test-id-123")).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        Beverage result = beverageService.updateBeverageName("test-id-123", "New Beer Name");

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById("test-id-123");
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageTypeShouldUpdateTypeSuccessfully() {
        // Arrange
        when(beverageRepository.findById("test-id-123")).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        Beverage result = beverageService.updateBeverageType("test-id-123", "New Type");

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById("test-id-123");
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageAbvShouldUpdateAbvSuccessfully() {
        // Arrange
        when(beverageRepository.findById("test-id-123")).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        Beverage result = beverageService.updateBeverageAbv("test-id-123", 10);

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById("test-id-123");
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageDescriptionShouldUpdateDescriptionSuccessfully() {
        // Arrange
        when(beverageRepository.findById("test-id-123")).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        Beverage result = beverageService.updateBeverageDescription("test-id-123", "This is a brand new updated description");

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById("test-id-123");
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageImageUrlShouldUpdateImageUrlSuccessfully() {
        // Arrange
        when(beverageRepository.findById("test-id-123")).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        Beverage result = beverageService.updateBeverageImageUrl("test-id-123", "https://example.com/new-image.jpg");

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById("test-id-123");
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    // UPDATE - Unhappy Paths
    @Test
    void updateBeverageShouldThrowExceptionWhenIdIsNull() {
        // Arrange
        Beverage updatedBeverage = new Beverage(null, "Updated Beer", "IPA", 6, "Updated description for the beer", "https://example.com/updated.jpg", null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverage(null, updatedBeverage));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void updateBeverageShouldThrowExceptionWhenIdIsEmpty() {
        // Arrange
        Beverage updatedBeverage = new Beverage(null, "Updated Beer", "IPA", 6, "Updated description for the beer", "https://example.com/updated.jpg", null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverage("   ", updatedBeverage));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void updateBeverageShouldThrowExceptionWhenUpdatedBeverageIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverage("test-id-123", null));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void updateBeverageShouldThrowExceptionWhenBeverageNotFound() {
        // Arrange
        Beverage updatedBeverage = new Beverage(null, "Updated Beer", "IPA", 6, "Updated description for the beer", "https://example.com/updated.jpg", null);
        when(beverageRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverage("non-existent", updatedBeverage));
        verify(beverageRepository, times(1)).findById("non-existent");
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverageName(null, "New Name"));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenNameIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverageName("test-id-123", null));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenNameIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverageName("test-id-123", "   "));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenBeverageNotFound() {
        // Arrange
        when(beverageRepository.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverageName("non-existent", "New Name"));
        verify(beverageRepository, times(1)).findById("non-existent");
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void updateBeverageTypeShouldThrowExceptionWhenTypeIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverageType("test-id-123", null));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void updateBeverageAbvShouldThrowExceptionWhenAbvIsNegative() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverageAbv("test-id-123", -1));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void updateBeverageDescriptionShouldThrowExceptionWhenDescriptionIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverageDescription("test-id-123", null));
        verify(beverageRepository, never()).findById(anyString());
    }

    @Test
    void updateBeverageImageUrlShouldThrowExceptionWhenImageUrlIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverageImageUrl("test-id-123", null));
        verify(beverageRepository, never()).findById(anyString());
    }

    // ========== DELETE OPERATIONS ==========

    // DELETE - Happy Paths
    @Test
    void deleteBeverageShouldDeleteSuccessfully() {
        // Arrange
        when(beverageRepository.existsById("test-id-123")).thenReturn(true);
        doNothing().when(beverageRepository).deleteById("test-id-123");

        // Act
        beverageService.deleteBeverage("test-id-123");

        // Assert
        verify(beverageRepository, times(1)).existsById("test-id-123");
        verify(beverageRepository, times(1)).deleteById("test-id-123");
    }

    @Test
    void deleteAllBeveragesShouldDeleteAllSuccessfully() {
        // Arrange
        doNothing().when(beverageRepository).deleteAll();

        // Act
        beverageService.deleteAllBeverages();

        // Assert
        verify(beverageRepository, times(1)).deleteAll();
    }

    @Test
    void deleteBeveragesByIdsShouldDeleteMultipleBeverages() {
        // Arrange
        List<String> ids = Arrays.asList("id1", "id2", "id3");
        doNothing().when(beverageRepository).deleteAllById(ids);

        // Act
        beverageService.deleteBeveragesByIds(ids);

        // Assert
        verify(beverageRepository, times(1)).deleteAllById(ids);
    }

    // DELETE - Unhappy Paths
    @Test
    void deleteBeverageShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.deleteBeverage(null));
        verify(beverageRepository, never()).existsById(anyString());
        verify(beverageRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteBeverageShouldThrowExceptionWhenIdIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.deleteBeverage("   "));
        verify(beverageRepository, never()).existsById(anyString());
        verify(beverageRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteBeverageShouldThrowExceptionWhenBeverageNotFound() {
        // Arrange
        when(beverageRepository.existsById("non-existent")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.deleteBeverage("non-existent"));
        verify(beverageRepository, times(1)).existsById("non-existent");
        verify(beverageRepository, never()).deleteById(anyString());
    }

    @Test
    void deleteBeveragesByIdsShouldThrowExceptionWhenIdsIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.deleteBeveragesByIds(null));
        verify(beverageRepository, never()).deleteAllById(anyList());
    }

    @Test
    void deleteBeveragesByIdsShouldThrowExceptionWhenIdsIsEmpty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> beverageService.deleteBeveragesByIds(List.of()));
        verify(beverageRepository, never()).deleteAllById(anyList());
    }

}