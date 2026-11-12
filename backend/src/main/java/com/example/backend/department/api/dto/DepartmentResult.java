package com.example.backend.department.api.dto;

import com.example.backend.department.Department;
import com.example.backend.employee.domain.model.Employee;

import java.util.List;

public record DepartmentResult(Long id, String name, String code, Long parentId, List<Long> employeeIds) {
    public static DepartmentResult fromDepartment(Department department) {
        return new DepartmentResult(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.getParent() == null ? null : department.getParent().getId(),
                department.getEmployees().stream().map(Employee::getId).toList()
        );
    }
}
