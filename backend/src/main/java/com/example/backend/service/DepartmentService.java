package com.example.backend.service;


import com.example.backend.dto.department.DepartmentRequest;
import com.example.backend.dto.department.DepartmentResult;
import com.example.backend.exception.notFound.DepartmentNotFoundException;
import com.example.backend.model.employee.Department;
import com.example.backend.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentResult create(DepartmentRequest request) {
        Department parentDepartment = null;
        if (request.parentId() != null) {
            parentDepartment = findById(request.parentId());
        }

        Department department = new Department(request.name(), request.code(), parentDepartment, List.of());
        return DepartmentResult.fromDepartment(departmentRepository.save(department));
    }

    public DepartmentResult getById(Long id) {
        return DepartmentResult.fromDepartment(findById(id));
    }

    private Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    public List<DepartmentResult> getAll() {
        return departmentRepository.findAll().stream().map(DepartmentResult::fromDepartment).toList();
    }

    public DepartmentResult update(Long id, DepartmentRequest request) {
        Department department = findById(id);

        if (request.name() != null) {
            department.setName(request.name());
        }

        if (request.code() != null) {
            department.setCode(request.code());
        }

        if (request.parentId() != null) {
            Department parentDepartment = findById(request.parentId());
            department.setParent(parentDepartment);
        }

        return DepartmentResult.fromDepartment(departmentRepository.save(department));
    }

    public void delete(Long id) {
        Department department = findById(id);
        departmentRepository.delete(department);
    }
}
