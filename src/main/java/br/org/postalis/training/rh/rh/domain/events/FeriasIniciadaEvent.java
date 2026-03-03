// src/main/java/br/org/postalis/training/rh/rh/domain/FeriasIniciadaEvent.java
package br.org.postalis.training.rh.rh.domain.events;

import br.org.postalis.training.rh.shared.domain.UUIDv7;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FeriasIniciadaEvent(
        UUID eventId,
        Instant occurredOn,
        String aggregateId,
        int version,
        LocalDate dataInicioReal
) implements FeriasEvent {

    @Override
    public String eventType() {
        return "FeriasIniciada";
    }

    @Override
    public String schema() {
        return "rh";
    }

    public static FeriasIniciadaEvent create(String aggregateId, int version) {
        return new FeriasIniciadaEvent(
                UUIDv7.generate(),
                Instant.now(),
                aggregateId,
                version,
                LocalDate.now()
        );
    }
}