// src/main/java/br/org/postalis/training/rh/rh/domain/FeriasConcluidaEvent.java
package br.org.postalis.training.rh.rh.domain.events;

import br.org.postalis.training.rh.shared.domain.UUIDv7;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FeriasConcluidaEvent(
        UUID eventId,
        Instant occurredOn,
        String aggregateId,
        int version,
        LocalDate dataRetorno
) implements FeriasEvent {

    @Override
    public String eventType() {
        return "FeriasConcluida";
    }

    @Override
    public String schema() {
        return "rh";
    }

    public static FeriasConcluidaEvent create(String aggregateId, int version) {
        return new FeriasConcluidaEvent(
                UUIDv7.generate(),
                Instant.now(),
                aggregateId,
                version,
                LocalDate.now()
        );
    }
}