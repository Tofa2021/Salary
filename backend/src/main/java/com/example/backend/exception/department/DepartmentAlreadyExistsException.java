package com.example.backend.exception.department;

import com.example.backend.exception.AlreadyExistsException;

public class DepartmentAlreadyExistsException extends AlreadyExistsException {
    public DepartmentAlreadyExistsException(String idName, String id) {
        super("Department", idName, id);
    }
}
