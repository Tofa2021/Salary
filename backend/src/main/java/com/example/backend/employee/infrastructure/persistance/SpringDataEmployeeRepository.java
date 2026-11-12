package com.example.backend.employee.infrastructure.persistance;

import com.example.backend.employee.infrastructure.persistance.model.EmployeeJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataEmployeeRepository extends JpaRepository<EmployeeJpaEntity, Long> {
    Optional<EmployeeJpaEntity> findByPersonnelNumber(String personnelNumber);

    boolean existsByPersonnelNumber(String personnelNumber);

    Page<EmployeeJpaEntity> findByDepartmentId(Long departmentId, Pageable pageable);

    List<EmployeeJpaEntity> findByDepartmentId(Long departmentId);

    List<EmployeeJpaEntity> findByPositionId(Long positionId);

    long countByDepartmentId(Long departmentId);

    @Query("""
            SELECT e FROM EmployeeJpaEntity e
            WHERE e.departmentId = :departmentId
              AND e.hireDate <= :date
              AND (e.dismissalDate IS NULL OR e.dismissalDate >= :date)
            """)
    List<EmployeeJpaEntity> findActiveByDepartmentOnDate(@Param("departmentId") Long departmentId, @Param("date") LocalDate date);
}
