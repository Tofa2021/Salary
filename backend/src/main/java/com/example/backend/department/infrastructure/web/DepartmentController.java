package com.example.backend.department.infrastructure.web;

import com.example.backend.department.DepartmentService;
import com.example.backend.department.api.dto.CreateDepartmentCommand;
import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.department.api.dto.UpdateDepartmentCommand;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResult create(@RequestBody CreateDepartmentCommand command) {
        return departmentService.create(command);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DepartmentResult getById(@PathVariable Long id) {
        return departmentService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResult<DepartmentResult> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var result = departmentService.getAll(PageRequest.of(page, size));
        return PageResult.of(result.items(), page, size, result.totalElements());
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DepartmentResult update(@PathVariable Long id, @RequestBody UpdateDepartmentCommand command) {
        return departmentService.update(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        departmentService.delete(id);
    }
}
