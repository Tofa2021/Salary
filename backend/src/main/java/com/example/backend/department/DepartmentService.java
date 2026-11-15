package com.example.backend.department;


import com.example.backend.department.api.dto.CreateDepartmentCommand;
import com.example.backend.department.api.dto.DepartmentResult;
import com.example.backend.department.api.dto.UpdateDepartmentCommand;
import com.example.backend.department.exception.*;
import com.example.backend.employee.api.EmployeeLookup;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final SpringDataDepartmentRepository departmentRepository;
    private final EmployeeLookup employeeLookup;

    @Transactional
    public DepartmentResult create(CreateDepartmentCommand request) {
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

        Department department = new Department(request.name(), request.code(), parentDepartment);
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
    public PageResult<DepartmentResult> getAll(PageRequest request) {
        var page = departmentRepository.findAll(org.springframework.data.domain.PageRequest.of(request.page(), request.size()));
        return PageResult.of(page.stream().map(DepartmentResult::fromDepartment).toList(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Transactional
    public DepartmentResult update(Long id, UpdateDepartmentCommand request) {
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

        if (employeeLookup.existsByDepartmentId(id)) {
            throw new DepartmentHasEmployeesException(id);
        }

        long childrenCount = departmentRepository.countByParentId(id);
        if (childrenCount > 0) {
            throw new DepartmentHasChildrenException(id, childrenCount);
        }

        departmentRepository.delete(department);
    }

    @Transactional(readOnly = true)
    public Optional<DepartmentResult> findByIdOptional(Long id) {
        return departmentRepository.findById(id).map(DepartmentResult::fromDepartment);
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
