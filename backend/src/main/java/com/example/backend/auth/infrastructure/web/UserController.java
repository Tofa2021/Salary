package com.example.backend.auth.infrastructure.web;

import com.example.backend.auth.api.UserFacade;
import com.example.backend.auth.api.dto.auth.ChangePasswordCommand;
import com.example.backend.auth.api.dto.user.CreateUserCommand;
import com.example.backend.auth.api.dto.user.UpdateRoleCommand;
import com.example.backend.auth.api.dto.user.UserResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {
    private final UserFacade userFacade;

    @PostMapping
    public ResponseEntity<UserResult> create(@RequestBody CreateUserCommand request) {
        return ResponseEntity.ok(userFacade.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResult> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userFacade.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResult>> getAll() {
        return ResponseEntity.ok(userFacade.getAll());
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResult> updateRole(@PathVariable Long id, @RequestBody UpdateRoleCommand request) {
        return ResponseEntity.ok(userFacade.updateRole(id, request));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordCommand request) {
        userFacade.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
