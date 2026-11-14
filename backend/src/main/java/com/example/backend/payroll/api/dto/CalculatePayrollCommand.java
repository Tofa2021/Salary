package com.example.backend.payroll.api.dto;

import com.example.backend.payroll.domain.shared.model.Period;

public record CalculatePayrollCommand(Long employeeId, Period period) {
}
