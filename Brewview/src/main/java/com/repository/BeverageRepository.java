package com.repository;

import com.model.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BeverageRepository extends JpaRepository<Beverage, String>{

    Optional<Beverage> findByBeverageName(String beverageName);

    List<Beverage> findByType(String type);

    List<Beverage> findByAbvBetween(int minAbv, int maxAbv);

    List<Beverage> findByTypeAndAbvBetween(String type, int minAbv, int maxAbv);

    boolean existsByBeverageName(String beverageName);

    boolean existsByType(String type);
}
