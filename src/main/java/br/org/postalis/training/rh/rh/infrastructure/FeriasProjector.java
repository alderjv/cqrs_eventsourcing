// src/main/java/br/org/postalis/training/rh/rh/infrastructure/FeriasProjector.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.rh.domain.events.FeriasAprovadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasConcluidaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasIniciadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasRejeitadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasSolicitadaEvent;
import br.org.postalis.training.rh.rh.domain.events.StatusFerias;
import br.org.postalis.training.rh.shared.domain.DomainEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeriasProjector {

    private final JdbcTemplate jdbcTemplate;

    public FeriasProjector(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void project(DomainEvent event) {
        switch (event) {
            case FeriasSolicitadaEvent e -> projectSolicitada(e);
            case FeriasAprovadaEvent e -> projectAprovada(e);
            case FeriasRejeitadaEvent e -> projectRejeitada(e);
            case FeriasIniciadaEvent e -> projectIniciada(e);
            case FeriasConcluidaEvent e -> projectConcluida(e);
            default -> {
            } // Ignora eventos não relacionados
        }
    }

    private void projectSolicitada(FeriasSolicitadaEvent e) {
        String sql = """
                INSERT INTO rh.ferias
                (id, funcionario_id, data_inicio, data_fim, dias_solicitados, status)
                VALUES (?::uuid, ?::uuid, ?, ?, ?, ?)
                ON CONFLICT (id) DO NOTHING
                """;

        jdbcTemplate.update(sql,
                e.aggregateId(),
                e.funcionarioId().toString(),
                e.dataInicio(),
                e.dataFim(),
                e.diasSolicitados(),
                StatusFerias.SOLICITADA.name()
        );
    }

    private void projectAprovada(FeriasAprovadaEvent e) {
        String sql = """
                UPDATE rh.ferias
                SET status = ?,
                    aprovado_por = ?,
                    updated_at = NOW()
                WHERE id = ?::uuid
                """;

        jdbcTemplate.update(sql,
                StatusFerias.APROVADA.name(),
                e.aprovadoPor(),
                e.aggregateId()
        );
    }

    private void projectRejeitada(FeriasRejeitadaEvent e) {
        String sql = """
                UPDATE rh.ferias
                SET status = ?,
                    rejeitado_por = ?,
                    motivo_rejeicao = ?,
                    updated_at = NOW()
                WHERE id = ?::uuid
                """;

        jdbcTemplate.update(sql,
                StatusFerias.REJEITADA.name(),
                e.rejeitadoPor(),
                e.motivo(),
                e.aggregateId()
        );
    }

    private void projectIniciada(FeriasIniciadaEvent e) {
        String sql = """
                UPDATE rh.ferias
                SET status = ?,
                    data_inicio_real = ?,
                    updated_at = NOW()
                WHERE id = ?::uuid
                """;

        jdbcTemplate.update(sql,
                StatusFerias.EM_GOZO.name(),
                e.dataInicioReal(),
                e.aggregateId()
        );
    }

    private void projectConcluida(FeriasConcluidaEvent e) {
        String sql = """
                UPDATE rh.ferias
                SET status = ?,
                    data_retorno = ?,
                    updated_at = NOW()
                WHERE id = ?::uuid
                """;

        jdbcTemplate.update(sql,
                StatusFerias.CONCLUIDA.name(),
                e.dataRetorno(),
                e.aggregateId()
        );
    }
}