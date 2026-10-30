package com.example.backend.model.audit;

import com.example.backend.model.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;
    private Long entityId;

    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime timestamp;
    private String changesJson;
}
