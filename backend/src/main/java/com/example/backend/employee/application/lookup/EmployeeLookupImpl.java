package com.example.backend.employee.application.lookup;

import com.example.backend.employee.api.EmployeeLookup;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.application.service.EmployeeService;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeLookupImpl implements EmployeeLookup {
    private final EmployeeService employeeService;

    @Override
    public Optional<EmployeeResult> findById(Long id) {
        try {
            return Optional.of(employeeService.getById(id));
        } catch (EmployeeNotFoundException e) {
            return Optional.empty();
        }
    }
}
