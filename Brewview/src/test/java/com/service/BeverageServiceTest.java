package com.service;

import com.dto.BeverageRequestDTO;
import com.dto.ResponseBeverage;
import com.exception.BadRequestException;
import com.exception.ResourceConflictException;
import com.exception.ResourceNotFoundException;
import com.model.Beverage;
import com.repository.BeverageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
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
    private Beverage savedBeverage2;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        validRequestDTO = new BeverageRequestDTO(
                "Sierra Nevada Pale Ale",
                "Pale Ale",
                new BigDecimal("5.6"),
                "A classic American pale ale with citrus and pine hop flavors",
                "https://example.com/sierra-nevada.jpg"
        );

        savedBeverage = new Beverage(
                testId,
                "Sierra Nevada Pale Ale",
                "Pale Ale",
                new BigDecimal("5.6"),
                "A classic American pale ale with citrus and pine hop flavors",
                "https://example.com/sierra-nevada.jpg",
                LocalDateTime.now()
        );

        savedBeverage2 = new Beverage(
                UUID.randomUUID(),
                "Lagunitas IPA",
                "IPA",
                new BigDecimal("6.2"),
                "A well-rounded, highly drinkable IPA with citrus and pine notes",
                "https://example.com/lagunitas.jpg",
                LocalDateTime.now()
        );
    }

    // ========== CREATE OPERATIONS ==========

    // CREATE - Happy Paths
    @Test
    void createBeverageShouldSaveBeverageSuccessfully() {
        when(beverageRepository.existsByBeverageName(validRequestDTO.getBeverageName())).thenReturn(false);
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        ResponseBeverage result = beverageService.createBeverage(validRequestDTO);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals("Sierra Nevada Pale Ale", result.getBeverageName());
        assertNotNull(result.getCreatedAt());
        verify(beverageRepository, times(1)).existsByBeverageName(validRequestDTO.getBeverageName());
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void createBeveragesShouldSaveMultipleBeverages() {
        BeverageRequestDTO requestDTO1 = new BeverageRequestDTO("Guinness Stout", "Stout", new BigDecimal("4.2"), "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg");
        BeverageRequestDTO requestDTO2 = new BeverageRequestDTO("Corona Extra Lager", "Lager", new BigDecimal("4.5"), "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg");
        List<BeverageRequestDTO> requestDTOs = Arrays.asList(requestDTO1, requestDTO2);

        Beverage beverage1 = new Beverage(UUID.randomUUID(), "Guinness Stout", "Stout", new BigDecimal("4.2"), "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg", LocalDateTime.now());
        Beverage beverage2 = new Beverage(UUID.randomUUID(), "Corona Extra Lager", "Lager", new BigDecimal("4.5"), "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg", LocalDateTime.now());
        List<Beverage> beverages = Arrays.asList(beverage1, beverage2);

        when(beverageRepository.existsByBeverageName("Guinness Stout")).thenReturn(false);
        when(beverageRepository.existsByBeverageName("Corona Extra Lager")).thenReturn(false);
        when(beverageRepository.saveAll(anyList())).thenReturn(beverages);

        List<ResponseBeverage> result = beverageService.createBeverages(requestDTOs);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(beverageRepository, times(2)).existsByBeverageName(anyString());
        verify(beverageRepository, times(1)).saveAll(anyList());
    }

    // CREATE - Unhappy Paths
    @Test
    void createBeverageShouldThrowExceptionWhenBeverageIsNull() {
        assertThrows(BadRequestException.class, () -> beverageService.createBeverage(null));
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void createBeverageShouldThrowConflictWhenBeverageNameExists() {
        when(beverageRepository.existsByBeverageName(validRequestDTO.getBeverageName())).thenReturn(true);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> beverageService.createBeverage(validRequestDTO)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(beverageRepository, times(1)).existsByBeverageName(validRequestDTO.getBeverageName());
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void createBeveragesShouldThrowExceptionWhenListIsNull() {
        assertThrows(BadRequestException.class, () -> beverageService.createBeverages(null));
        verify(beverageRepository, never()).saveAll(anyList());
    }

    @Test
    void createBeveragesShouldThrowExceptionWhenListIsEmpty() {
        assertThrows(BadRequestException.class, () -> beverageService.createBeverages(List.of()));
        verify(beverageRepository, never()).saveAll(anyList());
    }

    @Test
    void createBeveragesShouldThrowConflictWhenDuplicateNamesInRequest() {
        BeverageRequestDTO requestDTO1 = new BeverageRequestDTO("Guinness Stout", "Stout", new BigDecimal("4.2"), "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg");
        BeverageRequestDTO requestDTO2 = new BeverageRequestDTO("Guinness Stout", "Stout", new BigDecimal("4.2"), "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg");
        List<BeverageRequestDTO> requestDTOs = Arrays.asList(requestDTO1, requestDTO2);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> beverageService.createBeverages(requestDTOs)
        );

        assertTrue(exception.getMessage().contains("Duplicate beverage names found in request"));
        verify(beverageRepository, never()).saveAll(anyList());
    }

    @Test
    void createBeveragesShouldThrowConflictWhenBeverageNameExistsInDatabase() {
        BeverageRequestDTO requestDTO1 = new BeverageRequestDTO("Guinness Stout", "Stout", new BigDecimal("4.2"), "Rich and creamy Irish stout with roasted flavors", "https://example.com/guinness.jpg");
        BeverageRequestDTO requestDTO2 = new BeverageRequestDTO("Corona Extra Lager", "Lager", new BigDecimal("4.5"), "Light and refreshing Mexican lager with citrus notes", "https://example.com/corona.jpg");
        List<BeverageRequestDTO> requestDTOs = Arrays.asList(requestDTO1, requestDTO2);

        when(beverageRepository.existsByBeverageName("Guinness Stout")).thenReturn(true);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> beverageService.createBeverages(requestDTOs)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(beverageRepository, times(1)).existsByBeverageName("Guinness Stout");
        verify(beverageRepository, never()).saveAll(anyList());
    }

    // ========== READ OPERATIONS ==========

    // READ - Happy Paths
    @Test
    void getAllBeveragesShouldReturnAllBeverages() {
        List<Beverage> beverages = Arrays.asList(savedBeverage);
        when(beverageRepository.findAll()).thenReturn(beverages);

        List<ResponseBeverage> result = beverageService.getAllBeverages();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sierra Nevada Pale Ale", result.get(0).getBeverageName());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getAllBeveragesShouldReturnEmptyListWhenNoBeverages() {
        when(beverageRepository.findAll()).thenReturn(Collections.emptyList());

        List<ResponseBeverage> result = beverageService.getAllBeverages();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeverageByIdShouldReturnBeverageWhenExists() {
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));

        ResponseBeverage result = beverageService.getBeverageById(testId);

        assertNotNull(result);
        assertEquals("Sierra Nevada Pale Ale", result.getBeverageName());
        assertEquals(testId, result.getId());
        verify(beverageRepository, times(1)).findById(testId);
    }

    @Test
    void getBeverageByNameShouldReturnBeverageWhenExists() {
        when(beverageRepository.findByBeverageName("Sierra Nevada Pale Ale")).thenReturn(Optional.of(savedBeverage));

        ResponseBeverage result = beverageService.getBeverageByName("Sierra Nevada Pale Ale");

        assertNotNull(result);
        assertEquals("Sierra Nevada Pale Ale", result.getBeverageName());
        verify(beverageRepository, times(1)).findByBeverageName("Sierra Nevada Pale Ale");
    }

    @Test
    void getBeveragesByTypeShouldReturnMatchingBeveragesCaseInsensitive() {
        when(beverageRepository.findAll()).thenReturn(Arrays.asList(savedBeverage, savedBeverage2));

        // Test with different cases
        List<ResponseBeverage> result1 = beverageService.getBeveragesByType("pale ale");
        List<ResponseBeverage> result2 = beverageService.getBeveragesByType("PALE ALE");
        List<ResponseBeverage> result3 = beverageService.getBeveragesByType("Pale Ale");

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertEquals(1, result3.size());
        assertEquals("Pale Ale", result1.get(0).getType());
        verify(beverageRepository, times(3)).findAll();
    }

    @Test
    void getBeveragesByTypeShouldReturnEmptyListWhenNoMatches() {
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        List<ResponseBeverage> result = beverageService.getBeveragesByType("Stout");

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void getBeveragesByAbvRangeShouldReturnMatchingBeverages() {
        when(beverageRepository.findByAbvBetween(new BigDecimal("4.0"), new BigDecimal("6.0")))
                .thenReturn(List.of(savedBeverage));

        List<ResponseBeverage> result = beverageService.getBeveragesByAbvRange(
                new BigDecimal("4.0"),
                new BigDecimal("6.0")
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, new BigDecimal("5.6").compareTo(result.get(0).getAbv()));
        verify(beverageRepository, times(1)).findByAbvBetween(new BigDecimal("4.0"), new BigDecimal("6.0"));
    }

    @Test
    void getBeveragesByAbvRangeShouldReturnEmptyListWhenNoMatches() {
        when(beverageRepository.findByAbvBetween(new BigDecimal("10.0"), new BigDecimal("15.0")))
                .thenReturn(Collections.emptyList());

        List<ResponseBeverage> result = beverageService.getBeveragesByAbvRange(
                new BigDecimal("10.0"),
                new BigDecimal("15.0")
        );

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(beverageRepository, times(1)).findByAbvBetween(new BigDecimal("10.0"), new BigDecimal("15.0"));
    }

    @Test
    void getBeveragesByTypeAndAbvRangeShouldReturnMatchingBeverages() {
        when(beverageRepository.findAll()).thenReturn(List.of(savedBeverage));

        List<ResponseBeverage> result = beverageService.getBeveragesByTypeAndAbvRange(
                "Pale Ale",
                new BigDecimal("4.0"),
                new BigDecimal("6.0")
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Pale Ale", result.get(0).getType());
        assertEquals(0, new BigDecimal("5.6").compareTo(result.get(0).getAbv()));
        verify(beverageRepository, times(1)).findAll();
    }

    @Test
    void beverageExistsShouldReturnTrueWhenExists() {
        when(beverageRepository.existsById(testId)).thenReturn(true);

        boolean result = beverageService.beverageExists(testId);

        assertTrue(result);
        verify(beverageRepository, times(1)).existsById(testId);
    }

    @Test
    void beverageExistsShouldReturnFalseWhenNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.existsById(nonExistentId)).thenReturn(false);

        boolean result = beverageService.beverageExists(nonExistentId);

        assertFalse(result);
        verify(beverageRepository, times(1)).existsById(nonExistentId);
    }

    @Test
    void beverageNameExistsShouldReturnTrueWhenExists() {
        when(beverageRepository.existsByBeverageName("Sierra Nevada Pale Ale")).thenReturn(true);

        boolean result = beverageService.beverageNameExists("Sierra Nevada Pale Ale");

        assertTrue(result);
        verify(beverageRepository, times(1)).existsByBeverageName("Sierra Nevada Pale Ale");
    }

    @Test
    void beverageNameExistsShouldReturnFalseWhenNotExists() {
        when(beverageRepository.existsByBeverageName("Non-existent Beer")).thenReturn(false);

        boolean result = beverageService.beverageNameExists("Non-existent Beer");

        assertFalse(result);
        verify(beverageRepository, times(1)).existsByBeverageName("Non-existent Beer");
    }

    @Test
    void beverageNameExistsShouldReturnFalseWhenNameIsBlank() {
        // Should return false, not throw exception
        boolean result1 = beverageService.beverageNameExists("");
        boolean result2 = beverageService.beverageNameExists("   ");
        boolean result3 = beverageService.beverageNameExists(null);

        assertFalse(result1);
        assertFalse(result2);
        assertFalse(result3);
        verify(beverageRepository, never()).existsByBeverageName(anyString());
    }

    @Test
    void getTotalBeverageCountShouldReturnCorrectCount() {
        when(beverageRepository.count()).thenReturn(5L);

        long result = beverageService.getTotalBeverageCount();

        assertEquals(5L, result);
        verify(beverageRepository, times(1)).count();
    }

    // READ - Unhappy Paths
    @Test
    void getBeverageByIdShouldThrowExceptionWhenIdIsNull() {
        assertThrows(BadRequestException.class, () -> beverageService.getBeverageById(null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void getBeverageByIdShouldThrowExceptionWhenNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> beverageService.getBeverageById(nonExistentId));
        verify(beverageRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void getBeverageByNameShouldThrowExceptionWhenNameIsNull() {
        assertThrows(BadRequestException.class, () -> beverageService.getBeverageByName(null));
        verify(beverageRepository, never()).findByBeverageName(anyString());
    }

    @Test
    void getBeverageByNameShouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(BadRequestException.class, () -> beverageService.getBeverageByName("   "));
        verify(beverageRepository, never()).findByBeverageName(anyString());
    }

    @Test
    void getBeverageByNameShouldThrowExceptionWhenNameIsTooShort() {
        assertThrows(BadRequestException.class, () -> beverageService.getBeverageByName("Beer"));
        verify(beverageRepository, never()).findByBeverageName(anyString());
    }

    @Test
    void getBeverageByNameShouldThrowNotFoundWhenBeverageDoesNotExist() {
        when(beverageRepository.findByBeverageName("Non-existent Beer")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> beverageService.getBeverageByName("Non-existent Beer"));
        verify(beverageRepository, times(1)).findByBeverageName("Non-existent Beer");
    }

    @Test
    void getBeveragesByTypeShouldThrowExceptionWhenTypeIsNull() {
        assertThrows(BadRequestException.class, () -> beverageService.getBeveragesByType(null));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByTypeShouldThrowExceptionWhenTypeIsEmpty() {
        assertThrows(BadRequestException.class, () -> beverageService.getBeveragesByType("   "));
        verify(beverageRepository, never()).findAll();
    }

    @Test
    void getBeveragesByAbvRangeShouldThrowExceptionWhenMinAbvIsNegative() {
        assertThrows(BadRequestException.class,
                () -> beverageService.getBeveragesByAbvRange(new BigDecimal("-1"), new BigDecimal("10")));
        verify(beverageRepository, never()).findByAbvBetween(any(), any());
    }

    @Test
    void getBeveragesByAbvRangeShouldThrowExceptionWhenMaxAbvLessThanMinAbv() {
        assertThrows(BadRequestException.class,
                () -> beverageService.getBeveragesByAbvRange(new BigDecimal("10"), new BigDecimal("5")));
        verify(beverageRepository, never()).findByAbvBetween(any(), any());
    }

    @Test
    void beverageExistsShouldThrowExceptionWhenIdIsNull() {
        assertThrows(BadRequestException.class, () -> beverageService.beverageExists(null));
        verify(beverageRepository, never()).existsById(any(UUID.class));
    }

    // ========== UPDATE OPERATIONS ==========

    // UPDATE - Happy Paths
    @Test
    void updateBeverageShouldUpdateAllFieldsSuccessfully() {
        BeverageRequestDTO requestDTO = new BeverageRequestDTO(
                "Updated Beer Name",
                "Updated Type",
                new BigDecimal("8.5"),
                "This is an updated description for the beverage",
                "https://example.com/updated.jpg"
        );
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.existsByBeverageName("Updated Beer Name")).thenReturn(false);
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        ResponseBeverage result = beverageService.updateBeverage(testId, requestDTO);

        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).existsByBeverageName("Updated Beer Name");
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageShouldNotCheckExistenceWhenNameUnchanged() {
        BeverageRequestDTO requestDTO = new BeverageRequestDTO(
                "Sierra Nevada Pale Ale",  // Same name
                "Updated Type",
                new BigDecimal("8.5"),
                "This is an updated description for the beverage",
                "https://example.com/updated.jpg"
        );
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        ResponseBeverage result = beverageService.updateBeverage(testId, requestDTO);

        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, never()).existsByBeverageName(anyString());
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageNameShouldUpdateNameSuccessfully() {
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.existsByBeverageName("New Beer Name")).thenReturn(false);
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        ResponseBeverage result = beverageService.updateBeverageName(testId, "New Beer Name");

        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).existsByBeverageName("New Beer Name");
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageTypeShouldUpdateTypeSuccessfully() {
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        ResponseBeverage result = beverageService.updateBeverageType(testId, "New Type");

        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageAbvShouldUpdateAbvSuccessfully() {
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        ResponseBeverage result = beverageService.updateBeverageAbv(testId, new BigDecimal("10.5"));

        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageDescriptionShouldUpdateDescriptionSuccessfully() {
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        ResponseBeverage result = beverageService.updateBeverageDescription(
                testId,
                "This is a brand new updated description"
        );

        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    @Test
    void updateBeverageImageUrlShouldUpdateImageUrlSuccessfully() {
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.save(any(Beverage.class))).thenReturn(savedBeverage);

        ResponseBeverage result = beverageService.updateBeverageImageUrl(
                testId,
                "https://example.com/new-image.jpg"
        );

        assertNotNull(result);
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).save(any(Beverage.class));
    }

    // UPDATE - Unhappy Paths
    @Test
    void updateBeverageShouldThrowExceptionWhenIdIsNull() {
        BeverageRequestDTO requestDTO = new BeverageRequestDTO(
                "Updated Beer",
                "IPA",
                new BigDecimal("6.0"),
                "Updated description for the beer",
                "https://example.com/updated.jpg"
        );

        assertThrows(BadRequestException.class, () -> beverageService.updateBeverage(null, requestDTO));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageShouldThrowExceptionWhenUpdatedBeverageIsNull() {
        assertThrows(BadRequestException.class, () -> beverageService.updateBeverage(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageShouldThrowExceptionWhenBeverageNotFound() {
        BeverageRequestDTO requestDTO = new BeverageRequestDTO(
                "Updated Beer",
                "IPA",
                new BigDecimal("6.0"),
                "Updated description for the beer",
                "https://example.com/updated.jpg"
        );
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> beverageService.updateBeverage(nonExistentId, requestDTO));
        verify(beverageRepository, times(1)).findById(nonExistentId);
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void updateBeverageShouldThrowConflictWhenNewNameExists() {
        BeverageRequestDTO requestDTO = new BeverageRequestDTO(
                "Existing Beer Name",
                "IPA",
                new BigDecimal("6.0"),
                "Updated description for the beer",
                "https://example.com/updated.jpg"
        );
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.existsByBeverageName("Existing Beer Name")).thenReturn(true);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> beverageService.updateBeverage(testId, requestDTO)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).existsByBeverageName("Existing Beer Name");
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenIdIsNull() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageName(null, "New Name"));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenNameIsNull() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageName(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageName(testId, "   "));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenNameIsTooShort() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageName(testId, "Beer"));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageNameShouldThrowExceptionWhenBeverageNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> beverageService.updateBeverageName(nonExistentId, "New Beer Name"));
        verify(beverageRepository, times(1)).findById(nonExistentId);
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void updateBeverageNameShouldThrowConflictWhenNewNameExists() {
        when(beverageRepository.findById(testId)).thenReturn(Optional.of(savedBeverage));
        when(beverageRepository.existsByBeverageName("Existing Name")).thenReturn(true);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> beverageService.updateBeverageName(testId, "Existing Name")
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(beverageRepository, times(1)).findById(testId);
        verify(beverageRepository, times(1)).existsByBeverageName("Existing Name");
        verify(beverageRepository, never()).save(any(Beverage.class));
    }

    @Test
    void updateBeverageTypeShouldThrowExceptionWhenTypeIsNull() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageType(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageTypeShouldThrowExceptionWhenTypeIsEmpty() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageType(testId, "   "));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageAbvShouldThrowExceptionWhenAbvIsNegative() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageAbv(testId, new BigDecimal("-1")));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageDescriptionShouldThrowExceptionWhenDescriptionIsNull() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageDescription(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageDescriptionShouldThrowExceptionWhenDescriptionIsEmpty() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageDescription(testId, "   "));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageDescriptionShouldThrowExceptionWhenDescriptionIsTooShort() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageDescription(testId, "Too short"));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageImageUrlShouldThrowExceptionWhenImageUrlIsNull() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageImageUrl(testId, null));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    @Test
    void updateBeverageImageUrlShouldThrowExceptionWhenImageUrlIsEmpty() {
        assertThrows(BadRequestException.class,
                () -> beverageService.updateBeverageImageUrl(testId, "   "));
        verify(beverageRepository, never()).findById(any(UUID.class));
    }

    // ========== DELETE OPERATIONS ==========

    // DELETE - Happy Paths
    @Test
    void deleteBeverageShouldDeleteSuccessfully() {
        when(beverageRepository.existsById(testId)).thenReturn(true);
        doNothing().when(beverageRepository).deleteById(testId);

        beverageService.deleteBeverage(testId);

        verify(beverageRepository, times(1)).existsById(testId);
        verify(beverageRepository, times(1)).deleteById(testId);
    }

    @Test
    void deleteAllBeveragesShouldDeleteAllSuccessfully() {
        doNothing().when(beverageRepository).deleteAll();

        beverageService.deleteAllBeverages();

        verify(beverageRepository, times(1)).deleteAll();
    }

    @Test
    void deleteBeveragesByIdsShouldDeleteMultipleBeverages() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        List<UUID> ids = Arrays.asList(id1, id2, id3);
        doNothing().when(beverageRepository).deleteAllById(ids);

        beverageService.deleteBeveragesByIds(ids);

        verify(beverageRepository, times(1)).deleteAllById(ids);
    }

    // DELETE - Unhappy Paths
    @Test
    void deleteBeverageShouldThrowExceptionWhenIdIsNull() {
        assertThrows(BadRequestException.class, () -> beverageService.deleteBeverage(null));
        verify(beverageRepository, never()).existsById(any(UUID.class));
        verify(beverageRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void deleteBeverageShouldThrowExceptionWhenBeverageNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(beverageRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> beverageService.deleteBeverage(nonExistentId));
        verify(beverageRepository, times(1)).existsById(nonExistentId);
        verify(beverageRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void deleteBeveragesByIdsShouldThrowExceptionWhenIdsIsNull() {
        assertThrows(BadRequestException.class,
                () -> beverageService.deleteBeveragesByIds(null));
        verify(beverageRepository, never()).deleteAllById(anyList());
    }

    @Test
    void deleteBeveragesByIdsShouldThrowExceptionWhenIdsIsEmpty() {
        assertThrows(BadRequestException.class,
                () -> beverageService.deleteBeveragesByIds(List.of()));
        verify(beverageRepository, never()).deleteAllById(anyList());
    }
}