package com.example.backend.employee.infrastructure.web;

import com.example.backend.employee.api.EmployeeFacade;
import com.example.backend.employee.api.dto.CreateEmployeeCommand;
import com.example.backend.employee.api.dto.DismissCommand;
import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.api.dto.UpdateEmployeeCommand;
import com.example.backend.employee.infrastructure.web.dto.AddToDepartmentRequest;
import com.example.backend.employee.infrastructure.web.dto.RehireRequest;
import com.example.backend.employee.infrastructure.web.dto.TransferRequest;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeFacade employeeFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResult create(@RequestBody CreateEmployeeCommand command) {
        return employeeFacade.create(command);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResult getById(@PathVariable Long id) {
        return employeeFacade.getById(id);
    }

    @GetMapping("/by-personnel-number/{number}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResult getByPersonnelNumber(@PathVariable String number) {
        return employeeFacade.getByPersonnelNumber(number);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResult<EmployeeResult> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var result = employeeFacade.getAll(PageRequest.of(page, size));
        return PageResult.of(result.items(), page, size, result.totalElements());
    }

    @GetMapping("/by-department/{departmentId}")
    @ResponseStatus(HttpStatus.OK)
    public PageResult<EmployeeResult> getByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var result = employeeFacade.getByDepartment(departmentId, PageRequest.of(page, size));
        return PageResult.of(result.items(), result.page(), result.size(), result.totalElements());
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResult update(@PathVariable Long id, @RequestBody UpdateEmployeeCommand command) {
        return employeeFacade.update(id, command);
    }

    @PostMapping("/{id}/dismiss")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResult dismiss(@PathVariable Long id, @RequestBody DismissCommand command) {
        return employeeFacade.dismiss(id, command);
    }

    @PostMapping("/{id}/rehire")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResult rehire(@PathVariable Long id, @RequestBody RehireRequest request) {
        return employeeFacade.rehire(id, request.newHireDate());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeFacade.delete(id);
    }

    @PostMapping("/{id}/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@PathVariable Long id, @RequestBody TransferRequest request) {
        employeeFacade.transfer(request.newDepartmentId(), id);
    }

    @DeleteMapping("/{id}/department")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromDepartment(@PathVariable Long id) {
        employeeFacade.removeFromDepartment(id);
    }

    @PostMapping("/{id}/department")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addToDepartment(@PathVariable Long id, @RequestBody AddToDepartmentRequest request) {
        employeeFacade.addToDepartment(request.departmentId(), id);
    }
}
