package com.example.backend.department.api.dto;

public record CreateDepartmentCommand(String name, String code, Long parentId) {
}
