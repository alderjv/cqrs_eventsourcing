-- Criar schemas por módulo (padrão Core Postalis)
CREATE SCHEMA IF NOT EXISTS rh;
CREATE SCHEMA IF NOT EXISTS identidade;
CREATE SCHEMA IF NOT EXISTS shared;

-- Extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";  -- UUID v4
CREATE EXTENSION IF NOT EXISTS "pgcrypto";   -- Criptografia

-- Nota: UUID v7 é gerado pela aplicação Java, não pelo PostgreSQL

-- Comentário para documentação
COMMENT ON SCHEMA rh IS 'Módulo de Recursos Humanos - Funcionários, Férias, Departamentos';
COMMENT ON SCHEMA identidade IS 'Módulo de Identidade - Usuários, Autenticação, Autorização';
COMMENT ON SCHEMA shared IS 'Módulo Compartilhado - Snapshots, Configurações globais';