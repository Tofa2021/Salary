package com.example.backend.employee.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Employee {
    private Long id;

    private String personnelNumber;
    private String surname;
    private String name;
    private String middleName;

    private String passportSeries;
    private String passportNumber;
    private String identificationNumber;

    private LocalDate birthDate;
    private LocalDate hireDate;
    private LocalDate dismissalDate;

    private String email;
    private String phone;

    private Gender gender;

    private Long positionId;

    private Long departmentId;

    private EmploymentType employmentType;

    private int dependentChildrenCount;
    private boolean disabled;
}
