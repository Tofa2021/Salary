package com.example.backend.payroll.api.dto.statutory;

import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;

public record ChangeStatutoryRateCommand(Percentage rate, Money threshold) {
}
