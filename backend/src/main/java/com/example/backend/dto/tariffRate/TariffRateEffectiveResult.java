package com.example.backend.dto.tariffRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TariffRateEffectiveResult(Long id, LocalDate date, BigDecimal baseRate, String legalAct) {
}
