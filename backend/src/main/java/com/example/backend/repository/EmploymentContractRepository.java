package com.example.backend.repository;

import com.example.backend.model.employee.ContractType;
import com.example.backend.model.employee.EmploymentContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmploymentContractRepository extends JpaRepository<EmploymentContract, Long> {
    List<EmploymentContract> findByEmployeeId(Long employeeId);

    Optional<EmploymentContract> findByEmployeeIdAndMainJobTrue(Long employeeId);

    boolean existsByContractNumber(String contractNumber);

    @Query("""
            SELECT c FROM EmploymentContract c
            WHERE c.employee.id = :employeeId
              AND c.startDate <= :date
              AND (c.endDate IS NULL OR c.endDate >= :date)
              AND c.mainJob = true
            """)
    Optional<EmploymentContract> findActiveMainOnDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    @Query("""
            SELECT c FROM EmploymentContract c
            WHERE c.employee.id = :employeeId
              AND c.startDate <= :date
              AND (c.endDate IS NULL OR c.endDate >= :date)
            """)
    List<EmploymentContract> findAllActiveOnDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    @Query("""
            SELECT c FROM EmploymentContract c
            WHERE c.contractType = :type
              AND c.endDate IS NOT NULL
              AND c.endDate BETWEEN :from AND :to
            ORDER BY c.endDate
            """)
    List<EmploymentContract> findExpiring(
            @Param("type") ContractType type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
