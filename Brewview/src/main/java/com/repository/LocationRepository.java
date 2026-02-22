package com.repository;

import com.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    List<Location> findByLocationName(String locationName);

    List<Location> findByCity(String city);

    List<Location> findByCountry(String country);

    List<Location> findByCityAndCountry(String city, String country);

    boolean existsByLocationName(String locationName);

}
