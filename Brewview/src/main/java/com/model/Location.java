package com.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "locations")
public class Location {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "location_name", nullable = false)
    @NotBlank(message = "Location name cannot be blank")
    @Size(message = "Location name must be between 1 and 255 characters!", min = 1, max = 255)
    private String locationName;

    @Column(name = "address", nullable = false)
    @NotBlank(message = "Address cannot be blank")
    @Size(message = "Address must be between 1 and 500 characters!", min = 1, max = 500)
    private String address;

    @Column(name = "city", nullable = false)
    @NotBlank(message = "City cannot be blank")
    @Size(message = "City must be between 1 and 100 characters!", min = 1, max = 100)
    private String city;

    @Column(name = "country", nullable = false)
    @NotBlank(message = "Country cannot be blank")
    @Size(message = "Country must be between 4 and 100 characters!", min = 4, max = 100)
    private String country;

    public Location() {}

    public Location(String locationName, String address, String city, String country) {
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
