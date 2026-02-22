package com.service;

import com.dto.LocationCreateDTO;
import com.dto.ResponseLocation;
import com.dto.LocationUpdateDTO;
import com.exception.ResourceConflictException;
import com.exception.ResourceNotFoundException;
import com.model.Location;
import com.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location testLocation;
    private Location testLocation2;
    private Location testLocation3;
    private LocationCreateDTO createDTO;
    private LocationUpdateDTO updateDTO;
    private UUID testLocationId;
    private UUID testLocation2Id;
    private UUID testLocation3Id;

    @BeforeEach
    void setUp() {
        testLocationId = UUID.randomUUID();
        testLocation2Id = UUID.randomUUID();
        testLocation3Id = UUID.randomUUID();

        testLocation = new Location(
                "Headquarters",
                "123 Main Street",
                "New York",
                "USA"
        );

        testLocation2 = new Location(
                "Branch Office",
                "456 Oak Avenue",
                "Los Angeles",
                "USA"
        );

        testLocation3 = new Location(
                "Regional Headquarters",
                "789 Pine Street",
                "Chicago",
                "USA"
        );

        createDTO = new LocationCreateDTO(
                "Headquarters",
                "123 Main Street",
                "New York",
                "USA"
        );

        updateDTO = new LocationUpdateDTO(
                "Headquarters Updated",
                "789 New Street",
                "Boston",
                "USA"
        );
    }

    @Test
    void createLocation_Success() {
        when(locationRepository.existsByLocationName(createDTO.getLocationName())).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);

        ResponseLocation result = locationService.createLocation(createDTO);

        assertNotNull(result);
        assertEquals(createDTO.getLocationName(), result.getLocationName());
        assertEquals(createDTO.getAddress(), result.getAddress());
        verify(locationRepository).existsByLocationName(createDTO.getLocationName());
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void createLocation_ThrowsException_WhenLocationNameExists() {
        when(locationRepository.existsByLocationName(createDTO.getLocationName())).thenReturn(true);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> locationService.createLocation(createDTO)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(locationRepository).existsByLocationName(createDTO.getLocationName());
        verify(locationRepository, never()).save(any());
    }

    @Test
    void getLocationById_Success() {
        when(locationRepository.findById(testLocationId)).thenReturn(Optional.of(testLocation));

        ResponseLocation result = locationService.getLocationById(testLocationId);

        assertNotNull(result);
        assertEquals(testLocation.getLocationName(), result.getLocationName());
        verify(locationRepository).findById(testLocationId);
    }

    @Test
    void getLocationById_ThrowsException_WhenNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(locationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> locationService.getLocationById(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(locationRepository).findById(nonExistentId);
    }

    @Test
    void getLocationsByName_FindsMultipleLocations() {
        List<Location> locations = Arrays.asList(testLocation, testLocation3);
        when(locationRepository.findByLocationNameContainingIgnoreCase("Headquarters"))
                .thenReturn(locations);

        List<ResponseLocation> results = locationService.getLocationsByName("Headquarters");

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(l -> l.getLocationName().equals("Headquarters")));
        assertTrue(results.stream().anyMatch(l -> l.getLocationName().equals("Regional Headquarters")));
        verify(locationRepository).findByLocationNameContainingIgnoreCase("Headquarters");
    }

    @Test
    void getLocationsByName_FindsSingleLocation() {
        List<Location> locations = Collections.singletonList(testLocation2);
        when(locationRepository.findByLocationNameContainingIgnoreCase("Branch"))
                .thenReturn(locations);

        List<ResponseLocation> results = locationService.getLocationsByName("Branch");

        assertEquals(1, results.size());
        assertEquals("Branch Office", results.get(0).getLocationName());
        verify(locationRepository).findByLocationNameContainingIgnoreCase("Branch");
    }

    @Test
    void getLocationsByName_ReturnsEmptyList_WhenNoMatches() {
        when(locationRepository.findByLocationNameContainingIgnoreCase("Pizza"))
                .thenReturn(Collections.emptyList());

        List<ResponseLocation> results = locationService.getLocationsByName("Pizza");

        assertTrue(results.isEmpty());
        verify(locationRepository).findByLocationNameContainingIgnoreCase("Pizza");
    }

    @Test
    void getLocationsByName_CaseInsensitive() {
        List<Location> locations = Collections.singletonList(testLocation);
        when(locationRepository.findByLocationNameContainingIgnoreCase("headquarters"))
                .thenReturn(locations);

        List<ResponseLocation> results = locationService.getLocationsByName("headquarters");

        assertEquals(1, results.size());
        assertEquals("Headquarters", results.get(0).getLocationName());
        verify(locationRepository).findByLocationNameContainingIgnoreCase("headquarters");
    }

    @Test
    void getLocationsByName_PartialMatch() {
        List<Location> locations = Collections.singletonList(testLocation);
        when(locationRepository.findByLocationNameContainingIgnoreCase("Head"))
                .thenReturn(locations);

        List<ResponseLocation> results = locationService.getLocationsByName("Head");

        assertEquals(1, results.size());
        assertEquals("Headquarters", results.get(0).getLocationName());
        verify(locationRepository).findByLocationNameContainingIgnoreCase("Head");

    }

    @Test
    void getAllLocations_Success() {
        List<Location> locations = Arrays.asList(testLocation, testLocation2);
        when(locationRepository.findAll()).thenReturn(locations);

        List<ResponseLocation> result = locationService.getAllLocations();

        assertEquals(2, result.size());
        assertEquals(testLocation.getLocationName(), result.get(0).getLocationName());
        assertEquals(testLocation2.getLocationName(), result.get(1).getLocationName());
        verify(locationRepository).findAll();
    }

    @Test
    void getLocationsByCity_Success() {
        List<Location> locations = Arrays.asList(testLocation);
        when(locationRepository.findByCity("New York")).thenReturn(locations);

        List<ResponseLocation> result = locationService.getLocationsByCity("New York");

        assertEquals(1, result.size());
        assertEquals("New York", result.get(0).getCity());
        verify(locationRepository).findByCity("New York");
    }

    @Test
    void getLocationsByCountry_Success() {
        List<Location> locations = Arrays.asList(testLocation, testLocation2);
        when(locationRepository.findByCountry("USA")).thenReturn(locations);

        List<ResponseLocation> result = locationService.getLocationsByCountry("USA");

        assertEquals(2, result.size());
        verify(locationRepository).findByCountry("USA");
    }

    @Test
    void getLocationsByCityAndCountry_Success() {
        List<Location> locations = Arrays.asList(testLocation);
        when(locationRepository.findByCityAndCountry("New York", "USA")).thenReturn(locations);

        List<ResponseLocation> result = locationService.getLocationsByCityAndCountry("New York", "USA");

        assertEquals(1, result.size());
        assertEquals("New York", result.get(0).getCity());
        assertEquals("USA", result.get(0).getCountry());
        verify(locationRepository).findByCityAndCountry("New York", "USA");
    }

    @Test
    void updateLocation_Success() {
        when(locationRepository.findById(testLocationId)).thenReturn(Optional.of(testLocation));
        when(locationRepository.existsByLocationName(updateDTO.getLocationName())).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);

        ResponseLocation result = locationService.updateLocation(testLocationId, updateDTO);

        assertNotNull(result);
        verify(locationRepository).findById(testLocationId);
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void updateLocation_ThrowsException_WhenLocationNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(locationRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> locationService.updateLocation(nonExistentId, updateDTO)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(locationRepository).findById(nonExistentId);
        verify(locationRepository, never()).save(any());
    }

    @Test
    void updateLocation_ThrowsException_WhenNewNameAlreadyExists() {
        LocationUpdateDTO duplicateUpdateDTO = new LocationUpdateDTO(
                "Branch Office",
                "789 New Street",
                "Boston",
                "USA"
        );

        when(locationRepository.findById(testLocationId)).thenReturn(Optional.of(testLocation));
        when(locationRepository.existsByLocationName("Branch Office")).thenReturn(true);

        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> locationService.updateLocation(testLocationId, duplicateUpdateDTO)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(locationRepository, never()).save(any());
    }

    @Test
    void deleteLocation_Success() {
        when(locationRepository.existsById(testLocationId)).thenReturn(true);
        doNothing().when(locationRepository).deleteById(testLocationId);

        assertDoesNotThrow(() -> locationService.deleteLocation(testLocationId));

        verify(locationRepository).existsById(testLocationId);
        verify(locationRepository).deleteById(testLocationId);
    }

    @Test
    void deleteLocation_ThrowsException_WhenLocationNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(locationRepository.existsById(nonExistentId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> locationService.deleteLocation(nonExistentId)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(locationRepository).existsById(nonExistentId);
        verify(locationRepository, never()).deleteById(any());
    }

    @Test
    void locationExists_ReturnsTrue_WhenExists() {
        when(locationRepository.existsById(testLocationId)).thenReturn(true);

        boolean result = locationService.locationExists(testLocationId);

        assertTrue(result);
        verify(locationRepository).existsById(testLocationId);
    }

    @Test
    void locationExists_ReturnsFalse_WhenNotExists() {
        UUID nonExistentId = UUID.randomUUID();
        when(locationRepository.existsById(nonExistentId)).thenReturn(false);

        boolean result = locationService.locationExists(nonExistentId);

        assertFalse(result);
        verify(locationRepository).existsById(nonExistentId);
    }

    @Test
    void locationExistsByName_ReturnsTrue_WhenExists() {
        when(locationRepository.existsByLocationName("Headquarters")).thenReturn(true);

        boolean result = locationService.locationExistsByName("Headquarters");

        assertTrue(result);
        verify(locationRepository).existsByLocationName("Headquarters");
    }

    @Test
    void locationExistsByName_ReturnsFalse_WhenNotExists() {
        when(locationRepository.existsByLocationName("Non-existent")).thenReturn(false);

        boolean result = locationService.locationExistsByName("Non-existent");

        assertFalse(result);
        verify(locationRepository).existsByLocationName("Non-existent");
    }
}
