package com.example.backend.employee.domain.port;

import com.example.backend.employee.domain.model.Employee;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

import java.util.Optional;

public interface EmployeeRepository {
    boolean existsByPersonnelNumber(String personnelNumber);

    Optional<Employee> findByPersonnelNumber(String personnelNumber);

    PageResult<Employee> findAll(PageRequest pageRequest);

    PageResult<Employee> findByDepartmentId(Long departmentId, PageRequest pageRequest);

    void delete(Employee employee);

    Optional<Employee> findById(Long id);

    Employee save(Employee employee);
}
