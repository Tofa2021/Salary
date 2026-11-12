package com.example.backend.department;

public record CreateDepartmentRequest(String name, String code, Long parentId) {
}
