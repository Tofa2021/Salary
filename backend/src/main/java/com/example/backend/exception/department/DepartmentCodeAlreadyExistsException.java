package com.example.backend.exception.department;

import com.example.backend.exception.AlreadyExistsException;

public class DepartmentCodeAlreadyExistsException extends AlreadyExistsException {
    public DepartmentCodeAlreadyExistsException(String code) {
        super("Department", "code", code);
    }
}
