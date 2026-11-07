package com.example.backend.service;

import com.example.backend.dto.employee.CreateEmployeeRequest;
import com.example.backend.dto.employee.DismissRequest;
import com.example.backend.dto.employee.EmployeeResult;
import com.example.backend.dto.employee.UpdateEmployeeRequest;
import com.example.backend.exception.BusinessException;
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
    public EmployeeResult create(CreateEmployeeRequest request) {
        if (employeeRepository.existsByPersonnelNumber(request.personnelNumber())) {
            throw new EmployeePersonnelNumberAlreadyExistsException(request.personnelNumber());
        }

        Department department = findDepartment(request.departmentId());
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

        return EmployeeResult.fromEmployee(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public EmployeeResult getById(Long id) {
        return EmployeeResult.fromEmployee(findEmployee(id));
    }

    @Transactional(readOnly = true)
    public EmployeeResult getByPersonnelNumber(String personnelNumber) {
        Employee e = employeeRepository.findByPersonnelNumber(personnelNumber)
                .orElseThrow(() -> new EmployeeNotFoundException(personnelNumber));
        return EmployeeResult.fromEmployee(e);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResult> getAll(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(EmployeeResult::fromEmployee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResult> getByDepartment(Long departmentId, Pageable pageable) {
        return employeeRepository.findByDepartmentId(departmentId, pageable)
                .map(EmployeeResult::fromEmployee);
    }

    @Transactional
    public EmployeeResult update(Long id, UpdateEmployeeRequest request) {
        Employee employee = findEmployee(id);

        if (request.surname() != null) {
            employee.setSurname(request.surname());
        }
        if (request.name() != null) {
            employee.setName(request.name());
        }
        if (request.middleName() != null) {
            employee.setMiddleName(request.middleName());
        }
        if (request.email() != null) {
            employee.setEmail(request.email());
        }
        if (request.phone() != null) {
            employee.setPhone(request.phone());
        }
        if (request.hireDate() != null) {
            employee.setHireDate(request.hireDate());
        }
        if (request.dismissalDate() != null) {
            employee.setDismissalDate(request.dismissalDate());
        }
        if (request.departmentId() != null) {
            Department department = findDepartment(request.departmentId());
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

        return EmployeeResult.fromEmployee(employee);
    }

    @Transactional
    public EmployeeResult dismiss(Long id, DismissRequest request) {
        Employee employee = findEmployee(id);
        if (request.dismissalDate().isBefore(employee.getHireDate())) {
            throw new IllegalArgumentException("dismissalDate cannot be before hireDate");
        }
        employee.setDismissalDate(request.dismissalDate());
        return EmployeeResult.fromEmployee(employee);
    }

    @Transactional
    public EmployeeResult rehire(Long id, LocalDate newHireDate) {
        Employee employee = findEmployee(id);
        employee.setHireDate(newHireDate);
        employee.setDismissalDate(null);
        return EmployeeResult.fromEmployee(employee);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = findEmployee(id);
        employeeRepository.delete(employee);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    private EmploymentType parseEmploymentType(String value) {
        try {
            return EmploymentType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown employmentType: " + value);
        }
    }

    public void transfer(Long newDepartmentId, Long employeeId) {
        Employee employee = findEmployee(employeeId);

        if (employee.getDismissalDate() != null) {
            throw new BusinessException("Employee dismissed"); // TODO BadRequest
        }

        Department newDepartment = findDepartment(newDepartmentId);
        Department oldDepartment = employee.getDepartment();

        oldDepartment.getEmployees().remove(employee);
        newDepartment.getEmployees().add(employee);

        employeeRepository.save(employee);
        departmentRepository.save(oldDepartment);
        departmentRepository.save(newDepartment);
    }

    public void removeFromDepartment(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (employee.getDismissalDate() != null) {
            throw new BusinessException("Employee dismissed"); // TODO BadRequest
        }

        Department department = employee.getDepartment();
        if (department == null) {
            throw new BusinessException("Employee does not have department");
        }

        department.getEmployees().remove(employee);
        employee.setDepartment(null);

        employeeRepository.save(employee);
        departmentRepository.save(department);
    }

    public void addToDepartment(Long id, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (employee.getDismissalDate() != null) {
            throw new BusinessException("Employee dismissed"); // TODO BadRequest
        }

        Department oldDepartment = employee.getDepartment();
        if (oldDepartment != null) {
            throw new BusinessException("Employee already in department");
        }

        Department newDepartment = findDepartment(id);

        newDepartment.getEmployees().add(employee);
        employee.setDepartment(newDepartment);

        employeeRepository.save(employee);
        departmentRepository.save(newDepartment);
    }
}
