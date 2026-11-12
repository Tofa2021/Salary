package com.example.backend.employee.application.lookup;

import com.example.backend.employee.api.EmploymentContractLookup;
import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.application.service.EmploymentContractService;
import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmploymentContractLookupImpl implements EmploymentContractLookup {
    private final EmploymentContractService employmentContractService;

    @Override
    @Transactional(readOnly = true)
    public Optional<EmploymentContractResult> findById(Long id) {
        try {
            return Optional.of(employmentContractService.getById(id));
        } catch (EmploymentContractNotFoundException e) {
            return Optional.empty();
        }
    }
}
