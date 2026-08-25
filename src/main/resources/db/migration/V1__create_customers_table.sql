CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nin VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    last_name VARCHAR(255),
    date_of_birth DATE,
    phone_number VARCHAR(255),
    nida_verified BOOLEAN DEFAULT FALSE,
    biometric_verified BOOLEAN DEFAULT FALSE
);
