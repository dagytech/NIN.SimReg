package com.dagytech.simreg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dagytech.simreg.model.SecurityAuditLog;

// inteface segregation/ hii ni interface ndogo yenye jukumu moja tu kuhusu security audit log

public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, Long> {
    List<SecurityAuditLog> findTop50ByOrderByOccurredAtDesc();
}
