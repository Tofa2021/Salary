package com.example.backend.repository;

import com.example.backend.model.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByPersonnelNumber(String personnelNumber);

    boolean existsByPersonnelNumber(String personnelNumber);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByPositionId(Long positionId);
}
