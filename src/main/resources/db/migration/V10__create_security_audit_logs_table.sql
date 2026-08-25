CREATE TABLE security_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(100),
    path VARCHAR(255),
    ip_address VARCHAR(100),
    details VARCHAR(1000),
    occurred_at DATETIME(6)
);
