package com.example.backend.controller;

import com.example.backend.dto.department.DepartmentRequest;
import com.example.backend.dto.department.DepartmentResult;
import com.example.backend.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    private ResponseEntity<List<DepartmentResult>> getAll() {
        return ResponseEntity.ok(departmentService.getAll());
    }

    @GetMapping("/{id}")
    private ResponseEntity<DepartmentResult> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @PostMapping
    private ResponseEntity<DepartmentResult> create(@RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.create(request));
    }
}
