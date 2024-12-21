-- Insert roles
INSERT INTO roles (name) VALUES
('ROLE_ADMIN'),
('ROLE_VENERAVEL'),
('ROLE_TESOUREIRO'),
('ROLE_SECRETARIO'),
('ROLE_CHANCELER'),
('ROLE_USER');

-- Insert sample members
INSERT INTO members (name, cim, grau, data_nascimento, cpf, rg, profissao, endereco, cidade, estado, cep, telefone, email, data_iniciacao, observacoes, ativo) VALUES
('José Roberto Silva', 'CIM001', 'Mestre', '1975-05-15', '123.456.789-00', '12.345.678-9', 'Engenheiro', 'Rua das Acácias, 123', 'São Paulo', 'SP', '01234-567', '(11) 98765-4321', 'jose.silva@email.com', '2010-03-20', 'Membro ativo desde 2010', true),
('Antonio Carlos Santos', 'CIM002', 'Companheiro', '1980-08-22', '987.654.321-00', '98.765.432-1', 'Advogado', 'Av. dos Ipês, 456', 'São Paulo', 'SP', '04567-890', '(11) 91234-5678', 'antonio.santos@email.com', '2015-06-15', 'Participante assíduo', true),
('Ricardo Oliveira Lima', 'CIM003', 'Aprendiz', '1990-02-10', '456.789.123-00', '45.678.912-3', 'Médico', 'Rua dos Pinheiros, 789', 'São Paulo', 'SP', '05678-901', '(11) 94567-8901', 'ricardo.lima@email.com', '2022-09-10', 'Novo membro', true),
('Paulo Henrique Costa', 'CIM004', 'Mestre', '1970-11-30', '789.123.456-00', '78.912.345-6', 'Empresário', 'Av. Paulista, 1000', 'São Paulo', 'SP', '01310-100', '(11) 97890-1234', 'paulo.costa@email.com', '2005-04-25', 'Ex-Venerável', true),
('Marcos Antonio Pereira', 'CIM005', 'Companheiro', '1985-07-18', '234.567.890-00', '23.456.789-0', 'Professor', 'Rua Augusta, 2500', 'São Paulo', 'SP', '01412-100', '(11) 92345-6789', 'marcos.pereira@email.com', '2018-11-30', 'Coordenador de estudos', true);


-- Insert users (password encrypted using BCrypt, all passwords are set to 'password123')
INSERT INTO users (username, password, email, name, member_id) VALUES
('admin', '$2a$10$d55gXQecfBi52SDD1YGve.vNZhKOg64DppsXLPqo6VZSMfl2XEAUK', 'slproger@gmail.com', 'Administrador Sistema', 1),
('veneravel', '$2a$10$d55gXQecfBi52SDD1YGve.vNZhKOg64DppsXLPqo6VZSMfl2XEAUK', 'veneravel@loja.com.br', 'João Silva', 2),
('tesoureiro', '$2a$10$d55gXQecfBi52SDD1YGve.vNZhKOg64DppsXLPqo6VZSMfl2XEAUK', 'tesoureiro@loja.com.br', 'Pedro Santos', 3),
('secretario', '$2a$10$d55gXQecfBi52SDD1YGve.vNZhKOg64DppsXLPqo6VZSMfl2XEAUK', 'secretario@loja.com.br', 'Carlos Oliveira', 4),
('chanceler', '$2a$10$d55gXQecfBi52SDD1YGve.vNZhKOg64DppsXLPqo6VZSMfl2XEAUK', 'chanceler@loja.com.br', 'Roberto Lima', 5);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 6), -- admin -> ROLE_USER
(2, 6), -- veneravel -> ROLE_USER
(3, 6), -- tesoureiro -> ROLE_USER
(4, 6), -- secretario -> ROLE_USER
(5, 6), -- chanceler -> ROLE_USER
(1, 1), -- admin -> ROLE_ADMIN
(2, 2), -- veneravel -> ROLE_VENERAVEL
(3, 3), -- tesoureiro -> ROLE_TESOUREIRO
(4, 4), -- secretario -> ROLE_SECRETARIO
(5, 5); -- chanceler -> ROLE_CHANCELER

