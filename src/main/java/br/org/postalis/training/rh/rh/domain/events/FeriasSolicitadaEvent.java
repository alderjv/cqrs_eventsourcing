// src/main/java/br/org/postalis/training/rh/rh/domain/FeriasSolicitadaEvent.java
package br.org.postalis.training.rh.rh.domain.events;

import br.org.postalis.training.rh.shared.domain.UUIDv7;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FeriasSolicitadaEvent(
        UUID eventId,
        Instant occurredOn,
        String aggregateId,
        int version,
        UUID funcionarioId,
        LocalDate dataInicio,
        LocalDate dataFim,
        int diasSolicitados
) implements FeriasEvent {

    @Override
    public String eventType() {
        return "FeriasSolicitada";
    }

    @Override
    public String schema() {
        return "rh";
    }

    public static FeriasSolicitadaEvent create(
            String aggregateId,
            UUID funcionarioId,
            LocalDate dataInicio,
            LocalDate dataFim,
            int diasSolicitados) {
        return new FeriasSolicitadaEvent(
                UUIDv7.generate(),
                Instant.now(),
                aggregateId,
                1, // Primeiro evento
                funcionarioId,
                dataInicio,
                dataFim,
                diasSolicitados
        );
    }
}