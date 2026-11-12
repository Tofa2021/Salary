package com.example.backend.department;

import com.example.backend.department.api.dto.CreateDepartmentCommand;
import com.example.backend.department.api.dto.UpdateDepartmentCommand;
import com.example.backend.employee.api.EmployeeLookup;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract class BaseDepartmentServiceTest {
    @Mock
    protected SpringDataDepartmentRepository departmentRepository;

    @Mock
    protected EmployeeLookup employeeLookup;

    @InjectMocks
    protected DepartmentService departmentService;

    protected Department department(Long id, String name, String code, Department parent) {
        Department department = new Department(name, code, parent);
        department.setId(id);
        return department;
    }

    protected CreateDepartmentCommand createCommand(String name, String code, Long parentId) {
        return new CreateDepartmentCommand(name, code, parentId);
    }

    protected UpdateDepartmentCommand updateCommand(String name, String code, JsonNullable<Long> parentId) {
        return new UpdateDepartmentCommand(name, code, parentId);
    }

    protected void stubSaveAssignsId(Long id) {
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> {
            Department d = inv.getArgument(0);
            if (d.getId() == null) d.setId(id);
            return d;
        });
    }
}
