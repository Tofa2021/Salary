package com.example.backend.payroll.infrastructure.web;

import com.example.backend.payroll.api.StatutoryRateFacade;
import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRatePeriodCommand;
import com.example.backend.payroll.api.dto.statutory.CreateStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.StatutoryRateResult;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/api/v1/statutory-rates")
@RequiredArgsConstructor
public class StatutoryRateController {
    private final StatutoryRateFacade facade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StatutoryRateResult create(@RequestBody CreateStatutoryRateCommand command) {
        return facade.create(command);
    }

    @GetMapping("/{id}")
    public StatutoryRateResult get(@PathVariable Long id) {
        return facade.getById(id);
    }

    @GetMapping("/actual")
    public StatutoryRateResult actual(
            @RequestParam String code,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate date
    ) {
        return facade.getActual(code, date);
    }

    @GetMapping
    public PageResult<StatutoryRateResult> list(
            @RequestParam String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return facade.getAll(code, new PageRequest(page, size));
    }

    @PatchMapping("/{id}/rate")
    public StatutoryRateResult changeRate(
            @PathVariable Long id,
            @RequestBody ChangeStatutoryRateCommand command
    ) {
        return facade.changeRate(id, command);
    }

    @PatchMapping("/{id}/period")
    public StatutoryRateResult changePeriod(
            @PathVariable Long id,
            @RequestBody ChangeStatutoryRatePeriodCommand command
    ) {
        return facade.changePeriod(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        facade.delete(id);
    }
}
