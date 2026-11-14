package com.example.backend.employee.application.lookup;

import com.example.backend.employee.api.EmployeeLookup;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.application.service.EmployeeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeLookupImpl implements EmployeeLookup {
    private final EmployeeQueryService employeeQueryService;

    @Override
    public Optional<EmployeeResult> findById(Long id) {
        return employeeQueryService.findByIdOptional(id);
    }

    @Override
    public boolean existsByDepartmentId(Long departmentId) {
        return employeeQueryService.existsByDepartmentId(departmentId);
    }
}
