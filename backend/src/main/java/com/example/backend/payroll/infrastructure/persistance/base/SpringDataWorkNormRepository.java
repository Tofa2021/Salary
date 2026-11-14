package com.example.backend.payroll.infrastructure.persistance.base;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataWorkNormRepository extends JpaRepository<WorkNormJpaEntity, Long> {
    Optional<WorkNormJpaEntity> findByYearAndMonth(int year, int month);
}
