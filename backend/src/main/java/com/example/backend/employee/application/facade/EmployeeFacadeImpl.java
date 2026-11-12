package com.example.backend.employee.application.facade;

import com.example.backend.employee.api.EmployeeFacade;
import com.example.backend.employee.api.dto.CreateEmployeeCommand;
import com.example.backend.employee.api.dto.DismissCommand;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.api.dto.UpdateEmployeeCommand;
import com.example.backend.employee.application.service.EmployeeService;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EmployeeFacadeImpl implements EmployeeFacade {
    private final EmployeeService employeeService;

    @Override
    @Transactional
    public EmployeeResult create(CreateEmployeeCommand request) {
        return employeeService.create(request);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResult getById(Long id) {
        return employeeService.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResult getByPersonnelNumber(String personnelNumber) {
        return employeeService.getByPersonnelNumber(personnelNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<EmployeeResult> getAll(PageRequest pageRequest) {
        return employeeService.getAll(pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<EmployeeResult> getByDepartment(Long departmentId, PageRequest pageRequest) {
        return employeeService.getByDepartment(departmentId, pageRequest);
    }

    @Override
    @Transactional
    public EmployeeResult update(Long id, UpdateEmployeeCommand request) {
        return employeeService.update(id, request);
    }

    @Override
    @Transactional
    public EmployeeResult dismiss(Long id, DismissCommand request) {
        return employeeService.dismiss(id, request);
    }

    @Override
    @Transactional
    public EmployeeResult rehire(Long id, LocalDate newHireDate) {
        return employeeService.rehire(id, newHireDate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        employeeService.delete(id);
    }

    @Override
    @Transactional
    public void transfer(Long newDepartmentId, Long employeeId) {
        employeeService.transfer(newDepartmentId, employeeId);
    }

    @Override
    @Transactional
    public void removeFromDepartment(Long employeeId) {
        employeeService.removeFromDepartment(employeeId);
    }

    @Override
    @Transactional
    public void addToDepartment(Long departmentId, Long employeeId) {
        employeeService.addToDepartment(departmentId, employeeId);
    }
}
