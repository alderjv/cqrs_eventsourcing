// src/main/java/br/org/postalis/training/rh/rh/domain/FeriasRejeitadaEvent.java
package br.org.postalis.training.rh.rh.domain.events;

import br.org.postalis.training.rh.shared.domain.UUIDv7;

import java.time.Instant;
import java.util.UUID;

public record FeriasRejeitadaEvent(
        UUID eventId,
        Instant occurredOn,
        String aggregateId,
        int version,
        String rejeitadoPor,
        String motivo
) implements FeriasEvent {

    @Override
    public String eventType() {
        return "FeriasRejeitada";
    }

    @Override
    public String schema() {
        return "rh";
    }

    public static FeriasRejeitadaEvent create(
            String aggregateId,
            int version,
            String rejeitadoPor,
            String motivo) {
        return new FeriasRejeitadaEvent(
                UUIDv7.generate(),
                Instant.now(),
                aggregateId,
                version,
                rejeitadoPor,
                motivo
        );
    }
}