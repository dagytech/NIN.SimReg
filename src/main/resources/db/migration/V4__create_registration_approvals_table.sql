CREATE TABLE registration_approvals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sim_registration_id BIGINT,
    otp_code VARCHAR(10),
    otp_expires_at DATETIME(6),
    approved BOOLEAN DEFAULT FALSE,
    approved_at DATETIME(6),
    CONSTRAINT fk_approval_reg FOREIGN KEY (sim_registration_id) REFERENCES sim_registrations(id)
);
