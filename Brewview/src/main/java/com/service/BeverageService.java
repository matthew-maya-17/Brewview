package com.service;

import com.dto.BeverageRequestDTO;
import com.dto.ResponseBeverage;
import com.exception.ResourceNotFoundException;
import com.model.Beverage;
import com.repository.BeverageRepository;
import com.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BeverageService {

    private final BeverageRepository beverageRepository;


    public BeverageService(BeverageRepository beverageRepository) {
        this.beverageRepository = beverageRepository;
    }

    public ResponseBeverage createBeverage(BeverageRequestDTO requestDTO) {
        if (requestDTO == null) {
                throw new BadRequestException("Beverage cannot be null");
        }

        Beverage beverage = toEntity(requestDTO);
        beverage.setCreatedAt(LocalDateTime.now());

        Beverage savedBeverage = beverageRepository.save(beverage);
        return toDTO(savedBeverage);
    }

    public List<ResponseBeverage> createBeverages(List<BeverageRequestDTO> requestDTOS) {
        if (requestDTOS == null || requestDTOS.isEmpty()) {
                throw new BadRequestException("Beverage list cannot be null or empty");
        }

        List<Beverage> beverages = toEntityList(requestDTOS);
        beverages.forEach(beverage -> beverage.setCreatedAt(LocalDateTime.now()));

        List<Beverage> savedBeverages = beverageRepository.saveAll(beverages);
        return toDTOList(savedBeverages);
    }


    public List<ResponseBeverage> getAllBeverages() {
        List<Beverage> beverages = beverageRepository.findAll();
        return toDTOList(beverages);
    }

    public ResponseBeverage getBeverageById(UUID id) {
        if (id == null) {
                throw new BadRequestException("Beverage ID cannot be null");
        }

        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        return toDTO(beverage);
    }

    public Optional<ResponseBeverage> getBeverageByName(String beverageName) {

        if (beverageName == null || beverageName.trim().isEmpty()) {
                throw new BadRequestException("Beverage name cannot be null or empty");
        }

        List<Beverage> beverages = beverageRepository.findAll();
        return beverages.stream()
                .filter(b -> b.getBeverageName().equals(beverageName))
                .findFirst()
                .map(this::toDTO);
    }

    public List<ResponseBeverage> getBeveragesByType(String type) {
        if (type == null || type.trim().isEmpty()) {
                throw new BadRequestException("Beverage type cannot be null or empty");
        }

        List<Beverage> beverages = beverageRepository.findAll().stream()
                .filter(b -> b.getType().equals(type))
                .toList();

        return toDTOList(beverages);
    }

    public List<ResponseBeverage> getBeveragesByAbvRange(int minAbv, int maxAbv) {
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

    public List<ResponseBeverage> getBeveragesByTypeAndAbvRange(String type, int minAbv, int maxAbv) {
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


    public ResponseBeverage updateBeverage(UUID id, BeverageRequestDTO requestDTO) {
        if (id == null) {
                throw new BadRequestException("Beverage ID cannot be null");
        }
        if (requestDTO == null) {
                throw new BadRequestException("Updated beverage data cannot be null");
        }

        Beverage existingBeverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        existingBeverage.setBeverageName(requestDTO.getBeverageName());
        existingBeverage.setType(requestDTO.getType());
        existingBeverage.setAbv(requestDTO.getAbv());
        existingBeverage.setDescription(requestDTO.getDescription());
        existingBeverage.setImageUrl(requestDTO.getImageUrl());

        Beverage savedBeverage = beverageRepository.save(existingBeverage);
        return toDTO(savedBeverage);
    }

    public ResponseBeverage updateBeverageName(UUID id, String newName) {
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

    public ResponseBeverage updateBeverageType(UUID id, String newType) {
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

    public ResponseBeverage updateBeverageAbv(UUID id, int newAbv) {
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

    public ResponseBeverage updateBeverageDescription(UUID id, String newDescription) {
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

    public ResponseBeverage updateBeverageImageUrl(UUID id, String newImageUrl) {
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

    private ResponseBeverage toDTO(Beverage beverage) {
        if (beverage == null) {
            return null;
        }

        return new ResponseBeverage(
                beverage.getId(),
                beverage.getBeverageName(),
                beverage.getType(),
                beverage.getAbv(),
                beverage.getDescription(),
                beverage.getImageUrl(),
                beverage.getCreatedAt()
        );
    }

    private Beverage toEntity(BeverageRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        return new Beverage(
                null,
                requestDTO.getBeverageName(),
                requestDTO.getType(),
                requestDTO.getAbv(),
                requestDTO.getDescription(),
                requestDTO.getImageUrl(),
                null
        );
    }

    private List<ResponseBeverage> toDTOList(List<Beverage> beverages) {
        if (beverages == null) {
            return List.of();
        }

        return beverages.stream()
                .map(this::toDTO)
                .toList();
    }

    private List<Beverage> toEntityList(List<BeverageRequestDTO> requestDTOs) {
        if (requestDTOs == null) {
            return List.of();
        }

        return requestDTOs.stream()
                .map(this::toEntity)
                .toList();
    }
}
