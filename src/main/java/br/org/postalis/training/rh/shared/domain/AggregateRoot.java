// src/main/java/br/org/postalis/training/rh/shared/domain/AggregateRoot.java
package br.org.postalis.training.rh.shared.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Classe base para Aggregates.
 * <p>
 * Responsabilidades:
 * - Manter lista de eventos não commitados
 * - Aplicar eventos para reconstruir estado
 */
public abstract class AggregateRoot extends Entity {

    private final List<DomainEvent> uncommittedEvents = new ArrayList<>();
    private long version = 0;

    protected AggregateRoot(UUID id) {
        super(id);
    }

    /**
     * Versão atual (número de eventos aplicados)
     */
    public long getVersion() {
        return version;
    }

    /**
     * Para uso interno - define versão após carregar snapshot
     */
    protected void setVersion(long version) {
        this.version = version;
    }

    /**
     * Eventos ainda não persistidos
     */
    public List<DomainEvent> getUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedEvents);
    }

    /**
     * Limpa eventos após persistência
     */
    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }

    /**
     * Gera um novo evento.
     * 1. Aplica ao estado interno
     * 2. Adiciona à lista de não commitados
     */
    protected void raise(DomainEvent event) {
        apply(event);
        uncommittedEvents.add(event);
        version++;
    }

    /**
     * Aplica um evento ao estado.
     * Cada subclasse implementa sua lógica.
     */
    protected abstract void apply(DomainEvent event);

    /**
     * Incrementa versão (usado na reconstrução)
     */
    protected void incrementVersion() {
        this.version++;
    }

    /**
     * Reconstrói Aggregate a partir de eventos históricos.
     */
    public static <T extends AggregateRoot> T fromEvents(
            List<DomainEvent> events,
            Supplier<T> supplier) {

        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("Lista de eventos não pode ser vazia");
        }

        T aggregate = supplier.get();

        for (DomainEvent event : events) {
            aggregate.apply(event);
            aggregate.incrementVersion();
        }

        return aggregate;
    }
}