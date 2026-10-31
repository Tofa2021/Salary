package com.example.backend.repository;

import com.example.backend.model.employee.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
    boolean existsByName(String name);
}
