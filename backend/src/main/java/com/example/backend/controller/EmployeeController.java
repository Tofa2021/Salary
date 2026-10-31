package com.example.backend.controller;

import com.example.backend.dto.employee.CreateEmployeeRequest;
import com.example.backend.dto.employee.DismissRequest;
import com.example.backend.dto.employee.EmployeeDto;
import com.example.backend.dto.employee.UpdateEmployeeRequest;
import com.example.backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeDto> create(@RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'ANALYST')")
    public ResponseEntity<Page<EmployeeDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(employeeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'ANALYST')")
    public ResponseEntity<EmployeeDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @GetMapping("/by-personnel-number/{pn}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT')")
    public ResponseEntity<EmployeeDto> findByPersonnelNumber(@PathVariable String pn) {
        return ResponseEntity.ok(employeeService.findByPersonnelNumber(pn));
    }

    @GetMapping("/by-department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'ACCOUNTANT', 'ANALYST')")
    public ResponseEntity<Page<EmployeeDto>> findByDepartment(@PathVariable Long departmentId, Pageable pageable) {
        return ResponseEntity.ok(employeeService.findByDepartment(departmentId, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeDto> update(@PathVariable Long id, @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @PatchMapping("/{id}/dismiss")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeDto> dismiss(@PathVariable Long id, @RequestBody DismissRequest request) {
        return ResponseEntity.ok(employeeService.dismiss(id, request));
    }

    @PatchMapping("/{id}/rehire")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDto> rehire(@PathVariable Long id, @RequestParam LocalDate hireDate) {
        return ResponseEntity.ok(employeeService.rehire(id, hireDate));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
