package com.example.backend.department.exception;

import com.example.backend.shared.exception.AlreadyExistsException;

public class DepartmentNameAlreadyExistsException extends AlreadyExistsException {
    public DepartmentNameAlreadyExistsException(String name) {
        super("Department", "name", name);
    }
}
