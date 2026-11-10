package com.example.backend.shared.dto.department;

public record CreateDepartmentRequest(String name, String code, Long parentId) {
}
