package com.example.backend.department.api;

import com.example.backend.department.api.dto.DepartmentResult;

import java.util.Optional;

public interface DepartmentLookup {
    Optional<DepartmentResult> findById(Long id);
}
