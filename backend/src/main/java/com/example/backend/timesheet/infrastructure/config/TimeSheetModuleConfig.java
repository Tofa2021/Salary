package com.example.backend.timesheet.infrastructure.config;

import com.example.backend.timesheet.application.service.TimeSheetService;
import com.example.backend.timesheet.domain.policy.NightShiftPolicy;
import com.example.backend.timesheet.domain.policy.OvertimePolicy;
import com.example.backend.timesheet.domain.port.TimeSheetRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeSheetModuleConfig {
    @Bean
    public NightShiftPolicy nightShiftPolicy() {
        return new NightShiftPolicy();
    }

    @Bean
    public OvertimePolicy overtimePolicy() {
        return new OvertimePolicy();
    }

    @Bean
    public TimeSheetService timeSheetService(TimeSheetRepository timeSheetRepository, Clock clock) {
        return new TimeSheetService(timeSheetRepository, clock);
    }
}
