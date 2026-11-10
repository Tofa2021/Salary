package com.example.backend.shared.dto.tariffRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTariffRateRequest(BigDecimal baseRate, LocalDate effectiveFrom, String legalAct) {
}
