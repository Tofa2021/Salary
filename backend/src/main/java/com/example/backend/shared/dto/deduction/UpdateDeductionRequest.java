package com.example.backend.shared.dto.deduction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateDeductionRequest(BigDecimal amount, LocalDate validTo, String documentNumber) {
}
