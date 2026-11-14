package com.example.backend.payroll.infrastructure.web;

import com.example.backend.payroll.api.TariffRateFacade;
import com.example.backend.payroll.api.dto.rate.CreateTariffRateCommand;
import com.example.backend.payroll.api.dto.rate.TariffRateResult;
import com.example.backend.payroll.api.dto.rate.UpdateTariffRateAmountCommand;
import com.example.backend.payroll.api.dto.rate.UpdateTariffRatePeriodCommand;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

@RestController
@RequestMapping("/api/v1/tariff-rates")
@RequiredArgsConstructor
public class TariffRateController {
    private final TariffRateFacade facade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TariffRateResult create(@RequestBody CreateTariffRateCommand command) {
        return facade.create(command);
    }

    @GetMapping("/{id}")
    public TariffRateResult get(@PathVariable Long id) {
        return facade.getById(id);
    }

    @GetMapping
    public PageResult<TariffRateResult> list(
            @RequestParam Long positionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return facade.getByPosition(positionId, new PageRequest(page, size));
    }

    @GetMapping("/actual")
    public TariffRateResult actual(
            @RequestParam Long positionId,
            @RequestParam @DateTimeFormat(iso = DATE) LocalDate date
    ) {
        return facade.getActual(positionId, date);
    }

    @PatchMapping("/{id}/amount")
    public TariffRateResult changeAmount(
            @PathVariable Long id,
            @RequestBody UpdateTariffRateAmountCommand cmd
    ) {
        return facade.changeAmount(id, cmd);
    }

    @PatchMapping("/{id}/period")
    public TariffRateResult changePeriod(
            @PathVariable Long id,
            @RequestBody UpdateTariffRatePeriodCommand cmd
    ) {
        return facade.changePeriod(id, cmd);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        facade.delete(id);
    }
}