-- Insert sample monthly_fees
INSERT INTO monthly_fees (member_id, due_date, amount, reference_month, status, notes, created_by, created_at) VALUES
(1, '2025-01-15', 100, '2025-01', 'PENDING', 'Mensalidade da loja referente a Janeiro/2025', 1, now()),
(1, '2025-02-15', 100, '2025-02', 'PENDING', 'Mensalidade da loja referente a Fevereiro/2025', 1, now()),
(1, '2025-03-15', 100, '2025-03', 'PENDING', 'Mensalidade da loja referente a Março/2025', 1, now()),
(1, '2025-04-15', 100, '2025-04', 'PENDING', 'Mensalidade da loja referente a Abril/2025', 1, now()),
(1, '2025-05-15', 100, '2025-05', 'PENDING', 'Mensalidade da loja referente a Maio/2025', 1, now()),
(1, '2025-06-15', 100, '2025-06', 'PENDING', 'Mensalidade da loja referente a Junho/2025', 1, now()),
(1, '2025-07-15', 100, '2025-07', 'PENDING', 'Mensalidade da loja referente a julho/2025', 1, now()),
(1, '2025-08-15', 100, '2025-08', 'PENDING', 'Mensalidade da loja referente a Agosto/2025', 1, now()),
(1, '2025-09-15', 100, '2025-09', 'PENDING', 'Mensalidade da loja referente a Setembro/2025', 1, now()),
(1, '2025-10-15', 100, '2025-10', 'PENDING', 'Mensalidade da loja referente a outubro/2025', 1, now()),
(1, '2025-11-15', 100, '2025-11', 'PENDING', 'Mensalidade da loja referente a Novembro/2025', 1, now()),
(1, '2025-12-15', 100, '2025-12', 'PENDING', 'Mensalidade da loja referente a Dezembro/2025', 1, now());

-- Insert sample monthly_fees
INSERT INTO monthly_fees (member_id, due_date, amount, reference_month, status, notes, created_by, created_at) VALUES
(2, '2025-01-15', 200, '2025-01', 'PENDING', 'Mensalidade da loja referente a Janeiro/2025', 1, now()),
(2, '2025-02-15', 200, '2025-02', 'PENDING', 'Mensalidade da loja referente a Fevereiro/2025', 1, now()),
(2, '2025-03-15', 200, '2025-03', 'PENDING', 'Mensalidade da loja referente a Março/2025', 1, now()),
(2, '2025-04-15', 200, '2025-04', 'PENDING', 'Mensalidade da loja referente a Abril/2025', 1, now()),
(2, '2025-05-15', 200, '2025-05', 'PENDING', 'Mensalidade da loja referente a Maio/2025', 1, now()),
(2, '2025-06-15', 200, '2025-06', 'PENDING', 'Mensalidade da loja referente a Junho/2025', 1, now()),
(2, '2025-07-15', 200, '2025-07', 'PENDING', 'Mensalidade da loja referente a julho/2025', 1, now()),
(2, '2025-08-15', 200, '2025-08', 'PENDING', 'Mensalidade da loja referente a Agosto/2025', 1, now()),
(2, '2025-09-15', 200, '2025-09', 'PENDING', 'Mensalidade da loja referente a Setembro/2025', 1, now()),
(2, '2025-10-15', 200, '2025-10', 'PENDING', 'Mensalidade da loja referente a outubro/2025', 1, now()),
(2, '2025-11-15', 200, '2025-11', 'PENDING', 'Mensalidade da loja referente a Novembro/2025', 1, now()),
(2, '2025-12-15', 200, '2025-12', 'PENDING', 'Mensalidade da loja referente a Dezembro/2025', 1, now());
