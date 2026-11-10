package com.example.backend.shared.dto.employee;

import java.time.LocalDate;

public record CreateEmployeeRequest(
        String personnelNumber,
        String surname,
        String name,
        String middleName,
        String email,
        String phone,
        LocalDate hireDate,
        Long departmentId,
        Long positionId,
        String employmentType
) {
}
