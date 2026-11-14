package com.example.backend.employee.api;

import com.example.backend.employee.api.dto.CreateEmployeeCommand;
import com.example.backend.employee.api.dto.DismissCommand;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.api.dto.UpdateEmployeeCommand;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

import java.time.LocalDate;

public interface EmployeeFacade {
    EmployeeResult create(CreateEmployeeCommand request);

    EmployeeResult getById(Long id);

    EmployeeResult getByPersonnelNumber(String personnelNumber);

    PageResult<EmployeeResult> getAll(PageRequest pageRequest);

    PageResult<EmployeeResult> getByDepartment(Long departmentId, PageRequest pageRequest);

    EmployeeResult update(Long id, UpdateEmployeeCommand request);

    EmployeeResult dismiss(Long id, DismissCommand request);

    EmployeeResult rehire(Long id, LocalDate newHireDate);

    void delete(Long id);

    void transfer(Long newDepartmentId, Long employeeId);

    void removeFromDepartment(Long employeeId);

    void addToDepartment(Long departmentId, Long employeeId);
}
