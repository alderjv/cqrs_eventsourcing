// src/main/java/br/org/postalis/training/rh/rh/domain/FeriasEvent.java
package br.org.postalis.training.rh.rh.domain.events;

import br.org.postalis.training.rh.shared.domain.DomainEvent;

/**
 * Interface sealed para eventos de Férias.
 * Garante que o compilador detecte todos os casos no switch.
 */
public sealed interface FeriasEvent extends DomainEvent
        permits FeriasSolicitadaEvent,
        FeriasAprovadaEvent,
        FeriasRejeitadaEvent,
        FeriasIniciadaEvent,
        FeriasConcluidaEvent {
}