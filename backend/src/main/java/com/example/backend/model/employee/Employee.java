package com.example.backend.model.employee;

import com.example.backend.model.payroll.PayrollRecord;
import com.example.backend.model.timesheet.TimeSheetEntry;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    private int dependentChildrenCount;
    private boolean disabled;

    @OneToMany(mappedBy = "employee")
    private List<EmploymentContract> contracts;

    @OneToMany(mappedBy = "employee")
    private List<PayrollRecord> payrollRecords;

    @OneToMany(mappedBy = "employee")
    private List<TimeSheetEntry> timeSheetEntries;
}
