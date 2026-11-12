package com.example.backend.employee.domain.model;

import com.example.backend.employee.domain.exception.employee.*;
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

    public void dismiss(LocalDate date) {
        if (date.isBefore(hireDate)) {
            throw new DismissalDateBeforeHireDateException(hireDate, date);
        }
        this.dismissalDate = date;
    }

    public void ensureNotDismissed() {
        if (dismissalDate != null) {
            throw new EmployeeDismissedException(id);
        }
    }

    public void transferTo(Long newDepartmentId) {
        ensureNotDismissed();
        if (departmentId == null) {
            throw new EmployeeWithoutDepartmentException(id);
        }
        if (departmentId.equals(newDepartmentId)) {
            throw new SameDepartmentException(id, newDepartmentId);
        }
        this.departmentId = newDepartmentId;
    }

    public void removeFromDepartment() {
        ensureNotDismissed();
        if (this.departmentId == null) {
            throw new EmployeeWithoutDepartmentException(id);
        }
        this.departmentId = null;
    }

    public void addToDepartment(Long departmentId) {
        ensureCanBeAddedToDepartment();
        this.departmentId = departmentId;
    }

    public void rehire(LocalDate newHireDate) {
        hireDate = newHireDate;
        dismissalDate = null;
    }

    public void ensureCanBeAddedToDepartment() {
        ensureNotDismissed();
        if (this.departmentId != null) {
            throw new EmployeeAlreadyInDepartmentException(id);
        }
    }
}
