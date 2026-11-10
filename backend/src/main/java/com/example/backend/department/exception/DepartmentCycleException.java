package com.example.backend.department.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class DepartmentCycleException extends InvalidStateException {
    public DepartmentCycleException(Long departmentId, Long parentId) {
        super("Cannot set department " + parentId + " as parent of " + departmentId + ": cycle");
    }
}
