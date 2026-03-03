CREATE TABLE IF NOT EXISTS rh.event_publication
(
    id UUID PRIMARY KEY,

    -- Identificador do listener/subscriber que processa o evento
    listener_id VARCHAR(255) NOT NULL,

    -- Tipo do evento (nome totalmente qualificado da classe)
    event_type VARCHAR(255) NOT NULL,

    -- Evento serializado (geralmente JSON)
    serialized_event TEXT NOT NULL,

    -- Data/hora em que o evento foi publicado
    publication_date TIMESTAMPTZ NOT NULL,

    -- Data/hora em que o processamento foi concluído
    completion_date TIMESTAMPTZ,

    -- Estado do processamento (ex.: PUBLISHED, COMPLETED, FAILED)
    processing_state VARCHAR(50) NOT NULL,

    -- Mensagem de erro (se houver)
    error_message TEXT
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS ix_event_publication_processing_state
    ON rh.event_publication (processing_state);

CREATE INDEX IF NOT EXISTS ix_event_publication_listener
    ON rh.event_publication (listener_id);

CREATE INDEX IF NOT EXISTS ix_event_publication_event_type
    ON rh.event_publication (event_type);

-- Documentação
COMMENT ON TABLE rh.event_publication
    IS 'Spring Modulith Event Publication Registry (infra/outbox para entrega confiável)';

COMMENT ON COLUMN rh.event_publication.listener_id
    IS 'Identificador do listener/subscriber';

COMMENT ON COLUMN rh.event_publication.event_type
    IS 'Tipo do evento (FQN da classe)';

COMMENT ON COLUMN rh.event_publication.serialized_event
    IS 'Evento serializado (ex.: JSON)';

COMMENT ON COLUMN rh.event_publication.processing_state
    IS 'Estado do processamento do evento';
