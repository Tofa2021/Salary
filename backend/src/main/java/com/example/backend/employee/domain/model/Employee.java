package com.example.backend.employee.domain.model;

import com.example.backend.employee.domain.exception.employee.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
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

    public static Employee hire(
            String personnelNumber,
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
            LocalDate hireDate,
            Long departmentId,
            Long positionId,
            EmploymentType employmentType,
            int dependentChildrenCount,
            boolean disabled
    ) {
        if (personnelNumber == null || personnelNumber.isBlank()) {
            throw new IllegalArgumentException("personnelNumber is required");
        }
        if (surname == null || surname.isBlank()) {
            throw new IllegalArgumentException("surname is required");
        }
        if (hireDate == null) {
            throw new IllegalArgumentException("hireDate is required");
        }
        if (employmentType == null) {
            throw new IllegalArgumentException("employmentType is required");
        }
        if (dependentChildrenCount < 0) {
            throw new IllegalArgumentException("childrenCount >= 0");
        }

        Employee employee = new Employee();
        employee.personnelNumber = personnelNumber;
        employee.surname = surname;
        employee.name = name;
        employee.middleName = middleName;
        employee.passportSeries = passportSeries;
        employee.passportNumber = passportNumber;
        employee.identificationNumber = identificationNumber;
        employee.email = email;
        employee.phone = phone;
        employee.birthDate = birthDate;
        employee.gender = gender;
        employee.hireDate = hireDate;
        employee.departmentId = departmentId;
        employee.positionId = positionId;
        employee.employmentType = employmentType;
        employee.disabled = disabled;
        employee.dependentChildrenCount = dependentChildrenCount;
        return employee;
    }

    public static Employee rehydrate(
            Long id,
            String personnelNumber,
            String surname,
            String name,
            String middleName,
            String passportSeries,
            String passportNumber,
            String identificationNumber,
            LocalDate birthDate,
            LocalDate hireDate,
            LocalDate dismissalDate,
            String email,
            String phone,
            Gender gender,
            Long positionId,
            Long departmentId,
            EmploymentType employmentType,
            int dependentChildrenCount,
            boolean disabled
    ) {
        Employee employee = new Employee();
        employee.id = id;
        employee.personnelNumber = personnelNumber;
        employee.surname = surname;
        employee.name = name;
        employee.middleName = middleName;
        employee.passportSeries = passportSeries;
        employee.passportNumber = passportNumber;
        employee.identificationNumber = identificationNumber;
        employee.birthDate = birthDate;
        employee.hireDate = hireDate;
        employee.dismissalDate = dismissalDate;
        employee.email = email;
        employee.phone = phone;
        employee.gender = gender;
        employee.positionId = positionId;
        employee.departmentId = departmentId;
        employee.employmentType = employmentType;
        employee.dependentChildrenCount = dependentChildrenCount;
        employee.disabled = disabled;
        return employee;
    }


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

    public void ensureCanBeTransfer(Long newDepartmentId) {
        if (departmentId == null) {
            throw new EmployeeWithoutDepartmentException(id);
        }
        if (departmentId.equals(newDepartmentId)) {
            throw new SameDepartmentException(id, newDepartmentId);
        }
    }

    public void transferTo(Long newDepartmentId) {
        ensureNotDismissed();
        ensureCanBeTransfer(newDepartmentId);
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

    public void updatePersonalData(String surname, String name, String middleName) {
        if (surname != null) {
            if (surname.isBlank()) {
                throw new IllegalArgumentException("surname cannot be empty");
            }
            this.surname = surname;
        }
        if (name != null) {
            this.name = name;
        }
        if (middleName != null) {
            this.middleName = middleName;
        }
    }

    public void updateContacts(String email, String phone) {
        if (email != null) {
            this.email = email;
        }
        if (phone != null) {
            this.phone = phone;
        }
    }

    public void updateDocuments(
            String passportSeries,
            String passportNumber,
            String identificationNumber,
            LocalDate birthDate,
            Gender gender
    ) {
        if (passportSeries != null) {
            this.passportSeries = passportSeries;
        }
        if (passportNumber != null) {
            this.passportNumber = passportNumber;
        }
        if (identificationNumber != null) {
            this.identificationNumber = identificationNumber;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (gender != null) {
            this.gender = gender;
        }
    }

    public void updateBenefits(Boolean disabled, Integer dependentChildrenCount) {
        if (disabled != null) {
            this.disabled = disabled;
        }
        if (dependentChildrenCount != null) {
            if (dependentChildrenCount < 0) {
                throw new IllegalArgumentException("childrenCount >= 0");
            }
            this.dependentChildrenCount = dependentChildrenCount;
        }
    }

    public void changeDepartment(Long newDepartmentId) {
        this.departmentId = newDepartmentId;
    }

    public void changePosition(Long newPositionId) {
        this.positionId = newPositionId;
    }

    public void changeEmploymentType(EmploymentType type) {
        if (type == null) {
            throw new IllegalArgumentException("employmentType is required");
        }
        this.employmentType = type;
    }
}
