package com.example.backend.exception.department;

import com.example.backend.exception.InvalidStateException;

public class DepartmentCycleException extends InvalidStateException {
    public DepartmentCycleException(Long departmentId, Long parentId) {
        super("Cannot set department " + parentId + " as parent of " + departmentId + ": cycle");
    }
}
