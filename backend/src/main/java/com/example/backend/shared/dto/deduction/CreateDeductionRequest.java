package com.example.backend.shared.dto.deduction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDeductionRequest(
        String type,
        BigDecimal amount,
        LocalDate validFrom,
        LocalDate validTo,
        String documentNumber
) {
}
