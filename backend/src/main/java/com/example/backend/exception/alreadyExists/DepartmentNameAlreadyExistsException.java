package com.example.backend.exception.alreadyExists;

public class DepartmentNameAlreadyExistsException extends DepartmentAlreadyExistsException {
    public DepartmentNameAlreadyExistsException(String name) {
        super("name", name);
    }
}
