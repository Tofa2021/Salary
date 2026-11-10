package com.example.backend.department.exception;

import com.example.backend.shared.exception.ConflictException;

public class DepartmentHasChildrenException extends ConflictException {
    public DepartmentHasChildrenException(Long id, long childCount) {
        super("Department " + id + " has " + childCount + " child departments");
    }
}
