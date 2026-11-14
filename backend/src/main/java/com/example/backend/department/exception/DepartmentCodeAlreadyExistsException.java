package com.example.backend.department.exception;

import com.example.backend.shared.exception.AlreadyExistsException;

public class DepartmentCodeAlreadyExistsException extends AlreadyExistsException {
    public DepartmentCodeAlreadyExistsException(String code) {
        super("Department", "code", code);
    }
}
