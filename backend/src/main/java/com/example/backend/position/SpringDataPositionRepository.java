package com.example.backend.position;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPositionRepository extends JpaRepository<Position, Long> {
    boolean existsByName(String name);
}
