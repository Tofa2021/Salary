package com.example.backend.payroll.infrastructure.web.dto;

public record CalculatePayrollRequest(Long employeeId, int month, int year) {
}
