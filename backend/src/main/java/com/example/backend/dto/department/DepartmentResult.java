package com.example.backend.dto.department;

import com.example.backend.model.employee.Department;
import com.example.backend.model.employee.Employee;

import java.util.List;

public record DepartmentResult(Long id, String name, String code, Long parentId, List<Long> employeeIds) {
    public static DepartmentResult fromDepartment(Department department) {
        return new DepartmentResult(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.getParent().getId(),
                department.getEmployees().stream().map(Employee::getId).toList()
        );
    }
}
