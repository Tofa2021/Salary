package com.example.backend.employee.api.dto;

public record UpdateEmployeeCommand(
        String surname,
        String name,
        String middleName,
        String email,
        String phone,
        Long departmentId,
        Long positionId,
        String employmentType
) {
}
