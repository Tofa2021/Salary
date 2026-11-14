package com.example.backend.position.application;

import com.example.backend.position.PositionService;
import com.example.backend.position.api.PositionLookup;
import com.example.backend.position.api.dto.PositionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionLookupImpl implements PositionLookup {
    private final PositionService positionService;

    @Override
    public Optional<PositionResult> findById(Long id) {
        return positionService.findByIdOptional(id);
    }
}
