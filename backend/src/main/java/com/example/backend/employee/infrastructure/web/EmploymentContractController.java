package com.example.backend.employee.infrastructure.web;

import com.example.backend.employee.api.EmploymentContractFacade;
import com.example.backend.employee.api.dto.CreateEmploymentContractCommand;
import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.api.dto.UpdateEmploymentContractCommand;
import com.example.backend.employee.infrastructure.web.dto.ExtendContractRequest;
import com.example.backend.employee.infrastructure.web.dto.TerminateContractRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee-contracts")
@RequiredArgsConstructor
public class EmploymentContractController {
    private final EmploymentContractFacade employmentContractFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmploymentContractResult create(@RequestBody CreateEmploymentContractCommand command) {
        return employmentContractFacade.create(command);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmploymentContractResult getById(@PathVariable Long id) {
        return employmentContractFacade.getById(id);
    }

    @GetMapping("/by-employee/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public List<EmploymentContractResult> getByEmployee(@PathVariable Long employeeId) {
        return employmentContractFacade.findByEmployee(employeeId);
    }

    @PostMapping("/{id}/extend")
    @ResponseStatus(HttpStatus.OK)
    public EmploymentContractResult extend(@PathVariable Long id, @RequestBody ExtendContractRequest request) {
        return employmentContractFacade.extend(id, request.newEndDate());
    }

    @PostMapping("/{id}/terminate")
    @ResponseStatus(HttpStatus.OK)
    public EmploymentContractResult terminate(@PathVariable Long id, @RequestBody TerminateContractRequest request) {
        return employmentContractFacade.terminate(id, request.terminationDate());
    }

    @GetMapping("/expiring")
    @ResponseStatus(HttpStatus.OK)
    public List<EmploymentContractResult> findExpiring(@RequestParam int days) {
        return employmentContractFacade.findExpiring(days);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public EmploymentContractResult findActiveMainOnDate(
            @RequestParam Long employeeId,
            @RequestParam LocalDate date
    ) {
        return employmentContractFacade.findActiveMainOnDate(employeeId, date);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmploymentContractResult update(@PathVariable Long id, @RequestBody UpdateEmploymentContractCommand command) {
        return employmentContractFacade.update(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employmentContractFacade.delete(id);
    }
}
