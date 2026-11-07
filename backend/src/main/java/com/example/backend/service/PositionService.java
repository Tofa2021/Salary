package com.example.backend.service;

import com.example.backend.dto.position.PositionRequest;
import com.example.backend.dto.position.PositionResult;
import com.example.backend.exception.position.PositionNotFoundException;
import com.example.backend.model.employee.Position;
import com.example.backend.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
    private final TariffRateService tariffRateService;

    @Transactional
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

    @Transactional(readOnly = true)
    public List<PositionResult> getAll() {
        return positionRepository.findAll().stream().map(PositionResult::fromPosition).toList();
    }

    @Transactional(readOnly = true)
    public PositionResult getById(Long id) {
        return PositionResult.fromPosition(findById(id));
    }

    private Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException(id));
    }

    @Transactional
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
            position.setQualificationRequirements(request.qualificationRequirements());
        }

        return PositionResult.fromPosition(positionRepository.save(position));
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateBaseSalary(Long positionId, LocalDate date) {
        Position position = findById(positionId);
        BigDecimal baseRate = tariffRateService.getBaseRateOn(date);
        return baseRate.multiply(position.getTariffCoefficient()).setScale(2, RoundingMode.HALF_UP);
    }

    private void checkTariffCategory(int tariffCategory) {
        if (tariffCategory < 1 || tariffCategory > 27) {
            throw new IllegalArgumentException("Tariff category must be between 1 and 27");
        }
    }

    private void checkTariffCoefficient(BigDecimal tariffCoefficient) {
        if (tariffCoefficient.compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("Tariff coefficient cannot be lower than 1");
        }
    }

    @Transactional
    public void delete(Long id) {
        Position position = findById(id);
        positionRepository.delete(position);
    }
}
