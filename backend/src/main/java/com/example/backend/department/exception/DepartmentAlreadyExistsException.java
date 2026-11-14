package com.example.backend.department.exception;

import com.example.backend.shared.exception.AlreadyExistsException;

public class DepartmentAlreadyExistsException extends AlreadyExistsException {
    public DepartmentAlreadyExistsException(String idName, String id) {
        super("Department", idName, id);
    }
}
