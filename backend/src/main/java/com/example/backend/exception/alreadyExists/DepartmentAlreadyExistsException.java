package com.example.backend.exception.alreadyExists;

public class DepartmentAlreadyExistsException extends AlreadyExistsException {
    public DepartmentAlreadyExistsException(String idName, String id) {
        super("Department", idName, id);
    }
}
