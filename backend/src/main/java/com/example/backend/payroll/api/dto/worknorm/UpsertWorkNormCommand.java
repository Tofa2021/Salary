package com.example.backend.payroll.api.dto.worknorm;

import java.math.BigDecimal;

public record UpsertWorkNormCommand(
        int year,
        int month,
        BigDecimal normHours
) {
}
