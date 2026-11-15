package com.example.backend.payroll.domain.base.exception;

import com.example.backend.shared.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Map;

public class TariffRateNotFoundException extends NotFoundException {
    public TariffRateNotFoundException(Long id) {
        super("TariffRate", "id", String.valueOf(id));
    }

    public TariffRateNotFoundException(Long positionId, LocalDate date) {
        super("TariffRate", Map.of(
                "positionId", String.valueOf(positionId),
                "date", String.valueOf(date)
        ));
    }
}
