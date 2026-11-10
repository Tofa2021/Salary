package com.example.backend.timesheet.model;

import com.example.backend.auth.model.User;
import com.example.backend.department.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "time_sheets")
public class TimeSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;
    private int month;

    @ManyToOne
    private Department department;

    @OneToMany(mappedBy = "timeSheet")
    private List<TimeSheetEntry> entries;

    private boolean approved;
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    public TimeSheet(int year, int month, Department department) {
        this.year = year;
        this.month = month;
        this.department = department;
        this.approved = false;
        this.approvedAt = null;
        this.entries = new ArrayList<>();
        this.approvedBy = null;
    }
}
