-- Drop tables if they exist
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS monthly_fees;
DROP TABLE IF EXISTS password_reset_token;
DROP TABLE IF EXISTS roles;
ALTER TABLE users DROP FOREIGN KEY `users_ibfk_1`;
ALTER TABLE users DROP INDEX member_id;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS users;

-- Create roles table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);
-- Create members table
CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    cim VARCHAR(20) NOT NULL UNIQUE,
    grau VARCHAR(50) NOT NULL,
    data_nascimento DATE NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    rg VARCHAR(20) NOT NULL,
    profissao VARCHAR(100),
    endereco VARCHAR(200) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    cep VARCHAR(9) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    data_iniciacao DATE,
    observacoes TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);
-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    member_id BIGINT,
    member_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (member_id) REFERENCES members(id)
);
-- Create user_roles junction table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
-- Add columns to users table
ALTER TABLE users
ADD COLUMN provider VARCHAR(20) DEFAULT 'LOCAL',
    ADD COLUMN provider_id VARCHAR(255),
    ADD COLUMN image_url VARCHAR(255),
    ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;
-- Add columns to members table
ALTER TABLE members 
	ADD COLUMN user_id BIGINT,
    ADD COLUMN pendente BOOLEAN NOT NULL DEFAULT FALSE,
    ADD FOREIGN KEY (user_id) REFERENCES users(id);
-- Create password_reset_token table
CREATE TABLE password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
-- Create monthly_fees table
CREATE TABLE IF NOT EXISTS monthly_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    due_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    reference_month VARCHAR(7) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_date DATE,
    payment_method VARCHAR(50),
    notes TEXT,
    created_by BIGINT,
    created_at DATE NOT NULL,
    last_modified_by BIGINT,
    last_modified_at DATE,
    FOREIGN KEY (member_id) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (last_modified_by) REFERENCES users(id)
);
