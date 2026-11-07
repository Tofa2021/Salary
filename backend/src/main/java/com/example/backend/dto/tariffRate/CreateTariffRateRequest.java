package com.example.backend.dto.tariffRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTariffRateRequest(BigDecimal baseRate, LocalDate effectiveFrom, String legalAct) {
}
