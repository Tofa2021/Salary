package com.example.backend.timesheet.infrastructure.web;

import com.example.backend.auth.api.CurrentUser;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import com.example.backend.timesheet.api.TimeSheetFacade;
import com.example.backend.timesheet.api.dto.CreateTimeSheetCommand;
import com.example.backend.timesheet.api.dto.TimeSheetEntryCommand;
import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.api.dto.TimeSheetSummaryResult;
import com.example.backend.timesheet.infrastructure.web.dto.GetByPeriodTimeSheetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timesheets")
@RequiredArgsConstructor
public class TimeSheetController {
    private final TimeSheetFacade timeSheetFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TimeSheetResult create(@RequestBody CreateTimeSheetCommand command) {
        return timeSheetFacade.create(command);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TimeSheetResult getById(@PathVariable Long id) {
        return timeSheetFacade.getById(id);
    }

    @GetMapping("/by-employee/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public PageResult<TimeSheetResult> getByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return timeSheetFacade.getByEmployee(employeeId, PageRequest.of(page, size));
    }

    @GetMapping("/by-period")
    @ResponseStatus(HttpStatus.OK)
    public PageResult<TimeSheetResult> getByPeriod(
            @RequestBody GetByPeriodTimeSheetRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return timeSheetFacade.getByPeriod(request.start(), request.end(), PageRequest.of(page, size));
    }

    @GetMapping("/hours-summaries")
    @ResponseStatus(HttpStatus.OK)
    public List<TimeSheetSummaryResult> getSummaries(@RequestBody GetByPeriodTimeSheetRequest request) {
        return timeSheetFacade.findHoursSummaries(request.start(), request.end());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<TimeSheetResult> approve(@PathVariable Long id, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CurrentUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(timeSheetFacade.approve(id, user.userId()));
    }

    @PostMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public TimeSheetResult reject(@PathVariable Long id) {
        return timeSheetFacade.reject(id);
    }

    @PostMapping("/{id}/submit")
    @ResponseStatus(HttpStatus.OK)
    public TimeSheetResult submit(@PathVariable Long id) {
        return timeSheetFacade.submit(id);
    }

    @PostMapping("/{id}/entries")
    @ResponseStatus(HttpStatus.OK)
    public TimeSheetResult addEntry(@PathVariable Long id, @RequestBody TimeSheetEntryCommand command) {
        return timeSheetFacade.addEntry(id, command);
    }

    @DeleteMapping("/{id}/entries/{entryId}")
    @ResponseStatus(HttpStatus.OK)
    public TimeSheetResult removeEntry(@PathVariable Long id, @PathVariable Long entryId) {
        return timeSheetFacade.removeEntry(id, entryId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        timeSheetFacade.delete(id);
    }
}
