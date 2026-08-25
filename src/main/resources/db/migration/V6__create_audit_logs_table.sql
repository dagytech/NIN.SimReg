CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(100),
    entity_ref VARCHAR(255),
    action VARCHAR(100),
    performed_by VARCHAR(255),
    details VARCHAR(1000),
    occurred_at DATETIME(6)
);
