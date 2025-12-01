package com.service;

import com.model.Location;
import com.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private Location testLocation;
    private Location testLocation2;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void createLocation_Success() {
        when(locationRepository.existsByLocationName(testLocation.getLocationName())).thenReturn(false);
        when(locationRepository.save(testLocation)).thenReturn(testLocation);

        Location result = locationService.createLocation(testLocation);

        assertNotNull(result);
        assertEquals(testLocation.getLocationName(), result.getLocationName());
        verify(locationRepository).existsByLocationName(testLocation.getLocationName());
        verify(locationRepository).save(testLocation);
    }

    @Test
    void createLocation_ThrowsException_WhenLocationNameExists() {
        when(locationRepository.existsByLocationName(testLocation.getLocationName())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> locationService.createLocation(testLocation)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(locationRepository).existsByLocationName(testLocation.getLocationName());
        verify(locationRepository, never()).save(any());
    }

    @Test
    void getLocationById_Success() {
        String locationId = "test-id-123";
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(testLocation));

        Optional<Location> result = locationService.getLocationById(locationId);

        assertTrue(result.isPresent());
        assertEquals(testLocation.getLocationName(), result.get().getLocationName());
        verify(locationRepository).findById(locationId);
    }

    @Test
    void getLocationById_ReturnsEmpty_WhenNotFound() {
        String locationId = "non-existent-id";
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        Optional<Location> result = locationService.getLocationById(locationId);

        assertFalse(result.isPresent());
        verify(locationRepository).findById(locationId);
    }

    @Test
    void getLocationByName_Success() {
        when(locationRepository.findByLocationName("Headquarters")).thenReturn(Optional.of(testLocation));

        Optional<Location> result = locationService.getLocationByName("Headquarters");

        assertTrue(result.isPresent());
        assertEquals("Headquarters", result.get().getLocationName());
        verify(locationRepository).findByLocationName("Headquarters");
    }

    @Test
    void getAllLocations_Success() {
        List<Location> locations = Arrays.asList(testLocation, testLocation2);
        when(locationRepository.findAll()).thenReturn(locations);

        List<Location> result = locationService.getAllLocations();

        assertEquals(2, result.size());
        verify(locationRepository).findAll();
    }

    @Test
    void getLocationsByCity_Success() {
        List<Location> locations = Arrays.asList(testLocation);
        when(locationRepository.findByCity("New York")).thenReturn(locations);

        List<Location> result = locationService.getLocationsByCity("New York");

        assertEquals(1, result.size());
        assertEquals("New York", result.get(0).getCity());
        verify(locationRepository).findByCity("New York");
    }

    @Test
    void getLocationsByCountry_Success() {
        List<Location> locations = Arrays.asList(testLocation, testLocation2);
        when(locationRepository.findByCountry("USA")).thenReturn(locations);

        List<Location> result = locationService.getLocationsByCountry("USA");

        assertEquals(2, result.size());
        verify(locationRepository).findByCountry("USA");
    }

    @Test
    void getLocationsByCityAndCountry_Success() {
        List<Location> locations = Arrays.asList(testLocation);
        when(locationRepository.findByCityAndCountry("New York", "USA")).thenReturn(locations);

        List<Location> result = locationService.getLocationsByCityAndCountry("New York", "USA");

        assertEquals(1, result.size());
        assertEquals("New York", result.get(0).getCity());
        assertEquals("USA", result.get(0).getCountry());
        verify(locationRepository).findByCityAndCountry("New York", "USA");
    }

    @Test
    void updateLocation_Success() {
        String locationId = "test-id-123";
        Location updatedLocation = new Location(
                "Headquarters Updated",
                "789 New Street",
                "Boston",
                "USA"
        );

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(testLocation));
        when(locationRepository.existsByLocationName("Headquarters Updated")).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(testLocation);

        Location result = locationService.updateLocation(locationId, updatedLocation);

        assertNotNull(result);
        verify(locationRepository).findById(locationId);
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void updateLocation_ThrowsException_WhenLocationNotFound() {
        String locationId = "non-existent-id";
        when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> locationService.updateLocation(locationId, testLocation)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(locationRepository).findById(locationId);
        verify(locationRepository, never()).save(any());
    }

    @Test
    void updateLocation_ThrowsException_WhenNewNameAlreadyExists() {
        String locationId = "test-id-123";
        Location updatedLocation = new Location(
                "Branch Office",
                "789 New Street",
                "Boston",
                "USA"
        );

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(testLocation));
        when(locationRepository.existsByLocationName("Branch Office")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> locationService.updateLocation(locationId, updatedLocation)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(locationRepository, never()).save(any());
    }

    @Test
    void deleteLocation_Success() {
        String locationId = "test-id-123";
        when(locationRepository.existsById(locationId)).thenReturn(true);
        doNothing().when(locationRepository).deleteById(locationId);

        assertDoesNotThrow(() -> locationService.deleteLocation(locationId));

        verify(locationRepository).existsById(locationId);
        verify(locationRepository).deleteById(locationId);
    }

    @Test
    void deleteLocation_ThrowsException_WhenLocationNotFound() {
        String locationId = "non-existent-id";
        when(locationRepository.existsById(locationId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> locationService.deleteLocation(locationId)
        );

        assertTrue(exception.getMessage().contains("not found"));
        verify(locationRepository).existsById(locationId);
        verify(locationRepository, never()).deleteById(anyString());
    }

    @Test
    void locationExists_ReturnsTrue_WhenExists() {
        String locationId = "test-id-123";
        when(locationRepository.existsById(locationId)).thenReturn(true);

        boolean result = locationService.locationExists(locationId);

        assertTrue(result);
        verify(locationRepository).existsById(locationId);
    }

    @Test
    void locationExists_ReturnsFalse_WhenNotExists() {
        String locationId = "non-existent-id";
        when(locationRepository.existsById(locationId)).thenReturn(false);

        boolean result = locationService.locationExists(locationId);

        assertFalse(result);
        verify(locationRepository).existsById(locationId);
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
