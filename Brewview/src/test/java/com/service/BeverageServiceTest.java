package com.service;

import com.dto.BeverageRequestDTO;
import com.dto.ResponseBeverage;
import com.exception.BadRequestException;
import com.exception.ResourceNotFoundException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeverageServiceTest {

    @Mock
    private BeverageRepository beverageRepository;

    @InjectMocks
    private BeverageService beverageService;

    private BeverageRequestDTO validRequestDTO;
    private UUID testId;
    private Beverage savedBeverage;

    @BeforeEach
    void setUp() {

        testId = UUID.randomUUID();

        validRequestDTO = new BeverageRequestDTO(
                "Sierra Nevada Pale Ale",
                "Pale Ale",
                5,
                "A classic American pale ale with citrus and pine hop flavors",
                "https://example.com/sierra-nevada.jpg"
        );

        savedBeverage = new Beverage(
                testId,
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
        ResponseBeverage result = beverageService.createBeverage(validRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Sierra Nevada Pale Ale", result.getBeverageName());
        assertNotNull(result.getCreatedAt());
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void createBeveragesShouldSaveMultipleBeverages() {
        // Arrange
        BeverageRequestDTO requestDTO1 = new BeverageRequestDTO("Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg");
        BeverageRequestDTO requestDTO2 = new BeverageRequestDTO("Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg");
        List<BeverageRequestDTO> RequestDTOs = Arrays.asList(requestDTO1, requestDTO2);

        Beverage beverage1 = new Beverage(UUID.randomUUID(), "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(UUID.randomUUID(), "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        List<Beverage> beverages = Arrays.asList(beverage1, beverage2);

        when(beverageRepository.saveAll(anyList())).thenReturn(beverages);

        // Act
        List<ResponseBeverage> result = beverageService.createBeverages(RequestDTOs);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(beverageRepository, times(1)).saveAll(anyList());
    }

    // CREATE - Unhappy Paths
    @Test
    void createBeverageShouldThrowExceptionWhenBeverageIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.createBeverage(null));
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void createBeveragesShouldThrowExceptionWhenListIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.createBeverages(null));
        verify(beverageRepository, never()).saveAll(anyList());
    }

    @Test
    void createBeveragesShouldThrowExceptionWhenListIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.createBeverages(List.of()));
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
        List<ResponseBeverage> result = beverageService.getAllBeverages();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sierra Nevada Pale Ale", result.get(0).getBeverageName());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeverageByIdShouldReturnBeverageWhenExists() {
        // Arrange
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));

        // Act
        ResponseBeverage result = beverageService.getBeverageById(testId);

        // Assert
        assertNotNull(result);
        assertEquals("Sierra Nevada Pale Ale", result.getBeverageName());
        assertEquals(testId, result.getId());
        verify(beverageRepository, times(1)).findById(testId);
    }

    @Test
    void getBeverageByIdShouldThrowExceptionWhenNotExists() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> beverageService.getBeverageById(nonExistentId));
        verify(beverageRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void getBeverageByNameShouldReturnBeverageWhenExists() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        Optional<ResponseBeverage> result = beverageService.getBeverageByName("Sierra Nevada Pale Ale");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Sierra Nevada Pale Ale", result.get().getBeverageName());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeverageByNameShouldReturnEmptyWhenNotExists() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        Optional<ResponseBeverage> result = beverageService.getBeverageByName("Non-existent Beer");

        // Assert
        assertFalse(result.isPresent());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByTypeShouldReturnMatchingBeverages() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        List<ResponseBeverage> result = beverageService.getBeveragesByType("Pale Ale");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pale Ale", result.get(0).getType());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByTypeShouldReturnEmptyListWhenNoMatches() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        List<ResponseBeverage> result = beverageService.getBeveragesByType("Stout");

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByAbvRangeShouldReturnMatchingBeverages() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        List<ResponseBeverage> result = beverageService.getBeveragesByAbvRange(4, 6);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getAbv());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByAbvRangeShouldReturnEmptyListWhenNoMatches() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        List<ResponseBeverage> result = beverageService.getBeveragesByAbvRange(10, 15);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByTypeAndAbvRangeShouldReturnMatchingBeverages() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        List<ResponseBeverage> result = beverageService.getBeveragesByTypeAndAbvRange("Pale Ale", 4, 6);

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
        when(beverageRepository.existsById(testId)).thenReturn(true);

        // Act
        boolean result = beverageService.beverageExists(testId);

        // Assert
        assertTrue(result);
        verify(beverageRepository, times(1)).existsById(testId);
    }

    @Test
    void beverageExistsShouldReturnFalseWhenNotExists() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.existsById(nonExistentId)).thenReturn(false);

        // Act
        boolean result = beverageService.beverageExists(nonExistentId);

        // Assert
        assertFalse(result);
        verify(beverageRepository, times(1)).existsById(nonExistentId);
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
        assertThrows(BadRequestException.class, () -> beverageService.getBeverageById(null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void getBeverageByNameShouldThrowExceptionWhenNameIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.getBeverageByName(null));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeverageByNameShouldThrowExceptionWhenNameIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.getBeverageByName("   "));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByTypeShouldThrowExceptionWhenTypeIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.getBeveragesByType(null));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByTypeShouldThrowExceptionWhenTypeIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.getBeveragesByType("   "));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByAbvRangeShouldThrowExceptionWhenMinAbvIsNegative() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.getBeveragesByAbvRange(-1, 10));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByAbvRangeShouldThrowExceptionWhenMaxAbvLessThanMinAbv() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.getBeveragesByAbvRange(10, 5));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void beverageExistsShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.beverageExists(null));
        verify(beverageRepository, never()).existsById(any(UUID.class));
    }

    @Test
    void beverageNameExistsShouldThrowExceptionWhenNameIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.beverageNameExists(null));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void beverageNameExistsShouldThrowExceptionWhenNameIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.beverageNameExists("   "));
        verify(beverageRepository, never()).findAll();
    }

    // ========== UPDATE OPERATIONS ==========

    // UPDATE - Happy Paths
    @Test
    void updateBeverageShouldUpdateAllFieldsSuccessfully() {
        // Arrange
        BeverageRequestDTO requestDTO = new BeverageRequestDTO("Updated Beer Name", "Updated Type", 8, "This is an updated description for the beverage", "https://example.com/updated.jpg");
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        ResponseBeverage result = beverageService.updateBeverage(testId, requestDTO);

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageNameShouldUpdateNameSuccessfully() {
        // Arrange
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        ResponseBeverage result = beverageService.updateBeverageName(testId, "New Beer Name");

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageTypeShouldUpdateTypeSuccessfully() {
        // Arrange
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        ResponseBeverage result = beverageService.updateBeverageType(testId, "New Type");

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageAbvShouldUpdateAbvSuccessfully() {
        // Arrange
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        ResponseBeverage result = beverageService.updateBeverageAbv(testId, 10);

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageDescriptionShouldUpdateDescriptionSuccessfully() {
        // Arrange
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        ResponseBeverage result = beverageService.updateBeverageDescription(testId, "This is a brand new updated description");

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageImageUrlShouldUpdateImageUrlSuccessfully() {
        // Arrange
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        ResponseBeverage result = beverageService.updateBeverageImageUrl(testId, "https://example.com/new-image.jpg");

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    // UPDATE - Unhappy Paths
    @Test
    void updateBeverageShouldThrowExceptionWhenIdIsNull() {
        // Arrange
        BeverageRequestDTO requestDTO = new BeverageRequestDTO("Updated Beer", "IPA", 6, "Updated description for the beer", "https://example.com/updated.jpg");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverage(null, requestDTO));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageShouldThrowExceptionWhenUpdatedBeverageIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverage(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageShouldThrowExceptionWhenBeverageNotFound() {
        // Arrange
        BeverageRequestDTO requestDTO = new BeverageRequestDTO("Updated Beer", "IPA", 6, "Updated description for the beer", "https://example.com/updated.jpg");
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> beverageService.updateBeverage(nonExistentId, requestDTO));
        verify(beverageRepository, times(1)).findById(nonExistentId);
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageName(null, "New Name"));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenNameIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageName(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenNameIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageName(testId, "   "));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenBeverageNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> beverageService.updateBeverageName(nonExistentId, "New Name"));
        verify(beverageRepository, times(1)).findById(nonExistentId);
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void updateBeverageTypeShouldThrowExceptionWhenTypeIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageType(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageTypeShouldThrowExceptionWhenTypeIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageType(testId, "   "));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageAbvShouldThrowExceptionWhenAbvIsNegative() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageAbv(testId, -1));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageDescriptionShouldThrowExceptionWhenDescriptionIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageDescription(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageDescriptionShouldThrowExceptionWhenDescriptionIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageDescription(testId, "   "));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageImageUrlShouldThrowExceptionWhenImageUrlIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageImageUrl(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageImageUrlShouldThrowExceptionWhenImageUrlIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverageImageUrl(testId, "   "));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    // ========== DELETE OPERATIONS ==========

    // DELETE - Happy Paths
    @Test
    void deleteBeverageShouldDeleteSuccessfully() {
        // Arrange
        when(beverageRepository.existsById(testId)).thenReturn(true);
        doNothing().when(beverageRepository).deleteById(testId);

        // Act
        beverageService.deleteBeverage(testId);

        // Assert
        verify(beverageRepository, times(1)).existsById(testId);
        verify(beverageRepository, times(1)).deleteById(testId);
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
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        List<UUID> ids = Arrays.asList(id1, id2, id3);
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
        assertThrows(BadRequestException.class, () -> beverageService.deleteBeverage(null));
        verify(beverageRepository, never()).existsById(any(UUID.class));
        verify(beverageRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void deleteBeverageShouldThrowExceptionWhenBeverageNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> beverageService.deleteBeverage(nonExistentId));
        verify(beverageRepository, times(1)).existsById(nonExistentId);
        verify(beverageRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void deleteBeveragesByIdsShouldThrowExceptionWhenIdsIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.deleteBeveragesByIds(null));
        verify(beverageRepository, never()).deleteAllById(anyList());
    }

    @Test
    void deleteBeveragesByIdsShouldThrowExceptionWhenIdsIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> beverageService.deleteBeveragesByIds(List.of()));
        verify(beverageRepository, never()).deleteAllById(anyList());
    }
}