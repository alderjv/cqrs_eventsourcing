-- src/main/resources/db/migration/rh/V3__create_ferias_table.sql

CREATE TABLE rh.ferias (
    id UUID PRIMARY KEY DEFAULT uuidv7(),  -- ← PostgreSQL 18 nativo!
    funcionario_id UUID NOT NULL REFERENCES rh.funcionario(id),
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    dias_solicitados INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    aprovado_por VARCHAR(100),
    rejeitado_por VARCHAR(100),
    motivo_rejeicao TEXT,
    data_inicio_real DATE,
    data_retorno DATE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Índices
CREATE INDEX idx_ferias_funcionario ON rh.ferias(funcionario_id);
CREATE INDEX idx_ferias_status ON rh.ferias(status);
CREATE INDEX idx_ferias_periodo ON rh.ferias(data_inicio, data_fim);

COMMENT ON TABLE rh.ferias IS 'Projeção 3NF para queries de férias';