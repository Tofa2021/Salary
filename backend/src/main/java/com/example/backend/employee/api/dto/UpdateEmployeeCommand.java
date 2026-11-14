package com.example.backend.employee.api.dto;

import com.example.backend.employee.domain.model.Gender;

import java.time.LocalDate;

public record UpdateEmployeeCommand(
        String surname,
        String name,
        String middleName,
        String passportSeries,
        String passportNumber,
        String identificationNumber,
        String email,
        String phone,
        Gender gender,
        LocalDate birthDate,
        Long departmentId,
        Long positionId,
        String employmentType,
        Boolean disabled,
        Integer childrenCount
) {
}
