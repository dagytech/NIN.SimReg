package com.dagytech.simreg.service;

import com.dagytech.simreg.model.SecurityAuditLog;
import com.dagytech.simreg.repository.SecurityAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityAuditService {

    private final SecurityAuditLogRepository repository;

    @Autowired
    public SecurityAuditService(SecurityAuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(String eventType, String path, String ipAddress, String details) {
        repository.save(new SecurityAuditLog(eventType, path, ipAddress, details));
        // Pia tunachapisha kwenye console - kwenye mfumo halisi hii ingekwenda
        // kwenye mfumo wa "alerting" (mfano Slack/PagerDuty) kama tukio ni kali
        System.out.println("[SECURITY AUDIT] " + eventType + " | path=" + path + " | ip=" + ipAddress + " | " + details);
    }
}
