package com.example.backend.department.exception;

import com.example.backend.shared.exception.ConflictException;

public class DepartmentCycleException extends ConflictException {
    public DepartmentCycleException(Long departmentId, Long parentId) {
        super("Cannot set department " + parentId + " as parent of " + departmentId + ": cycle");
    }
}
