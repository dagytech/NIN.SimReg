CREATE TABLE sim_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference VARCHAR(255) NOT NULL UNIQUE,
    customer_id BIGINT,
    agent_id BIGINT,
    mobile_number VARCHAR(255),
    mno VARCHAR(100),
    status VARCHAR(50) DEFAULT 'PENDING_APPROVAL',
    created_at DATETIME(6),
    CONSTRAINT fk_reg_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_reg_agent FOREIGN KEY (agent_id) REFERENCES agents(id)
);
