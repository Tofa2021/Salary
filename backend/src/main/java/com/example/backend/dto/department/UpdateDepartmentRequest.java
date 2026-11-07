package com.example.backend.dto.department;

import org.openapitools.jackson.nullable.JsonNullable;

public record UpdateDepartmentRequest(String name, String code, JsonNullable<Long> parentId) {
}
