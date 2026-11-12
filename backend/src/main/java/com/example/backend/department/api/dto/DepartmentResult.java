package com.example.backend.department.api.dto;

import com.example.backend.department.Department;

public record DepartmentResult(Long id, String name, String code, Long parentId) {
    public static DepartmentResult fromDepartment(Department department) {
        return new DepartmentResult(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.getParent() == null ? null : department.getParent().getId()
        );
    }
}
