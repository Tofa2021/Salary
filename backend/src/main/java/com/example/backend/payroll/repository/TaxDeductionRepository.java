package com.example.backend.payroll.repository;

import com.example.backend.payroll.model.DeductionType;
import com.example.backend.payroll.model.TaxDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaxDeductionRepository extends JpaRepository<TaxDeduction, Long> {
    List<TaxDeduction> findByEmployeeId(Long employeeId);

    boolean existsByEmployeeIdAndTypeAndDocumentNumber(Long employeeId, DeductionType type, String documentNumber);


    @Query("""
            SELECT d FROM TaxDeduction d
            WHERE d.employee.id = :employeeId
              AND d.validFrom <= :date
              AND (d.validTo IS NULL OR d.validTo >= :date)
            """)
    List<TaxDeduction> findActiveOnDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
}
