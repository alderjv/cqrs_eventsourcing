// src/main/java/br/org/postalis/training/rh/shared/infrastructure/eventsourcing/EventStore.java
package br.org.postalis.training.rh.shared.infrastructure.eventsourcing;

import br.org.postalis.training.rh.shared.domain.DomainEvent;
import java.util.List;

/**
 * Interface para persistência de eventos.
 */
public interface EventStore {

    /**
     * Salva eventos de um Aggregate.
     *
     * @param aggregateId     ID do Aggregate
     * @param aggregateType   Tipo do Aggregate (ex: "Funcionario")
     * @param events          Lista de eventos a salvar
     * @param expectedVersion Versão esperada (optimistic locking)
     */
    void save(String aggregateId, String aggregateType,
              List<DomainEvent> events, long expectedVersion);

    /**
     * Carrega todos os eventos de um Aggregate.
     */
    List<DomainEvent> load(String aggregateId);

    /**
     * Carrega eventos a partir de uma versão específica.
     * Usado em conjunto com Snapshots.
     */
    List<DomainEvent> loadFromVersion(String aggregateId, long fromVersion);

    long getVersion(String string);

    /**
     * Carrega todos os eventos de um tipo de aggregate.
     */
    List<DomainEvent> loadByAggregateType(String aggregateType);
}