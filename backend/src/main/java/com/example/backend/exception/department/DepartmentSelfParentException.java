package com.example.backend.exception.department;

import com.example.backend.exception.InvalidStateException;

public class DepartmentSelfParentException extends InvalidStateException {
    public DepartmentSelfParentException(Long id) {
        super("Department " + id + " cannot be its own parent");
    }
}
