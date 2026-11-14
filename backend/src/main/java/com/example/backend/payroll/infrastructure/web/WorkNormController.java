package com.example.backend.payroll.infrastructure.web;

import com.example.backend.payroll.api.WorkNormFacade;
import com.example.backend.payroll.api.dto.worknorm.UpsertWorkNormCommand;
import com.example.backend.payroll.api.dto.worknorm.WorkNormResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/work-norms")
@RequiredArgsConstructor
public class WorkNormController {
    private final WorkNormFacade facade;

    @PutMapping("/{year}/{month}")
    public WorkNormResult upsert(
            @PathVariable int year,
            @PathVariable int month,
            @RequestBody UpsertWorkNormCommand command
    ) {
        if (command.year() != year || command.month() != month) {
            throw new IllegalArgumentException("path year/month must match body");
        }
        return facade.upsert(command);
    }

    @GetMapping("/{year}/{month}")
    public WorkNormResult get(@PathVariable int year, @PathVariable int month) {
        return facade.getByYearAndMonth(year, month);
    }

    @DeleteMapping("/{year}/{month}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int year, @PathVariable int month) {
        facade.delete(year, month);
    }
}
