-- V6__add_cargo_funcionarios.sql
ALTER TABLE rh.funcionario
ADD COLUMN IF NOT EXISTS escolaridade VARCHAR(100) DEFAULT 'Não Informado';