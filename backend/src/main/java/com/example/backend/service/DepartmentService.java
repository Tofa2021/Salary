package com.example.backend.service;


import com.example.backend.dto.department.CreateDepartmentRequest;
import com.example.backend.dto.department.DepartmentResult;
import com.example.backend.dto.department.UpdateDepartmentRequest;
import com.example.backend.exception.department.*;
import com.example.backend.model.employee.Department;
import com.example.backend.repository.DepartmentRepository;
import com.example.backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public DepartmentResult create(CreateDepartmentRequest request) {
        if (departmentRepository.existsByName(request.name())) {
            throw new DepartmentNameAlreadyExistsException(request.name());
        }

        if (departmentRepository.existsByCode(request.code())) {
            throw new DepartmentCodeAlreadyExistsException(request.code());
        }

        Department parentDepartment = null;
        if (request.parentId() != null) {
            parentDepartment = findById(request.parentId());
        }

        Department department = new Department(request.name(), request.code(), parentDepartment, List.of());
        return DepartmentResult.fromDepartment(departmentRepository.save(department));
    }

    @Transactional(readOnly = true)
    public DepartmentResult getById(Long id) {
        return DepartmentResult.fromDepartment(findById(id));
    }

    private Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResult> getAll() {
        return departmentRepository.findAll().stream().map(DepartmentResult::fromDepartment).toList();
    }

    @Transactional
    public DepartmentResult update(Long id, UpdateDepartmentRequest request) {
        Department department = findById(id);

        if (request.name() != null) {
            department.setName(request.name());
        }

        if (request.code() != null) {
            department.setCode(request.code());
        }

        if (request.parentId() != null && request.parentId().isPresent()) {
            Long newParentId = request.parentId().orElse(null);
            if (newParentId == null) {
                department.setParent(null);
            } else {
                if (newParentId.equals(id)) {
                    throw new DepartmentSelfParentException(id);
                }

                Department parentDepartment = findById(newParentId);
                if (isDescendant(parentDepartment, department)) {
                    throw new DepartmentCycleException(id, parentDepartment.getId());
                }
                department.setParent(parentDepartment);
            }
        }

        return DepartmentResult.fromDepartment(departmentRepository.save(department));
    }

    @Transactional
    public void delete(Long id) {
        Department department = findById(id);

        long employeeCount = employeeRepository.countByDepartmentId(id);
        if (employeeCount > 0) {
            throw new DepartmentHasEmployeesException(id, employeeCount);
        }

        long childrenCount = departmentRepository.countByParentId(id);
        if (childrenCount > 0) {
            throw new DepartmentHasChildrenException(id, childrenCount);
        }

        departmentRepository.delete(department);
    }

    private boolean isDescendant(Department candidate, Department root) {
        Department current = candidate;
        while (current != null) {
            if (current.getId().equals(root.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}
