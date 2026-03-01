package com.repository;

import com.model.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface BeverageRepository extends JpaRepository<Beverage, UUID>{

    Optional<Beverage> findByBeverageName(String beverageName);

    List<Beverage> findByType(String type);

    List<Beverage> findByAbvBetween(BigDecimal minAbv, BigDecimal maxAbv);

    List<Beverage> findByTypeAndAbvBetween(String type, BigDecimal minAbv, BigDecimal maxAbv);

    boolean existsByBeverageName(String beverageName);

    boolean existsByType(String type);

    boolean existsByBeverageNameAndTypeAndAbv(String beverageName, String type, BigDecimal abv);

    Optional<Beverage> findByBeverageNameAndTypeAndAbv(String beverageName, String type, BigDecimal abv);
}
