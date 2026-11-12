package com.example.backend.employee.application.facade;

import com.example.backend.employee.api.EmploymentContractFacade;
import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.application.service.EmploymentContractService;
import com.example.backend.employee.infrastructure.web.dto.CreateEmploymentContractRequest;
import com.example.backend.employee.infrastructure.web.dto.UpdateEmploymentContractRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmploymentContractFacadeImpl implements EmploymentContractFacade {

    private final EmploymentContractService employmentContractService;

    @Override
    @Transactional
    public EmploymentContractResult create(Long employeeId, CreateEmploymentContractRequest request) {
        return employmentContractService.create(employeeId, request);
    }

    @Override
    @Transactional(readOnly = true)
    public EmploymentContractResult getById(Long id) {
        return employmentContractService.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmploymentContractResult> findByEmployee(Long employeeId) {
        return employmentContractService.findByEmployee(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public EmploymentContractResult findActiveMainOnDate(Long employeeId, LocalDate date) {
        return employmentContractService.findActiveMainOnDate(employeeId, date);
    }

    @Override
    @Transactional
    public EmploymentContractResult update(Long id, UpdateEmploymentContractRequest request) {
        return employmentContractService.update(id, request);
    }

    @Override
    @Transactional
    public EmploymentContractResult terminate(Long id, LocalDate terminationDate) {
        return employmentContractService.terminate(id, terminationDate);
    }

    @Override
    @Transactional
    public EmploymentContractResult extend(Long id, LocalDate newEndDate) {
        return employmentContractService.extend(id, newEndDate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        employmentContractService.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmploymentContractResult> findExpiring(int days) {
        return employmentContractService.findExpiring(days);
    }
}
