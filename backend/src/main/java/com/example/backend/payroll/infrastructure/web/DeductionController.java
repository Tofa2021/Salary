package com.example.backend.payroll.infrastructure.web;

import com.example.backend.payroll.api.DeductionFacade;
import com.example.backend.payroll.api.dto.deduction.ChangeDeductionPeriodCommand;
import com.example.backend.payroll.api.dto.deduction.ChangeDeductionValueCommand;
import com.example.backend.payroll.api.dto.deduction.CreateDeductionCommand;
import com.example.backend.payroll.api.dto.deduction.DeductionResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/api/v1/deductions")
@RequiredArgsConstructor
public class DeductionController {
    private final DeductionFacade facade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeductionResultDto create(@RequestBody CreateDeductionCommand command) {
        return facade.create(command);
    }

    @GetMapping("/{id}")
    public DeductionResultDto getById(@PathVariable Long id) {
        return facade.getById(id);
    }

    @GetMapping
    public List<DeductionResultDto> getAll(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate date
    ) {
        return facade.getByEmployee(employeeId, date);
    }

    @PatchMapping("/{id}/value")
    public DeductionResultDto changeValue(
            @PathVariable Long id,
            @RequestBody ChangeDeductionValueCommand command
    ) {
        return facade.changeValue(id, command);
    }

    @PatchMapping("/{id}/period")
    public DeductionResultDto changePeriod(
            @PathVariable Long id,
            @RequestBody ChangeDeductionPeriodCommand command
    ) {
        return facade.changePeriod(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        facade.delete(id);
    }
}
