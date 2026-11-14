package com.example.backend.payroll.api.dto.rate;

import java.math.BigDecimal;

public record UpdateTariffRateAmountCommand(BigDecimal amount, String currency) {
}
