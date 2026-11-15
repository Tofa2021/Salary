package com.example.backend.payroll.api.dto.payroll;

public record ApprovePayrollCommand(Long payrollId, Long approvedBy) {
}
