package com.example.backend.position;

import com.example.backend.payroll.api.TariffRateLookup;
import com.example.backend.position.api.dto.PositionCommand;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final SpringDataPositionRepository springDataPositionRepository;
    private final TariffRateLookup tariffRateLookup;

    @Transactional
    public PositionResult create(PositionCommand request) {
        checkTariffCategory(request.tariffCategory());
        checkTariffCoefficient(request.tariffCoefficient());

        Position position = new Position(
                request.name(),
                request.tariffCategory(),
                request.tariffCoefficient(),
                request.qualificationRequirements()
        );

        return toResult(springDataPositionRepository.save(position));
    }

    @Transactional(readOnly = true)
    public List<PositionResult> getAll() {
        return springDataPositionRepository.findAll().stream().map(this::toResult).toList();
    }

    @Transactional(readOnly = true)
    public PositionResult getById(Long id) {
        return toResult(findById(id));
    }

    @Transactional(readOnly = true)
    public Optional<PositionResult> findByIdOptional(Long id) {
        return springDataPositionRepository.findById(id).map(this::toResult);
    }

    private Position findById(Long id) {
        return springDataPositionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException(id));
    }

    @Transactional
    public PositionResult update(Long id, PositionCommand request) {
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

        return toResult(springDataPositionRepository.save(position));
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateBaseSalary(Long positionId, LocalDate date) {
        Position position = findById(positionId);
        BigDecimal baseRate = tariffRateLookup.getBaseRateOnDate(date);
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
        springDataPositionRepository.delete(position);
    }

    private PositionResult toResult(Position position) {
        return new PositionResult(
                position.getId(),
                position.getName(),
                position.getTariffCategory(),
                position.getTariffCoefficient(),
                position.getQualificationRequirements()
        );
    }
}
