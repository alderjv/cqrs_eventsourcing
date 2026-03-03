// src/main/java/br/org/postalis/training/rh/shared/infrastructure/eventsourcing/ReplayService.java
package br.org.postalis.training.rh.shared.infrastructure.eventsourcing;

import br.org.postalis.training.rh.shared.domain.DomainEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@Service
public class ReplayService {

    private final EventStore eventStore;
    private final JdbcTemplate jdbcTemplate;

    public ReplayService(EventStore eventStore, JdbcTemplate jdbcTemplate) {
        this.eventStore = eventStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Faz replay de todos os eventos de um tipo de aggregate.
     */
    @Transactional
    public void replayAll(String aggregateType, Consumer<DomainEvent> projector) {
        List<DomainEvent> events = eventStore.loadByAggregateType(aggregateType);

        for (DomainEvent event : events) {
            projector.accept(event);
        }
    }

    /**
     * Faz replay de eventos de um aggregate específico.
     */
    @Transactional
    public void replayAggregate(String aggregateId, Consumer<DomainEvent> projector) {
        List<DomainEvent> events = eventStore.load(aggregateId);

        for (DomainEvent event : events) {
            projector.accept(event);
        }
    }

    /**
     * Reconstrói toda a projeção de funcionários.
     */
    @Transactional
    public void rebuildFuncionarioProjection() {
        // 1. Limpar projeção
        jdbcTemplate.execute("TRUNCATE rh.funcionario CASCADE");

        // 2. Replay de todos os eventos
        replayAll("Funcionario", this::projectFuncionarioEvent);
    }

    /**
     * Reconstrói toda a projeção de férias.
     */
    @Transactional
    public void rebuildFeriasProjection() {
        jdbcTemplate.execute("TRUNCATE rh.ferias CASCADE");
        replayAll("Ferias", this::projectFeriasEvent);
    }

    private void projectFuncionarioEvent(DomainEvent event) {
        // Delega para o Projector real
        // funcionarioProjector.project(event);
    }

    private void projectFeriasEvent(DomainEvent event) {
        // Delega para o Projector real
        // feriasProjector.project(event);
    }
}