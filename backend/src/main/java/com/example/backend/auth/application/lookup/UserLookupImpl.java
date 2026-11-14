package com.example.backend.auth.application.lookup;

import com.example.backend.auth.api.UserLookup;
import com.example.backend.auth.api.dto.user.UserResult;
import com.example.backend.auth.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserLookupImpl implements UserLookup {
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResult> findById(Long userId) {
        return userService.findByIdOptional(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResult> findByUsername(String username) {
        return userService.findByUsernameOptional(username);
    }
}
