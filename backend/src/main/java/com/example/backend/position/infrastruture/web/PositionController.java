package com.example.backend.position.infrastruture.web;

import com.example.backend.position.PositionService;
import com.example.backend.position.api.dto.PositionCommand;
import com.example.backend.position.api.dto.PositionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PositionResult create(@RequestBody PositionCommand command) {
        return positionService.create(command);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PositionResult getById(@PathVariable Long id) {
        return positionService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PositionResult> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return positionService.getAll(page, size);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PositionResult update(@PathVariable Long id, @RequestBody PositionCommand command) {
        return positionService.update(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        positionService.delete(id);
    }
}
