package com.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class LocationDTO {
    private UUID id;

    @NotBlank(message = "Location name cannot be blank")
    @Size(message = "Location name must be between 1 and 255 characters!", min = 1, max = 255)
    private String locationName;

    @NotBlank(message = "Address cannot be blank")
    @Size(message = "Address must be between 1 and 500 characters!", min = 1, max = 500)
    private String address;

    @NotBlank(message = "City cannot be blank")
    @Size(message = "City must be between 1 and 100 characters!", min = 1, max = 100)
    private String city;

    @NotBlank(message = "Country cannot be blank")
    @Size(message = "Country must be between 1 and 100 characters!", min = 1, max = 100)
    private String country;

    public LocationDTO() {}

    public LocationDTO(UUID id, String locationName, String address, String city, String country) {
        this.id = id;
        this.locationName = locationName;
        this.address = address;
        this.city = city;
        this.country = country;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
