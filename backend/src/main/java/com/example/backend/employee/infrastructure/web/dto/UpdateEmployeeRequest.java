package com.example.backend.employee.infrastructure.web.dto;

public record UpdateEmployeeRequest(
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
