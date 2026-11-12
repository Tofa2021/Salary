package com.example.backend.department.application;

import com.example.backend.department.DepartmentService;
import com.example.backend.department.api.DepartmentLookup;
import com.example.backend.department.api.dto.DepartmentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentLookupImpl implements DepartmentLookup {
    private final DepartmentService departmentService;

    @Override
    public Optional<DepartmentResult> findById(Long id) {
        return departmentService.findByIdOptional(id);
    }
}
