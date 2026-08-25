CREATE TABLE device_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sim_registration_id BIGINT,
    device_fingerprint VARCHAR(255),
    ip_address VARCHAR(100),
    recorded_at DATETIME(6),
    CONSTRAINT fk_device_reg FOREIGN KEY (sim_registration_id) REFERENCES sim_registrations(id)
);
