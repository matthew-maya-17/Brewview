package com.service;

import com.model.Location;
import com.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location createLocation(Location location) {
        if (locationRepository.existsByLocationName(location.getLocationName())) {
            throw new IllegalArgumentException("Location with name '" + location.getLocationName() + "' already exists");
        }
        return locationRepository.save(location);
    }

    public Optional<Location> getLocationById(String id) {
        return locationRepository.findById(id);
    }

    public Optional<Location> getLocationByName(String locationName) {
        return locationRepository.findByLocationName(locationName);
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public List<Location> getLocationsByCity(String city) {
        return locationRepository.findByCity(city);
    }

    public List<Location> getLocationsByCountry(String country) {
        return locationRepository.findByCountry(country);
    }

    public List<Location> getLocationsByCityAndCountry(String city, String country) {
        return locationRepository.findByCityAndCountry(city, country);
    }

    public Location updateLocation(String id, Location updatedLocation) {
        return locationRepository.findById(id)
                .map(location -> {
                    if (!location.getLocationName().equals(updatedLocation.getLocationName())
                            && locationRepository.existsByLocationName(updatedLocation.getLocationName())) {
                        throw new IllegalArgumentException("Location with name '" + updatedLocation.getLocationName() + "' already exists");
                    }

                    location.setLocationName(updatedLocation.getLocationName());
                    location.setAddress(updatedLocation.getAddress());
                    location.setCity(updatedLocation.getCity());
                    location.setCountry(updatedLocation.getCountry());
                    return locationRepository.save(location);
                })
                .orElseThrow(() -> new IllegalArgumentException("Location with id '" + id + "' not found"));
    }

    public void deleteLocation(String id) {
        if (!locationRepository.existsById(id)) {
            throw new IllegalArgumentException("Location with id '" + id + "' not found");
        }
        locationRepository.deleteById(id);
    }

    public boolean locationExists(String id) {
        return locationRepository.existsById(id);
    }

    public boolean locationExistsByName(String locationName) {
        return locationRepository.existsByLocationName(locationName);
    }
}
