package com.example.backend.department.exception;

import com.example.backend.shared.exception.NotFoundException;

public class DepartmentNotFoundException extends NotFoundException {
    public DepartmentNotFoundException(Long id) {
        super("Department", "id", String.valueOf(id));
    }
}
