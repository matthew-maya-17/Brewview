package com.service;

import com.dto.BeverageDTO;
import com.exception.ResourceNotFoundException;
import com.model.Beverage;
import com.repository.BeverageRepository;
import com.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BeverageService {

    private final BeverageRepository beverageRepository;


    public BeverageService(BeverageRepository beverageRepository) {
        this.beverageRepository = beverageRepository;
    }

    public BeverageDTO createBeverage(BeverageDTO beverageDTO) {
        if (beverageDTO == null) {
                throw new BadRequestException("Beverage cannot be null");
        }

        Beverage beverage = toEntity(beverageDTO);
        beverage.setCreatedAt(LocalDateTime.now());

        Beverage savedBeverage = beverageRepository.save(beverage);
        return toDTO(savedBeverage);
    }

    public List<BeverageDTO> createBeverages(List<BeverageDTO> beverageDTOS) {
        if (beverageDTOS == null || beverageDTOS.isEmpty()) {
                throw new BadRequestException("Beverage list cannot be null or empty");
        }

        List<Beverage> beverages = toEntityList(beverageDTOS);
        beverages.forEach(beverage -> beverage.setCreatedAt(LocalDateTime.now()));

        List<Beverage> savedBeverages = beverageRepository.saveAll(beverages);
        return toDTOList(savedBeverages);
    }


    public List<BeverageDTO> getAllBeverages() {
        List<Beverage> beverages = beverageRepository.findAll();
        return toDTOList(beverages);
    }

    public BeverageDTO getBeverageById(UUID id) {
        if (id == null) {
                throw new BadRequestException("Beverage ID cannot be null");
        }

        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        return toDTO(beverage);
    }

    public Optional<BeverageDTO> getBeverageByName(String beverageName) {

        if (beverageName == null || beverageName.trim().isEmpty()) {
                throw new BadRequestException("Beverage name cannot be null or empty");
        }

        List<Beverage> beverages = beverageRepository.findAll();
        return beverages.stream()
                .filter(b -> b.getBeverageName().equals(beverageName))
                .findFirst()
                .map(this::toDTO);
    }

    public List<BeverageDTO> getBeveragesByType(String type) {
        if (type == null || type.trim().isEmpty()) {
                throw new BadRequestException("Beverage type cannot be null or empty");
        }

        List<Beverage> beverages = beverageRepository.findAll().stream()
                .filter(b -> b.getType().equals(type))
                .toList();

        return toDTOList(beverages);
    }

    public List<BeverageDTO> getBeveragesByAbvRange(int minAbv, int maxAbv) {
        if (minAbv < 0) {
                throw new BadRequestException("Minimum ABV cannot be negative");
        }
        if (maxAbv < minAbv) {
                throw new BadRequestException("Maximum ABV cannot be less than minimum ABV");
        }

        List<Beverage> beverages = beverageRepository.findAll().stream()
                .filter(b -> b.getAbv() >= minAbv && b.getAbv() <= maxAbv)
                .toList();

        return toDTOList(beverages);
    }

    public List<BeverageDTO> getBeveragesByTypeAndAbvRange(String type, int minAbv, int maxAbv) {
        if (type == null || type.trim().isEmpty()) {
                throw new BadRequestException("Beverage type cannot be null or empty");
        }
        if (minAbv < 0) {
                throw new BadRequestException("Minimum ABV cannot be negative");
        }
        if (maxAbv < minAbv) {
                throw new BadRequestException("Maximum ABV cannot be less than minimum ABV");
        }


        List<Beverage> beverages = beverageRepository.findAll().stream()
                .filter(b -> b.getType().equals(type))
                .filter(b -> b.getAbv() >= minAbv && b.getAbv() <= maxAbv)
                .toList();

        return toDTOList(beverages);
    }

    public boolean beverageExists(UUID id) {
        if (id == null) {
            throw new BadRequestException("Beverage ID cannot be null or empty");
        }
        return beverageRepository.existsById(id);
    }

    public boolean beverageNameExists(String beverageName) {
        if (beverageName == null || beverageName.trim().isEmpty()) {
                throw new BadRequestException("Beverage name cannot be null or empty");
        }

        return beverageRepository.findAll().stream()
                .anyMatch(b -> b.getBeverageName().equals(beverageName));
    }

    public long getTotalBeverageCount() {
        return beverageRepository.count();
    }


    public BeverageDTO updateBeverage(UUID id, BeverageDTO updatedBeverageDTO) {
        if (id == null) {
                throw new BadRequestException("Beverage ID cannot be null");
        }
        if (updatedBeverageDTO == null) {
                throw new BadRequestException("Updated beverage data cannot be null");
        }

        Beverage existingBeverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        existingBeverage.setBeverageName(updatedBeverageDTO.getBeverageName());
        existingBeverage.setType(updatedBeverageDTO.getType());
        existingBeverage.setAbv(updatedBeverageDTO.getAbv());
        existingBeverage.setDescription(updatedBeverageDTO.getDescription());
        existingBeverage.setImageUrl(updatedBeverageDTO.getImageUrl());

        Beverage savedBeverage = beverageRepository.save(existingBeverage);
        return toDTO(savedBeverage);
    }

    public BeverageDTO  updateBeverageName(UUID id, String newName) {
        if (id == null) {
                throw new BadRequestException("Beverage ID cannot be null or empty");
        }
        if (newName == null || newName.trim().isEmpty()) {
                throw new BadRequestException("Beverage name cannot be null or empty");
        }

        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        beverage.setBeverageName(newName);
        Beverage savedBeverage = beverageRepository.save(beverage);
        return toDTO(savedBeverage);
    }

    public BeverageDTO updateBeverageType(UUID id, String newType) {
        if (id == null) {
            throw new BadRequestException("Beverage ID cannot be null or empty");
        }
        if (newType == null || newType.trim().isEmpty()) {
            throw new BadRequestException("Beverage type cannot be null or empty");
        }

        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        beverage.setType(newType);
        Beverage savedBeverage = beverageRepository.save(beverage);
        return toDTO(savedBeverage);
    }

    public BeverageDTO updateBeverageAbv(UUID id, int newAbv) {
        if (id == null) {
            throw new BadRequestException("Beverage ID cannot be null or empty");
        }
        if (newAbv < 0) {
            throw new BadRequestException("ABV cannot be negative");
        }

        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        beverage.setAbv(newAbv);
        Beverage savedBeverage = beverageRepository.save(beverage);
        return toDTO(savedBeverage);
    }

    public BeverageDTO updateBeverageDescription(UUID id, String newDescription) {
        if (id == null) {
            throw new BadRequestException("Beverage ID cannot be null or empty");
        }
        if (newDescription == null || newDescription.trim().isEmpty()) {
            throw new BadRequestException("Beverage description cannot be null or empty");
        }

        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        beverage.setDescription(newDescription);
        Beverage savedBeverage = beverageRepository.save(beverage);
        return toDTO(savedBeverage);
    }

    public BeverageDTO updateBeverageImageUrl(UUID id, String newImageUrl) {
        if (id == null) {
            throw new BadRequestException("Beverage ID cannot be null or empty");
        }
        if (newImageUrl == null || newImageUrl.trim().isEmpty()) {
            throw new BadRequestException("Beverage image URL cannot be null or empty");
        }

        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        beverage.setImageUrl(newImageUrl);
        Beverage savedBeverage = beverageRepository.save(beverage);
        return toDTO(savedBeverage);
    }

    public void deleteBeverage(UUID id) {
        if (id == null) {
            throw new BadRequestException("Beverage ID cannot be null or empty");
        }

        if (!beverageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Beverage not found with id: " + id);
        }

        beverageRepository.deleteById(id);
    }

    public void deleteAllBeverages() {
        beverageRepository.deleteAll();
    }

    public void deleteBeveragesByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("ID list cannot be null or empty");
        }

        beverageRepository.deleteAllById(ids);
    }

    private BeverageDTO toDTO(Beverage beverage) {
        if (beverage == null) {
            return null;
        }

        return new BeverageDTO(
                beverage.getId(),
                beverage.getBeverageName(),
                beverage.getType(),
                beverage.getAbv(),
                beverage.getDescription(),
                beverage.getImageUrl(),
                beverage.getCreatedAt()
        );
    }

    private Beverage toEntity(BeverageDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Beverage(
                dto.getId(),
                dto.getBeverageName(),
                dto.getType(),
                dto.getAbv(),
                dto.getDescription(),
                dto.getImageUrl(),
                dto.getCreatedAt()
        );
    }

    private List<BeverageDTO> toDTOList(List<Beverage> beverages) {
        if (beverages == null) {
            return List.of();
        }

        return beverages.stream()
                .map(this::toDTO)
                .toList();
    }

    private List<Beverage> toEntityList(List<BeverageDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }

        return dtos.stream()
                .map(this::toEntity)
                .toList();
    }
}
