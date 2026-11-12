package com.example.backend.employee.infrastructure.persistance;

import com.example.backend.employee.domain.model.ContractType;
import com.example.backend.employee.infrastructure.persistance.model.EmploymentContractJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataEmploymentContractRepository extends JpaRepository<EmploymentContractJpaEntity, Long> {
    List<EmploymentContractJpaEntity> findByEmployeeId(Long employeeId);

    Optional<EmploymentContractJpaEntity> findByEmployeeIdAndMainJobTrue(Long employeeId);

    boolean existsByContractNumber(String contractNumber);

    @Query("""
            SELECT c FROM EmploymentContractJpaEntity c
            WHERE c.employeeId = :employeeId
              AND c.startDate <= :date
              AND (c.endDate IS NULL OR c.endDate >= :date)
              AND c.mainJob = true
            """)
    Optional<EmploymentContractJpaEntity> findActiveMainOnDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    @Query("""
            SELECT c FROM EmploymentContractJpaEntity c
            WHERE c.employeeId = :employeeId
              AND c.startDate <= :date
              AND (c.endDate IS NULL OR c.endDate >= :date)
            """)
    List<EmploymentContractJpaEntity> findAllActiveOnDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);

    @Query("""
            SELECT c FROM EmploymentContractJpaEntity c
            WHERE c.contractType = :type
              AND c.endDate IS NOT NULL
              AND c.endDate BETWEEN :from AND :to
            ORDER BY c.endDate
            """)
    List<EmploymentContractJpaEntity> findExpiring(
            @Param("type") ContractType type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
