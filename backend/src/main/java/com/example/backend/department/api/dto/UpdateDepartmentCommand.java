package com.example.backend.department.api.dto;

import org.openapitools.jackson.nullable.JsonNullable;

public record UpdateDepartmentCommand(String name, String code, JsonNullable<Long> parentId) {
}
