package com.example.backend.payroll.api.dto;

public record ApprovePayrollCommand(Long payrollId, Long approvedBy) {
}
