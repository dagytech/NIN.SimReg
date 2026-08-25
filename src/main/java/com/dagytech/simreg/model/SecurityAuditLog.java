// single responsibility/  Kurekodi matukio ya usalama TU

package com.dagytech.simreg.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Tofauti na AuditLog (matukio ya KIBIASHARA - verify NIN, approve, n.k.),
 * hii inarekodi matukio ya KIUSALAMA TU - majaribio ya kuvunja mfumo:
 * API key mbaya, signature batili, rate limit, replay attempts.
 * Hii ndiyo timu ya usalama ingeangalia kila siku kutafuta mashambulizi.
 */
@Entity
@Table(name = "security_audit_logs")
public class SecurityAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;   // API_KEY_REJECTED, SIGNATURE_INVALID, REPLAY_DETECTED, RATE_LIMITED
    private String path;
    private String ipAddress;
    private String details;
    private LocalDateTime occurredAt = LocalDateTime.now();

    public SecurityAuditLog() {
    }

    public SecurityAuditLog(String eventType, String path, String ipAddress, String details) {
        this.eventType = eventType;
        this.path = path;
        this.ipAddress = ipAddress;
        this.details = details;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
