package com.example.backend.dto.department;

public record CreateDepartmentRequest(String name, String code, Long parentId) {
}
