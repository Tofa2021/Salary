package com.example.backend.service;

import com.example.backend.dto.position.PositionRequest;
import com.example.backend.dto.position.PositionResult;
import com.example.backend.exception.notFound.PositionNotFoundException;
import com.example.backend.model.employee.Position;
import com.example.backend.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;

    public PositionResult create(PositionRequest request) {
        checkTariffCategory(request.tariffCategory());
        checkTariffCoefficient(request.tariffCoefficient());

        Position position = new Position(
                request.name(),
                request.tariffCategory(),
                request.tariffCoefficient(),
                request.qualificationRequirements()
        );

        return PositionResult.fromPosition(positionRepository.save(position));
    }

    public List<PositionResult> getAll() {
        return positionRepository.findAll().stream().map(PositionResult::fromPosition).toList();
    }

    public PositionResult getById(Long id) {
        return PositionResult.fromPosition(findById(id));
    }

    private Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException(id));
    }

    public PositionResult update(Long id, PositionRequest request) {
        Position position = findById(id);

        if (request.name() != null) {
            if (request.name().isBlank()) {
                throw new IllegalArgumentException("New position name cannot be blank");
            }
            position.setName(request.name());
        }

        if (request.tariffCategory() != null) {
            checkTariffCategory(request.tariffCategory());
            position.setTariffCategory(request.tariffCategory());
        }

        if (request.tariffCoefficient() != null) {
            checkTariffCoefficient(request.tariffCoefficient());
            position.setTariffCoefficient(request.tariffCoefficient());
        }

        if (request.qualificationRequirements() != null) {
            if (request.qualificationRequirements().isBlank()) {
                throw new IllegalArgumentException("New position qualification requirements cannot be blank");
            }
            position.setQualificationRequirements(request.qualificationRequirements());
        }

        return PositionResult.fromPosition(positionRepository.save(position));
    }

    private boolean isTariffCategoryValid(int tariffCategory) {
        return tariffCategory < 1 || tariffCategory > 27;
    }

    private void checkTariffCategory(int tariffCategory) {
        if (!isTariffCategoryValid(tariffCategory)) {
            throw new IllegalArgumentException("Tariff category cannot be lower than 1 or higher than 27");
        }
    }

    private boolean isTariffCoefficientValid(BigDecimal tariffCoefficient) {
        return tariffCoefficient.compareTo(BigDecimal.ONE) < 1;
    }

    private void checkTariffCoefficient(BigDecimal tariffCoefficient) {
        if (isTariffCoefficientValid(tariffCoefficient)) {
            throw new IllegalArgumentException("Tariff coefficient cannot be lower than 1");
        }
    }

    public void delete(Long id) {
        Position position = findById(id);
        positionRepository.delete(position);
    }
}
