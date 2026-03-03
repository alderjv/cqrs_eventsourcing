// src/main/java/br/org/postalis/training/rh/funcionario/infrastructure/FuncionarioProjector.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratadoV1;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioDemitido;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioDesligado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioEvent;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioPromovido;
import br.org.postalis.training.rh.funcionario.domain.events.SalarioAjustado;
import br.org.postalis.training.rh.shared.domain.DomainEvent;
import br.org.postalis.training.rh.shared.domain.event.projection.RelationalProjector;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Projector que atualiza a tabela funcionario (3NF) a partir de eventos.
 * <p>
 * Projeção Transacional:
 * - Executado na MESMA transação que persiste eventos
 * - Garante consistência forte entre Event Store e Relational Model
 * - Falha em projetar = rollback de tudo
 * <p>
 * Idempotência:
 * - Cada handler deve ser idempotente
 * - Se o mesmo evento for processado múltiplas vezes, resultado é o mesmo
 */
@Component
public class FuncionarioProjector implements RelationalProjector {

    private static final Logger log = LoggerFactory.getLogger(FuncionarioProjector.class);

    private final JdbcTemplate jdbcTemplate;

    public FuncionarioProjector(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getName() {
        return "FuncionarioRelationalProjector";
    }

    @Override
    public String getSourceSchema() {
        return "rh";
    }

    @Override
    public void project(DomainEvent event) {
        // Filtra apenas eventos do aggregate Funcionario
        if (!(event instanceof FuncionarioEvent funcionarioEvent)) {
            return;
        }

        // Pattern matching com sealed interface - compilador garante exaustividade!
        switch (funcionarioEvent) {
            case FuncionarioContratado e -> projectAdmissao(e);
            case FuncionarioDemitido e -> projectDemissao(e);
            case SalarioAjustado e -> projectAjusteSalario(e);
            case FuncionarioPromovido e -> projectPromovido(e);
            case FuncionarioDesligado e -> projectDesligado(e);
            default -> throw new IllegalStateException("Unexpected value: " + funcionarioEvent);
        }
    }

    /**
     * Projeta FuncionarioAdmitido V1.
     * <p>
     * SQL: INSERT INTO rh.funcionario
     * Idempotência: ON CONFLICT DO NOTHING
     */
    private void projectAdmissao(FuncionarioContratadoV1 evento) {
        log.debug("Projetando FuncionarioAdmitido: {}", evento.matricula());

        String sql = """
                INSERT INTO rh.funcionario (
                    id, cpf, nome, email, matricula, cargo, escolaridade, salario,
                    data_admissao, ativo, created_at, version
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, true, ?, 1)
                ON CONFLICT (id) DO UPDATE SET
                 cpf = EXCLUDED.cpf,
                 nome = EXCLUDED.nome,
                 salario = EXCLUDED.salario,
                 cargo = EXCLUDED.cargo,
                 escolaridade = 'Não informado'
                 data_contratacao = EXCLUDED.data_contratacao,
                 status = 'ATIVO'
                """;

        int rows = jdbcTemplate.update(sql,
                evento.funcionarioId(),
                evento.cpf(),
                evento.nome(),
                evento.email(),
                evento.matricula(),
                evento.cargo(),
                evento.salario(),
                evento.dataAdmissao(),
                Timestamp.from(evento.occurredOn())
        );

        if (rows > 0) {
            log.debug("Funcionário {} projetado com sucesso", evento.matricula());
        } else {
            log.debug("Funcionário {} já existe na projeção (idempotência)", evento.matricula());
        }
    }

    private void projectAdmissao(FuncionarioContratado evento) {
        log.debug("Projetando FuncionarioAdmitido: {}", evento.matricula());

        String sql = """
                INSERT INTO rh.funcionario (
                    id, cpf, nome, email, matricula, cargo, escolaridade, salario,
                    data_admissao, ativo, created_at, version
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, true, ?, 1)
                ON CONFLICT (id) DO UPDATE SET
                 cpf = EXCLUDED.cpf,
                 nome = EXCLUDED.nome,
                 salario = EXCLUDED.salario,
                 cargo = EXCLUDED.cargo,
                 escolaridade = EXCLUDED.escolaridade,
                 data_contratacao = EXCLUDED.data_contratacao,
                 status = 'ATIVO'
                """;

        int rows = jdbcTemplate.update(sql,
                evento.funcionarioId(),
                evento.cpf(),
                evento.nome(),
                evento.email(),
                evento.matricula(),
                evento.cargo(),
                evento.salario(),
                evento.dataAdmissao(),
                Timestamp.from(evento.occurredOn())
        );

        if (rows > 0) {
            log.debug("Funcionário {} projetado com sucesso", evento.matricula());
        } else {
            log.debug("Funcionário {} já existe na projeção (idempotência)", evento.matricula());
        }
    }

    /**
     * Projeta FuncionarioDemitido V1.
     * <p>
     * SQL: UPDATE rh.funcionario SET ativo = false
     */
    private void projectDemissao(FuncionarioDemitido evento) {
        log.debug("Projetando FuncionarioDemitido: {}", evento.funcionarioId());

        String sql = """
                UPDATE rh.funcionario
                SET ativo = false,
                    data_demissao = ?,
                    updated_at = ?,
                    version = version + 1
                WHERE id = ?
                """;

        int rows = jdbcTemplate.update(sql,
                evento.dataDemissao(),
                Timestamp.from(evento.occurredOn()),
                evento.funcionarioId()
        );

        if (rows == 0) {
            log.warn("Funcionário {} não encontrado para demissão", evento.funcionarioId());
        }
    }

    /**
     * Projeta SalarioAjustado V1.
     * <p>
     * SQL: UPDATE rh.funcionario SET salario
     */
    private void projectAjusteSalario(SalarioAjustado evento) {
        log.debug("Projetando SalarioAjustado: {}", evento.funcionarioId());

        String sql = """
                UPDATE rh.funcionario
                SET salario = ?,
                    updated_at = ?,
                    version = version + 1
                WHERE id = ?
                """;

        int rows = jdbcTemplate.update(sql,
                evento.salarioNovo(),
                Timestamp.from(evento.occurredOn()),
                evento.funcionarioId()
        );

        if (rows == 0) {
            log.warn("Funcionário {} não encontrado para ajuste salarial", evento.funcionarioId());
        }
    }

    private void projectPromovido(FuncionarioPromovido e) {
        String sql = """
                UPDATE rh.funcionario
                SET cargo = ?,
                    salario = ?,
                    updated_at = NOW()
                WHERE id = ?::uuid
                """;

        jdbcTemplate.update(sql,
                e.cargoNovo(),
                e.salarioNovo(),
                e.aggregateId()
        );
    }

    private void projectDesligado(FuncionarioDesligado e) {
        String sql = """
                UPDATE rh.funcionario
                SET ativo = false,
                    data_demissao = ?,
                    updated_at = NOW()
                WHERE id = ?::uuid
                """;

        jdbcTemplate.update(sql,
                e.dataDesligamento(),
                e.aggregateId()
        );
    }
}