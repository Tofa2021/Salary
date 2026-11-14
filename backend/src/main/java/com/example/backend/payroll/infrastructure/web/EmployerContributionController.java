package com.example.backend.payroll.infrastructure.web;

import com.example.backend.payroll.api.EmployerContributionFacade;
import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionPeriodCommand;
import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionRateCommand;
import com.example.backend.payroll.api.dto.employer.CreateEmployerContributionCommand;
import com.example.backend.payroll.api.dto.employer.EmployerContributionResult;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/api/v1/employer-contributions")
@RequiredArgsConstructor
public class EmployerContributionController {
    private final EmployerContributionFacade facade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployerContributionResult create(@RequestBody CreateEmployerContributionCommand command) {
        return facade.create(command);
    }

    @GetMapping("/{id}")
    public EmployerContributionResult get(@PathVariable Long id) {
        return facade.getById(id);
    }

    @GetMapping("/active")
    public List<EmployerContributionResult> getAllActiveOnDate(@RequestParam @DateTimeFormat(iso = DATE) LocalDate date) {
        return facade.getAllActiveOnDate(date);
    }

    @GetMapping
    public PageResult<EmployerContributionResult> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return facade.getAll(new PageRequest(page, size));
    }

    @PatchMapping("/{id}/rate")
    public EmployerContributionResult changeRate(
            @PathVariable Long id,
            @RequestBody ChangeEmployerContributionRateCommand cmd
    ) {
        return facade.changeRate(id, cmd);
    }

    @PatchMapping("/{id}/period")
    public EmployerContributionResult changePeriod(
            @PathVariable Long id,
            @RequestBody ChangeEmployerContributionPeriodCommand cmd
    ) {
        return facade.changePeriod(id, cmd);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        facade.delete(id);
    }
}
