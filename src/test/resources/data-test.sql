-- Insert roles
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_VENERAVEL'),
('ROLE_TESOUREIRO'),
('ROLE_SECRETARIO'),
('ROLE_CHANCELER');

-- Insert test members
INSERT INTO members (name, cim, grau, data_nascimento, cpf, rg, profissao, endereco, cidade, estado, cep, telefone, email, data_iniciacao, observacoes, ativo) VALUES
('Test User', 'CIM001', 'Mestre', '1980-01-01', '123.456.789-00', '12.345.678-9', 'Developer', 'Test Street, 123', 'Test City', 'SP', '12345-678', '(11) 98765-4321', 'test@test.com', '2020-01-01', 'Test member', true),
('Test Admin', 'CIM002', 'Mestre', '1985-01-01', '987.654.321-00', '98.765.432-1', 'Manager', 'Admin Street, 456', 'Test City', 'SP', '12345-679', '(11) 98765-4322', 'admin@test.com', '2019-01-01', 'Test admin member', true);

-- Insert test users (password: 'password123' encrypted with BCrypt)
INSERT INTO users (username, password, email, name, member_id, member_status) VALUES
('testuser', '$2a$10$d55gXQecfBi52SDD1YGve.vNZhKOg64DppsXLPqo6VZSMfl2XEAUK', 'test@test.com', 'Test User', 1, 'ACTIVE'),
('testadmin', '$2a$10$d55gXQecfBi52SDD1YGve.vNZhKOg64DppsXLPqo6VZSMfl2XEAUK', 'admin@test.com', 'Test Admin', 2, 'ACTIVE');

-- Assign roles to test users
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 2), -- testuser -> ROLE_VENERAVEL
(2, 1); -- testadmin -> ROLE_ADMIN

-- Insert test monthly fees
INSERT INTO monthly_fees (member_id, due_date, amount, reference_month, status, notes, created_by, created_at) VALUES
(1, '2024-01-15', 100.00, '2024-01', 'PENDING', 'Test monthly fee', 2, CURRENT_TIMESTAMP());
