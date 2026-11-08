package com.example.backend.repository;

import com.example.backend.model.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByPersonnelNumber(String personnelNumber);

    boolean existsByPersonnelNumber(String personnelNumber);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByPositionId(Long positionId);

    long countByDepartmentId(Long departmentId);

    @Query("""
            SELECT e FROM Employee e
            WHERE e.department.id = :departmentId
              AND e.hireDate <= :date
              AND (e.dismissalDate IS NULL OR e.dismissalDate >= :date)
            """)
    List<Employee> findActiveByDepartmentOnDate(@Param("departmentId") Long departmentId, @Param("date") LocalDate date);
}
