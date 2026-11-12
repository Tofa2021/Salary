package com.example.backend.employee.application.service;

import com.example.backend.department.api.DepartmentLookup;
import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.employee.api.dto.CreateEmployeeCommand;
import com.example.backend.employee.api.dto.DismissCommand;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.api.dto.UpdateEmployeeCommand;
import com.example.backend.employee.domain.exception.employee.EmployeeInvalidDepartmentException;
import com.example.backend.employee.domain.exception.employee.EmployeeInvalidPositionException;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.exception.employee.EmployeePersonnelNumberAlreadyExistsException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentType;
import com.example.backend.employee.domain.port.EmployeeRepository;
import com.example.backend.position.api.PositionLookup;
import com.example.backend.position.api.dto.PositionResult;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentLookup departmentLookup;
    private final PositionLookup positionLookup;

    public EmployeeResult create(CreateEmployeeCommand command) { // FIXME промотреть комманду передавать childrenCount disable и паспортные данные
        if (employeeRepository.existsByPersonnelNumber(command.personnelNumber())) {
            throw new EmployeePersonnelNumberAlreadyExistsException(command.personnelNumber());
        }

        DepartmentResult departmentResult = findDepartmentResult(command.departmentId());
        PositionResult positionResult = findPositionResult(command.positionId());

        Employee employee = Employee.hire(
                command.personnelNumber(),
                command.surname(),
                command.name(),
                command.middleName(),
                command.email(),
                command.phone(),
                command.hireDate(),
                departmentResult.id(),
                positionResult.id(),
                EmploymentType.from(command.employmentType())
        );

        return toResult(employeeRepository.save(employee));
    }

    public EmployeeResult getById(Long id) {
        return toResult(findEmployee(id));
    }

    public EmployeeResult getByPersonnelNumber(String personnelNumber) {
        Employee employee = employeeRepository.findByPersonnelNumber(personnelNumber)
                .orElseThrow(() -> new EmployeeNotFoundException(personnelNumber));
        return toResult(employee);
    }

    public PageResult<EmployeeResult> getAll(PageRequest pageRequest) {
        return employeeRepository.findAll(pageRequest).map(this::toResult);
    }

    public PageResult<EmployeeResult> getByDepartment(Long departmentId, PageRequest pageRequest) {
        return employeeRepository.findByDepartmentId(departmentId, pageRequest)
                .map(this::toResult);
    }

    public EmployeeResult update(Long id, UpdateEmployeeCommand command) {
        Employee employee = findEmployee(id);

        employee.updatePersonalData(command.surname(), command.name(), command.middleName());
        employee.updateContacts(command.email(), command.phone());

        if (command.departmentId() != null) {
            employee.changeDepartment(findDepartmentResult(command.departmentId()).id());
        }
        if (command.positionId() != null) {
            employee.changePosition(findPositionResult(command.positionId()).id());
        }
        if (command.employmentType() != null) {
            employee.changeEmploymentType(EmploymentType.from(command.employmentType()));
        }

        employeeRepository.save(employee);

        return toResult(employee);
    }

    public EmployeeResult dismiss(Long id, DismissCommand request) {
        Employee employee = findEmployee(id);
        employee.dismiss(request.dismissalDate());
        employeeRepository.save(employee);
        return toResult(employee);
    }

    public EmployeeResult rehire(Long id, LocalDate newHireDate) {
        Employee employee = findEmployee(id);
        employee.rehire(newHireDate);
        employeeRepository.save(employee);
        return toResult(employee);
    }

    public void delete(Long id) {
        Employee employee = findEmployee(id);
        employeeRepository.delete(employee);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private DepartmentResult findDepartmentResult(Long id) {
        return departmentLookup.findById(id)
                .orElseThrow(() -> new EmployeeInvalidDepartmentException(id));
    }

    private PositionResult findPositionResult(Long id) {
        return positionLookup.findById(id)
                .orElseThrow(() -> new EmployeeInvalidPositionException(id));
    }

    public void transfer(Long newDepartmentId, Long employeeId) {
        Employee employee = findEmployee(employeeId);
        employee.transferTo(newDepartmentId);
        employeeRepository.save(employee);
    }

    public void removeFromDepartment(Long employeeId) {
        Employee employee = findEmployee(employeeId);
        employee.removeFromDepartment();
        employeeRepository.save(employee);
    }

    public void addToDepartment(Long departmentId, Long employeeId) {
        Employee employee = findEmployee(employeeId);
        employee.ensureCanBeAddedToDepartment();
        DepartmentResult newDepartmentResult = findDepartmentResult(departmentId);
        employee.addToDepartment(newDepartmentResult.id());
        employeeRepository.save(employee);
    }

    private EmployeeResult toResult(Employee employee) {
        return new EmployeeResult(
                employee.getId(),
                employee.getPersonnelNumber(),
                employee.getSurname(),
                employee.getName(),
                employee.getMiddleName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getHireDate(),
                employee.getDismissalDate(),
                employee.getEmploymentType() == null ? null : employee.getEmploymentType().name(),
                employee.getDepartmentId(),
                employee.getPositionId()
        );
    }
}
