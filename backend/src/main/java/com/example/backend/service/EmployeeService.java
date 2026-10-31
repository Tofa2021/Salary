package com.example.backend.service;

import com.example.backend.dto.employee.CreateEmployeeRequest;
import com.example.backend.dto.employee.DismissRequest;
import com.example.backend.dto.employee.EmployeeDto;
import com.example.backend.dto.employee.UpdateEmployeeRequest;
import com.example.backend.exception.alreadyExists.EmployeePersonnelNumberAlreadyExistsException;
import com.example.backend.exception.notFound.DepartmentNotFoundException;
import com.example.backend.exception.notFound.EmployeeNotFoundException;
import com.example.backend.exception.notFound.PositionNotFoundException;
import com.example.backend.model.employee.Department;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.employee.EmploymentType;
import com.example.backend.model.employee.Position;
import com.example.backend.repository.DepartmentRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    @Transactional
    public EmployeeDto create(CreateEmployeeRequest request) {
        if (employeeRepository.existsByPersonnelNumber(request.personnelNumber())) {
            throw new EmployeePersonnelNumberAlreadyExistsException(request.personnelNumber());
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(request.departmentId()));
        Position position = positionRepository.findById(request.positionId())
                .orElseThrow(() -> new PositionNotFoundException(request.positionId()));

        Employee employee = new Employee();
        employee.setPersonnelNumber(request.personnelNumber());
        employee.setSurname(request.surname());
        employee.setName(request.name());
        employee.setMiddleName(request.middleName());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setHireDate(request.hireDate());
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setEmploymentType(parseEmploymentType(request.employmentType()));

        return EmployeeDto.fromEmployee(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeDto findById(Long id) {
        return EmployeeDto.fromEmployee(getEmployee(id));
    }

    @Transactional(readOnly = true)
    public EmployeeDto findByPersonnelNumber(String personnelNumber) {
        Employee e = employeeRepository.findByPersonnelNumber(personnelNumber)
                .orElseThrow(() -> new EmployeeNotFoundException(personnelNumber));
        return EmployeeDto.fromEmployee(e);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> findAll(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(EmployeeDto::fromEmployee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> findByDepartment(Long departmentId, Pageable pageable) {
        return employeeRepository.findByDepartmentId(departmentId, pageable)
                .map(EmployeeDto::fromEmployee);
    }

    @Transactional
    public EmployeeDto update(Long id, UpdateEmployeeRequest request) {
        Employee employee = getEmployee(id);

        if (request.surname() != null) employee.setSurname(request.surname());
        if (request.name() != null) employee.setName(request.name());
        if (request.middleName() != null) employee.setMiddleName(request.middleName());
        if (request.email() != null) employee.setEmail(request.email());
        if (request.phone() != null) employee.setPhone(request.phone());
        if (request.hireDate() != null) employee.setHireDate(request.hireDate());
        if (request.dismissalDate() != null) employee.setDismissalDate(request.dismissalDate());
        if (request.departmentId() != null) {
            Department department = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException(request.departmentId()));
            employee.setDepartment(department);
        }
        if (request.positionId() != null) {
            Position position = positionRepository.findById(request.positionId())
                    .orElseThrow(() -> new PositionNotFoundException(request.positionId()));
            employee.setPosition(position);
        }
        if (request.employmentType() != null) {
            employee.setEmploymentType(parseEmploymentType(request.employmentType()));
        }

        return EmployeeDto.fromEmployee(employee);
    }

    @Transactional
    public EmployeeDto dismiss(Long id, DismissRequest request) {
        Employee employee = getEmployee(id);
        if (request.dismissalDate().isBefore(employee.getHireDate())) {
            throw new IllegalArgumentException("dismissalDate cannot be before hireDate");
        }
        employee.setDismissalDate(request.dismissalDate());
        return EmployeeDto.fromEmployee(employee);
    }

    @Transactional
    public EmployeeDto rehire(Long id, LocalDate newHireDate) {
        Employee employee = getEmployee(id);
        employee.setHireDate(newHireDate);
        employee.setDismissalDate(null);
        return EmployeeDto.fromEmployee(employee);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = getEmployee(id);
        employeeRepository.delete(employee);
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private EmploymentType parseEmploymentType(String value) {
        try {
            return EmploymentType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown employmentType: " + value);
        }
    }
}
