package com.example.backend.exception.department;

import com.example.backend.exception.NotFoundException;

public class DepartmentNotFoundException extends NotFoundException {
    public DepartmentNotFoundException(Long id) {
        super("Department", "id", String.valueOf(id));
    }
}
