package com.example.backend.department.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class DepartmentSelfParentException extends InvalidStateException {
    public DepartmentSelfParentException(Long id) {
        super("Department " + id + " cannot be its own parent");
    }
}
