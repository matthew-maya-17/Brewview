package com.service;

import com.dto.LocationCreateDTO;
import com.dto.LocationUpdateDTO;
import com.dto.ResponseLocation;
import com.exception.ResourceConflictException;
import com.exception.ResourceNotFoundException;
import com.model.Location;
import com.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;


    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public ResponseLocation createLocation(LocationCreateDTO createDTO) {
        if (locationRepository.existsByLocationName(createDTO.getLocationName())) {
            throw new ResourceConflictException("Location with name '" + createDTO.getLocationName() + "' already exists");
        }
        Location location = toEntity(createDTO);
        Location savedLocation = locationRepository.save(location);
        return toDTO(savedLocation);
    }

    public ResponseLocation getLocationById(UUID id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location with id '" + id + "' not found"));
        return toDTO(location);
    }

    public List<ResponseLocation> getLocationsByName(String locationName) {
        List<Location> locations = locationRepository.findByLocationNames(locationName);
        return toDTOList(locations);
    }

    public List<ResponseLocation> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return toDTOList(locations);
    }

    public List<ResponseLocation> getLocationsByCity(String city) {
        List<Location> locations = locationRepository.findByCity(city);
        return toDTOList(locations);
    }

    public List<ResponseLocation> getLocationsByCountry(String country) {
        List<Location> locations = locationRepository.findByCountry(country);
        return toDTOList(locations);
    }

    public List<ResponseLocation> getLocationsByCityAndCountry(String city, String country) {
        List<Location> locations = locationRepository.findByCityAndCountry(city, country);
        return toDTOList(locations);
    }

    public ResponseLocation updateLocation(UUID id, LocationUpdateDTO updateDTO) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location with id '" + id + "' not found"));

        // Check if the new name conflicts with another location
        if (!location.getLocationName().equals(updateDTO.getLocationName())
                && locationRepository.existsByLocationName(updateDTO.getLocationName())) {
            throw new ResourceConflictException("Location with name '" + updateDTO.getLocationName() + "' already exists");
        }

        updateEntityFromDTO(location, updateDTO);
        Location updatedLocation = locationRepository.save(location);
        return toDTO(updatedLocation);
    }

    public void deleteLocation(UUID id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location with id '" + id + "' not found");
        }
        locationRepository.deleteById(id);
    }

    public boolean locationExists(UUID id) {
        return locationRepository.existsById(id);
    }

    public boolean locationExistsByName(String locationName) {
        return locationRepository.existsByLocationName(locationName);
    }

    private ResponseLocation toDTO(Location location) {
        if (location == null) {
            return null;
        }
        return new ResponseLocation(
                location.getId(),
                location.getLocationName(),
                location.getAddress(),
                location.getCity(),
                location.getCountry()
        );
    }

    private Location toEntity(LocationCreateDTO createDTO) {
        if (createDTO == null) {
            return null;
        }
        return new Location(
                createDTO.getLocationName(),
                createDTO.getAddress(),
                createDTO.getCity(),
                createDTO.getCountry()
        );
    }

    private List<ResponseLocation> toDTOList(List<Location> locations) {
        if (locations == null) {
            return null;
        }
        return locations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private void updateEntityFromDTO(Location location, LocationUpdateDTO updateDTO) {
        if (location == null || updateDTO == null) {
            return;
        }
        location.setLocationName(updateDTO.getLocationName());
        location.setAddress(updateDTO.getAddress());
        location.setCity(updateDTO.getCity());
        location.setCountry(updateDTO.getCountry());
    }
}
