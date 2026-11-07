package com.example.backend.exception.alreadyExists;

public class DepartmentCodeAlreadyExistsException extends DepartmentAlreadyExistsException {
    public DepartmentCodeAlreadyExistsException(String code) {
        super("code", code);
    }
}
