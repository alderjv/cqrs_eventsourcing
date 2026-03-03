// src/main/java/br/org/postalis/training/rh/shared/domain/AggregateRepository.java
package br.org.postalis.training.rh.shared.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface base para repositories de Aggregates.
 */
public interface AggregateRepository<T extends AggregateRoot> {

    /**
     * Salva um Aggregate (persiste eventos não commitados)
     */
    void save(T aggregate);

    /**
     * Busca um Aggregate por ID (reconstrói de eventos)
     */
    Optional<T> findById(UUID id);

    /**
     * Verifica se existe
     */
    default boolean existsById(UUID id) {
        return findById(id).isPresent();
    }
}