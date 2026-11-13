package com.example.backend.position.api;

import com.example.backend.position.api.dto.PositionResult;

import java.util.Optional;

public interface PositionLookup {
    Optional<PositionResult> findById(Long id);
}
