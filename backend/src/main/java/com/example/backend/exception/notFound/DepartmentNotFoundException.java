package com.example.backend.exception.notFound;

public class DepartmentNotFoundException extends NotFoundException {
    public DepartmentNotFoundException(Long id) {
        super("Department", "id", String.valueOf(id));
    }
}
