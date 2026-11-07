package com.example.backend.exception.department;

import com.example.backend.exception.ConflictException;

public class DepartmentHasChildrenException extends ConflictException {
    public DepartmentHasChildrenException(Long id, long childCount) {
        super("Department " + id + " has " + childCount + " child departments");
    }
}
