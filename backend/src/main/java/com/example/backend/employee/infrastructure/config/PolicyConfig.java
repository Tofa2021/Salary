package com.example.backend.employee.infrastructure.config;

import com.example.backend.employee.domain.policy.MainJobPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PolicyConfig {
    @Bean
    public MainJobPolicy mainJobPolicy() {
        return new MainJobPolicy();
    }
}
