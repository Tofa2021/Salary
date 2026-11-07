package com.example.backend.dto.department;

public record DepartmentRequest(String name, String code, Long parentId) {
}
