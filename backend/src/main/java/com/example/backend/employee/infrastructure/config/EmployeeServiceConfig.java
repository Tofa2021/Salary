package com.example.backend.employee.infrastructure.config;

import com.example.backend.department.api.DepartmentLookup;
import com.example.backend.employee.application.service.EmployeeService;
import com.example.backend.employee.application.service.EmploymentContractService;
import com.example.backend.employee.domain.port.EmployeeRepository;
import com.example.backend.employee.domain.port.EmploymentContractRepository;
import com.example.backend.position.api.PositionLookup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class EmployeeServiceConfig {

    @Bean
    public EmployeeService employeeService(
            EmployeeRepository employeeRepository,
            DepartmentLookup departmentLookup,
            PositionLookup positionLookup
    ) {
        return new EmployeeService(employeeRepository, departmentLookup, positionLookup);
    }

    @Bean
    public EmploymentContractService employmentContractService(
            EmploymentContractRepository employmentContractRepository,
            EmployeeRepository employeeRepository,
            Clock clock
    ) {
        return new EmploymentContractService(employmentContractRepository, employeeRepository, clock);
    }
}
