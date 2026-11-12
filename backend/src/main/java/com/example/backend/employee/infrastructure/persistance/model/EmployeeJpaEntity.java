package com.example.backend.employee.infrastructure.persistance.model;

import com.example.backend.employee.domain.model.EmploymentType;
import com.example.backend.employee.domain.model.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "employees")
public class EmployeeJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "personnel_number", nullable = false, unique = true)
    private String personnelNumber;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "passport_series")
    private String passportSeries;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "identification_number")
    private String identificationNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "dismissal_date")
    private LocalDate dismissalDate;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "department_id")
    private Long departmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType;

    @Column(name = "dependent_children_count")
    private int dependentChildrenCount;

    @Column(name = "disabled")
    private boolean disabled;
}
