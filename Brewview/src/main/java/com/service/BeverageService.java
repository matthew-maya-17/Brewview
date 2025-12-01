package com.service;

import com.model.Beverage;
import com.repository.BeverageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BeverageService {

    private final BeverageRepository beverageRepository;

    @Autowired
    public BeverageService(BeverageRepository beverageRepository) {
        this.beverageRepository = beverageRepository;
    }


    public Beverage createBeverage(Beverage beverage) {
        if (beverage == null) {
            throw new IllegalArgumentException("Beverage cannot be null");
        }

        beverage.setCreatedAt(LocalDateTime.now());

        return beverageRepository.save(beverage);
    }

    public List<Beverage> createBeverages(List<Beverage> beverages) {
        if (beverages == null || beverages.isEmpty()) {
            throw new IllegalArgumentException("Beverage list cannot be null or empty");
        }

        beverages.forEach(beverage -> beverage.setCreatedAt(LocalDateTime.now()));
        return beverageRepository.saveAll(beverages);
    }


    public List<Beverage> getAllBeverages() {
        return beverageRepository.findAll();
    }

    public Optional<Beverage> getBeverageById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage ID cannot be null or empty");
        }
        return beverageRepository.findById(id);
    }

    public Optional<Beverage> getBeverageByName(String beverageName) {
        if (beverageName == null || beverageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage name cannot be null or empty");
        }

        List<Beverage> beverages = beverageRepository.findAll();
        return beverages.stream()
                .filter(b -> b.getBeverageName().equals(beverageName))
                .findFirst();
    }

    public List<Beverage> getBeveragesByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage type cannot be null or empty");
        }

        return beverageRepository.findAll().stream()
                .filter(b -> b.getType().equals(type))
                .toList();
    }

    public List<Beverage> getBeveragesByAbvRange(int minAbv, int maxAbv) {
        if (minAbv < 0) {
            throw new IllegalArgumentException("Minimum ABV cannot be negative");
        }
        if (maxAbv < minAbv) {
            throw new IllegalArgumentException("Maximum ABV cannot be less than minimum ABV");
        }

        return beverageRepository.findAll().stream()
                .filter(b -> b.getAbv() >= minAbv && b.getAbv() <= maxAbv)
                .toList();
    }

    public List<Beverage> getBeveragesByTypeAndAbvRange(String type, int minAbv, int maxAbv) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage type cannot be null or empty");
        }
        if (minAbv < 0) {
            throw new IllegalArgumentException("Minimum ABV cannot be negative");
        }
        if (maxAbv < minAbv) {
            throw new IllegalArgumentException("Maximum ABV cannot be less than minimum ABV");
        }

        return beverageRepository.findAll().stream()
                .filter(b -> b.getType().equals(type))
                .filter(b -> b.getAbv() >= minAbv && b.getAbv() <= maxAbv)
                .toList();
    }

    public boolean beverageExists(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage ID cannot be null or empty");
        }
        return beverageRepository.existsById(id);
    }

    public boolean beverageNameExists(String beverageName) {
        if (beverageName == null || beverageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage name cannot be null or empty");
        }

        return beverageRepository.findAll().stream()
                .anyMatch(b -> b.getBeverageName().equals(beverageName));
    }

    public long getTotalBeverageCount() {
        return beverageRepository.count();
    }


    public Beverage updateBeverage(String id, Beverage updatedBeverage) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage ID cannot be null or empty");
        }
        if (updatedBeverage == null) {
            throw new IllegalArgumentException("Updated beverage cannot be null");
        }

        return beverageRepository.findById(id)
                .map(existingBeverage -> {
                    existingBeverage.setBeverageName(updatedBeverage.getBeverageName());
                    existingBeverage.setType(updatedBeverage.getType());
                    existingBeverage.setAbv(updatedBeverage.getAbv());
                    existingBeverage.setDescription(updatedBeverage.getDescription());
                    existingBeverage.setImg_url(updatedBeverage.getImg_url());

                    return beverageRepository.save(existingBeverage);
                })
                .orElseThrow(() -> new IllegalArgumentException("Beverage with ID '" + id + "' not found"));
    }

    public Beverage updateBeverageName(String id, String newName) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage ID cannot be null or empty");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage name cannot be null or empty");
        }

        return beverageRepository.findById(id)
                .map(beverage -> {
                    beverage.setBeverageName(newName);
                    return beverageRepository.save(beverage);
                })
                .orElseThrow(() -> new IllegalArgumentException("Beverage with ID '" + id + "' not found"));
    }

    public Beverage updateBeverageType(String id, String newType) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage ID cannot be null or empty");
        }
        if (newType == null || newType.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage type cannot be null or empty");
        }

        return beverageRepository.findById(id)
                .map(beverage -> {
                    beverage.setType(newType);
                    return beverageRepository.save(beverage);
                })
                .orElseThrow(() -> new IllegalArgumentException("Beverage with ID '" + id + "' not found"));
    }

    public Beverage updateBeverageAbv(String id, int newAbv) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage ID cannot be null or empty");
        }
        if (newAbv < 0) {
            throw new IllegalArgumentException("ABV cannot be negative");
        }

        return beverageRepository.findById(id)
                .map(beverage -> {
                    beverage.setAbv(newAbv);
                    return beverageRepository.save(beverage);
                })
                .orElseThrow(() -> new IllegalArgumentException("Beverage with ID '" + id + "' not found"));
    }

    public Beverage updateBeverageDescription(String id, String newDescription) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage ID cannot be null or empty");
        }
        if (newDescription == null || newDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage description cannot be null or empty");
        }

        return beverageRepository.findById(id)
                .map(beverage -> {
                    beverage.setDescription(newDescription);
                    return beverageRepository.save(beverage);
                })
                .orElseThrow(() -> new IllegalArgumentException("Beverage with ID '" + id + "' not found"));
    }

    public Beverage updateBeverageImageUrl(String id, String newImageUrl) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage ID cannot be null or empty");
        }
        if (newImageUrl == null || newImageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage image URL cannot be null or empty");
        }

        return beverageRepository.findById(id)
                .map(beverage -> {
                    beverage.setImg_url(newImageUrl);
                    return beverageRepository.save(beverage);
                })
                .orElseThrow(() -> new IllegalArgumentException("Beverage with ID '" + id + "' not found"));
    }

    public void deleteBeverage(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Beverage ID cannot be null or empty");
        }

        if (!beverageRepository.existsById(id)) {
            throw new IllegalArgumentException("Beverage with ID '" + id + "' not found");
        }

        beverageRepository.deleteById(id);
    }

    public void deleteAllBeverages() {
        beverageRepository.deleteAll();
    }

    public void deleteBeveragesByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ID list cannot be null or empty");
        }

        beverageRepository.deleteAllById(ids);
    }
}
