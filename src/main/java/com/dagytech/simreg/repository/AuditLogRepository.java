package com.dagytech.simreg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dagytech.simreg.model.AuditLog;
// inteface segregation/ hii ni interface ndogo yenye jukumu moja tu kuhusu audit log


public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityRefOrderByOccurredAtDesc(String entityRef);
}
