package com.example.backend.payroll.domain.statutory.exception;

import com.example.backend.shared.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Map;

public class StatutoryRateNotFoundException extends NotFoundException {
    public StatutoryRateNotFoundException(Long id) {
        super("StatutoryRate", "id", String.valueOf(id));
    }

    public StatutoryRateNotFoundException(String code, LocalDate date) {
        super("StatutoryRate", Map.of("code", String.valueOf(code), "date", String.valueOf(date)));
    }
}
