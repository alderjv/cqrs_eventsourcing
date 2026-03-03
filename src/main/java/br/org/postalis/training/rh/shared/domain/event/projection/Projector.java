// src/main/java/br/org/postalis/training/rh/shared/domain/event/projection/Projector.java
package br.org.postalis.training.rh.shared.domain.event.projection;

import br.org.postalis.training.rh.shared.domain.DomainEvent;

/**
 * Interface base para transformar eventos em Read Models.
 * <p>
 * Responsabilidades:
 * - Consumir eventos do Event Store
 * - Transformar eventos em modelos de leitura otimizados
 * - Manter idempotência (processar mesmo evento várias vezes = mesmo resultado)
 */
public interface Projector {

    /**
     * Nome único do projector.
     * Usado para identificar checkpoints e logs.
     */
    String getName();

    /**
     * Schema (módulo) do qual este projector consome eventos.
     */
    String getSourceSchema();

    /**
     * Projeta um evento para o Read Model.
     * <p>
     * DEVE SER IDEMPOTENTE: processar o mesmo evento
     * múltiplas vezes deve produzir o mesmo resultado.
     */
    void project(DomainEvent event);

    /**
     * Verifica se este projector processa este tipo de evento.
     */
    default boolean canHandle(String eventType) {
        return true;
    }
}