package com.example.backend.exception.department;

import com.example.backend.exception.AlreadyExistsException;

public class DepartmentNameAlreadyExistsException extends AlreadyExistsException {
    public DepartmentNameAlreadyExistsException(String name) {
        super("Department", "name", name);
    }
}
