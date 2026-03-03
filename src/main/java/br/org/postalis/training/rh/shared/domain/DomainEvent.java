// src/main/java/br/org/postalis/training/rh/shared/domain/DomainEvent.java
package br.org.postalis.training.rh.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Contrato base para todos os eventos de domínio no sistema.
 *
 * Define os metadados essenciais que todo evento deve possuir para suportar
 * Event Sourcing, auditoria e reconstrução de estado de aggregates.
 *
 * Características dos Eventos:
 * - Imutáveis: Uma vez criados, não podem ser modificados
 * - Versionados: Suportam evolução através do campo version
 * - Rastreáveis: Possuem eventId único e timestamp
 * - Ligados ao Aggregate: Sempre associados a um aggregateId
 *
 * @see AggregateRoot
 */
public interface DomainEvent {

    /**
     * Identificador único do evento.
     * Permite rastreamento individual no Event Store e
     * garante idempotência no processamento.
     */
    UUID eventId();

    /**
     * Timestamp de quando o evento ocorreu no domínio.
     * Representa o momento do fato de negócio, não quando foi persistido.
     */
    Instant occurredOn();

    /**
     * Identificador do aggregate que gerou o evento.
     * Usado para reconstruir o estado via fromEvents().
     */
    String aggregateId();

    /**
     * Tipo do evento para identificação e roteamento.
     * Normalmente é o nome da classe (ex: "FuncionarioContratado").
     */
    String eventType();

    /**
     * Versão do schema do evento.
     * Suporta evolução do evento ao longo do tempo.
     * Incrementado quando a estrutura muda (upcasting).
     */
    int version();

    /**
     * Schema (módulo) ao qual o evento pertence.
     *
     * Define em qual schema do banco de dados o evento deve ser persistido.
     * Exemplos: "rh", "identidade", "cadastros", "emprestimo".
     *
     * IMPORTANTE: Este método NÃO tem implementação default.
     * Cada evento DEVE declarar explicitamente seu schema.
     */
    String schema();

    /**
     * Metadata de auditoria e rastreabilidade.
     *
     * Implementação padrão retorna null. Eventos que precisam de
     * metadata específica devem sobrescrever.
     *
     * A metadata é armazenada em coluna JSONB separada do payload.
     */
    default EventMetadata metadata() {
        return null;
    }
}