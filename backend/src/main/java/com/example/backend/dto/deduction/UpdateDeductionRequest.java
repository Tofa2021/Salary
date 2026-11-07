package com.example.backend.dto.deduction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateDeductionRequest(BigDecimal amount, LocalDate validTo, String documentNumber) {
}
