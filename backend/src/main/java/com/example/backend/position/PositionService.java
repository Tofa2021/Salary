package com.example.backend.position;

import com.example.backend.payroll.service.TariffRateService;
import com.example.backend.position.api.dto.PositionResult;
import com.example.backend.position.exception.InvalidPositionNameException;
import com.example.backend.position.exception.InvalidTariffCategoryException;
import com.example.backend.position.exception.InvalidTariffCoefficientException;
import com.example.backend.position.exception.PositionNotFoundException;
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
                throw new InvalidPositionNameException(request.name());
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
            throw new InvalidTariffCategoryException(tariffCategory);
        }
    }

    private void checkTariffCoefficient(BigDecimal tariffCoefficient) {
        if (tariffCoefficient.compareTo(BigDecimal.ONE) < 0) {
            throw new InvalidTariffCoefficientException(tariffCoefficient);
        }
    }

    @Transactional
    public void delete(Long id) {
        Position position = findById(id);
        positionRepository.delete(position);
    }
}
