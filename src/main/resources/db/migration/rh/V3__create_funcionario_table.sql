-- src/main/resources/db/migration/rh/V3__create_funcionario_table.sql

-- Tabela relacional para queries
CREATE TABLE rh.funcionario (
    id UUID PRIMARY KEY DEFAULT uuidv7(),
    cpf VARCHAR(11) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    matricula VARCHAR(6) NOT NULL UNIQUE,
    cargo VARCHAR(100) NOT NULL,
    salario DECIMAL(10,2) NOT NULL,
    data_admissao DATE NOT NULL,
    data_demissao DATE,
    ativo BOOLEAN NOT NULL DEFAULT true,

    -- Auditoria
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    version INT NOT NULL DEFAULT 1
);

-- Índices para queries comuns
CREATE INDEX idx_funcionario_cpf ON rh.funcionario(cpf);
CREATE INDEX idx_funcionario_matricula ON rh.funcionario(matricula);
CREATE INDEX idx_funcionario_ativo ON rh.funcionario(ativo) WHERE ativo = true;

COMMENT ON TABLE rh.funcionario IS 'Projeção 3NF para queries - derivada do Event Store';