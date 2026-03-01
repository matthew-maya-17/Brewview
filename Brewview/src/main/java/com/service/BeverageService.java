package com.service;

import com.dto.BeverageRequestDTO;
import com.dto.ResponseBeverage;
import com.exception.ResourceConflictException;
import com.exception.ResourceNotFoundException;
import com.model.Beverage;
import com.repository.BeverageRepository;
import com.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BeverageService {

    private final BeverageRepository beverageRepository;


    public BeverageService(BeverageRepository beverageRepository) {
        this.beverageRepository = beverageRepository;
    }

    public ResponseBeverage createBeverage(BeverageRequestDTO requestDTO) {
        if (requestDTO == null) {
                throw new BadRequestException("Beverage cannot be null");
        }

        if (beverageRepository.existsByBeverageNameAndTypeAndAbv(
                requestDTO.getBeverageName(),
                requestDTO.getType(),
                requestDTO.getAbv())) {
            throw new ResourceConflictException(
                    "Beverage with name '" + requestDTO.getBeverageName() +
                            "', type '" + requestDTO.getType() +
                            "', and ABV " + requestDTO.getAbv() + " already exists"
            );
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

        long distinctCount = requestDTOS.stream()
                .map(dto -> dto.getBeverageName() + "|" + dto.getType() + "|" + dto.getAbv())
                .distinct()
                .count();

        if (distinctCount != requestDTOS.size()) {
            throw new ResourceConflictException("Duplicate beverages (same name, type, and ABV) found in request");
        }

        for (BeverageRequestDTO dto : requestDTOS) {
            if (beverageRepository.existsByBeverageNameAndTypeAndAbv(
                    dto.getBeverageName(),
                    dto.getType(),
                    dto.getAbv())) {
                throw new ResourceConflictException(
                        "Beverage with name '" + dto.getBeverageName() +
                                "', type '" + dto.getType() +
                                "', and ABV " + dto.getAbv() + " already exists"
                );
            }
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

    public ResponseBeverage getBeverageByName(String beverageName) {

        if (beverageName == null || beverageName.trim().isEmpty()) {
            throw new BadRequestException("Beverage name cannot be null or empty");
        }

        if (beverageName.trim().length() < 6) {
            throw new BadRequestException("Beverage name must be at least 6 characters long");
        }

        Beverage beverage = beverageRepository.findByBeverageName(beverageName)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with name: " + beverageName));

        return toDTO(beverage);
    }

    public List<ResponseBeverage> getBeveragesByType(String type) {
        if (type == null || type.trim().isEmpty()) {
                throw new BadRequestException("Beverage type cannot be null or empty");
        }

        List<Beverage> beverages = beverageRepository.findAll().stream()
                .filter(b -> b.getType().equalsIgnoreCase(type))
                .toList();

        return toDTOList(beverages);
    }

    public List<ResponseBeverage> getBeveragesByAbvRange(BigDecimal minAbv, BigDecimal maxAbv) {
        if (minAbv == null || minAbv.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Minimum ABV cannot be negative");
        }
        if (maxAbv == null || maxAbv.compareTo(minAbv) < 0) {
                throw new BadRequestException("Maximum ABV cannot be less than minimum ABV");
        }

        List<Beverage> beverages = beverageRepository.findByAbvBetween(minAbv, maxAbv);

        return toDTOList(beverages);
    }

    public List<ResponseBeverage> getBeveragesByTypeAndAbvRange(String type, BigDecimal minAbv, BigDecimal maxAbv) {
        if (type == null || type.trim().isEmpty()) {
                throw new BadRequestException("Beverage type cannot be null or empty");
        }
        if (minAbv == null || minAbv.compareTo(BigDecimal.ZERO) < 0) {
                throw new BadRequestException("Minimum ABV cannot be negative");
        }
        if (maxAbv == null || maxAbv.compareTo(minAbv) < 0) {
                throw new BadRequestException("Maximum ABV cannot be less than minimum ABV");
        }


        List<Beverage> beverages = beverageRepository.findAll().stream()
                .filter(b -> b.getType().equalsIgnoreCase(type))
                .filter(b -> b.getAbv().compareTo(minAbv) >= 0 && b.getAbv().compareTo(maxAbv) <= 0)
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
            return false;
        }

        return beverageRepository.existsByBeverageName(beverageName);
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

        boolean isNameChanged = !existingBeverage.getBeverageName().equals(requestDTO.getBeverageName());
        boolean isTypeChanged = !existingBeverage.getType().equals(requestDTO.getType());
        boolean isAbvChanged = existingBeverage.getAbv().compareTo(requestDTO.getAbv()) != 0;

        if (isNameChanged || isTypeChanged || isAbvChanged) {
            Optional<Beverage> duplicate = beverageRepository.findByBeverageNameAndTypeAndAbv(
                    requestDTO.getBeverageName(),
                    requestDTO.getType(),
                    requestDTO.getAbv()
            );

            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new ResourceConflictException(
                        "Beverage with name '" + requestDTO.getBeverageName() +
                                "', type '" + requestDTO.getType() +
                                "', and ABV " + requestDTO.getAbv() + " already exists"
                );
            }
        }

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
        if (newName.trim().length() < 6) {
            throw new BadRequestException("Beverage name must be at least 6 characters long");
        }

        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        if (!beverage.getBeverageName().equals(newName)) {
            Optional<Beverage> duplicate = beverageRepository.findByBeverageNameAndTypeAndAbv(
                    newName,
                    beverage.getType(),
                    beverage.getAbv()
            );

            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new ResourceConflictException(
                        "Beverage with name '" + newName +
                                "', type '" + beverage.getType() +
                                "', and ABV " + beverage.getAbv() + " already exists"
                );
            }
        }

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

        if (!beverage.getType().equals(newType)) {
            Optional<Beverage> duplicate = beverageRepository.findByBeverageNameAndTypeAndAbv(
                    beverage.getBeverageName(),
                    newType,
                    beverage.getAbv()
            );

            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new ResourceConflictException(
                        "Beverage with name '" + beverage.getBeverageName() +
                                "', type '" + newType +
                                "', and ABV " + beverage.getAbv() + " already exists"
                );
            }
        }

        beverage.setType(newType);
        Beverage savedBeverage = beverageRepository.save(beverage);
        return toDTO(savedBeverage);
    }

    public ResponseBeverage updateBeverageAbv(UUID id, BigDecimal newAbv) {
        if (id == null) {
            throw new BadRequestException("Beverage ID cannot be null or empty");
        }
        if (newAbv == null || newAbv.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("ABV cannot be negative");
        }

        Beverage beverage = beverageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beverage not found with id: " + id));

        if (beverage.getAbv().compareTo(newAbv) != 0) {
            Optional<Beverage> duplicate = beverageRepository.findByBeverageNameAndTypeAndAbv(
                    beverage.getBeverageName(),
                    beverage.getType(),
                    newAbv
            );

            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new ResourceConflictException(
                        "Beverage with name '" + beverage.getBeverageName() +
                                "', type '" + beverage.getType() +
                                "', and ABV " + newAbv + " already exists"
                );
            }
        }

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
        if (newDescription.trim().length() < 25) {
            throw new BadRequestException("Beverage description must be at least 25 characters long");
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
