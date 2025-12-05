package com.service;

import com.dto.BeverageDTO;
import com.exception.ResourceNotFoundException;
import com.model.Beverage;
import com.repository.BeverageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.exception.BadRequestException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeverageServiceTest {

    @Mock
    private BeverageRepository beverageRepository;

    @InjectMocks
    private BeverageService beverageService;

    private BeverageDTO validBeverageDTO;
    private UUID testId;
    private Beverage validBeverage;
    private Beverage savedBeverage;

    @BeforeEach
    void setUp() {

        testId = UUID.randomUUID();

        validBeverageDTO = new BeverageDTO(
                null,
                "Sierra Nevada Pale Ale",
                "Pale Ale",
                5,
                "A classic American pale ale with citrus and pine hop flavors",
                "https://example.com/sierra-nevada.jpg",
                null
        );

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
        BeverageDTO result = beverageService.createBeverage(validBeverageDTO);

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
        BeverageDTO dto1 = new BeverageDTO(null, "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", null);
        BeverageDTO dto2 = new BeverageDTO(null, "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", null);
        List<BeverageDTO> dtos = Arrays.asList(dto1, dto2);

        Beverage beverage1 = new Beverage(UUID.randomUUID(), "Guinness Stout", "Stout", 4, "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(UUID.randomUUID(), "Corona Extra Lager", "Lager", 5, "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        List<Beverage> beverages = Arrays.asList(beverage1, beverage2);

        when(beverageRepository.saveAll(anyList())).thenReturn(beverages);

        // Act
        List<BeverageDTO> result = beverageService.createBeverages(dtos);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(beverageRepository, times(1)).saveAll(anyList());
    }

    // ========== READ OPERATIONS ==========

    // READ - Happy Paths
    @Test
    void getAllBeveragesShouldReturnAllBeverages() {
        // Arrange
        List<Beverage> beverages = Arrays.asList(savedBeverage);
        when(beverageRepository.findAll()).thenReturn(beverages);

        // Act
        List<BeverageDTO> result = beverageService.getAllBeverages();

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
        BeverageDTO result = beverageService.getBeverageById(testId);

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
        Optional<BeverageDTO> result = beverageService.getBeverageByName("Sierra Nevada Pale Ale");

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
        Optional<BeverageDTO> result = beverageService.getBeverageByName("Non-existent Beer");

        // Assert
        assertFalse(result.isPresent());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByTypeShouldReturnMatchingBeverages() {
        // Arrange
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        // Act
        List<BeverageDTO> result = beverageService.getBeveragesByType("Pale Ale");

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
        List<BeverageDTO> result = beverageService.getBeveragesByType("Stout");

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
        List<BeverageDTO> result = beverageService.getBeveragesByAbvRange(4, 6);

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
        List<BeverageDTO> result = beverageService.getBeveragesByAbvRange(10, 15);

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
        List<BeverageDTO> result = beverageService.getBeveragesByTypeAndAbvRange("Pale Ale", 4, 6);

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



    // ========== UPDATE OPERATIONS ==========

    // UPDATE - Happy Paths
    @Test
    void updateBeverageShouldUpdateAllFieldsSuccessfully() {
        // Arrange
        BeverageDTO updatedDTO = new BeverageDTO(null, "Updated Beer Name", "Updated Type", 8, "This is an updated description for the beverage", "https://example.com/updated.jpg", null);
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        // Act
        BeverageDTO result = beverageService.updateBeverage(testId, updatedDTO);

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
        BeverageDTO result = beverageService.updateBeverageName(testId, "New Beer Name");

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
        BeverageDTO result = beverageService.updateBeverageType(testId, "New Type");

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
        BeverageDTO result = beverageService.updateBeverageAbv(testId, 10);

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
        BeverageDTO result = beverageService.updateBeverageDescription(testId, "This is a brand new updated description");

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
        BeverageDTO result = beverageService.updateBeverageImageUrl(testId, "https://example.com/new-image.jpg");

        // Assert
        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
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

}