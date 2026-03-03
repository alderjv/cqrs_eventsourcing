// src/main/java/br/org/postalis/training/rh/rh/domain/FeriasAprovadaEvent.java
package br.org.postalis.training.rh.rh.domain.events;

import br.org.postalis.training.rh.shared.domain.UUIDv7;

import java.time.Instant;
import java.util.UUID;

public record FeriasAprovadaEvent(
        UUID eventId,
        Instant occurredOn,
        String aggregateId,
        int version,
        String aprovadoPor,
        String observacao
) implements FeriasEvent {

    @Override
    public String eventType() {
        return "FeriasAprovada";
    }

    @Override
    public String schema() {
        return "rh";
    }

    public static FeriasAprovadaEvent create(
            String aggregateId,
            int version,
            String aprovadoPor,
            String observacao) {
        return new FeriasAprovadaEvent(
                UUIDv7.generate(),
                Instant.now(),
                aggregateId,
                version,
                aprovadoPor,
                observacao
        );
    }
}