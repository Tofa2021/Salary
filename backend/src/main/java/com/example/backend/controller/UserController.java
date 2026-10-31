package com.example.backend.controller;

import com.example.backend.dto.user.ChangePasswordRequest;
import com.example.backend.dto.user.CreateUserRequest;
import com.example.backend.dto.user.UpdateRoleRequest;
import com.example.backend.dto.user.UserDto;
import com.example.backend.service.UserService;
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
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDto> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(userService.updateRole(id, request.role()));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
