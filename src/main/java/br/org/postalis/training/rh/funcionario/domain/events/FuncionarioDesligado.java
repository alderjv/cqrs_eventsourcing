// src/main/java/br/org/postalis/training/rh/rh/domain/FuncionarioDesligado.java
package br.org.postalis.training.rh.funcionario.domain.events;

import br.org.postalis.training.rh.shared.domain.UUIDv7;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FuncionarioDesligado(
        UUID eventId,
        Instant occurredOn,
        String aggregateId,
        int version,
        String motivo,
        LocalDate dataDesligamento
) implements FuncionarioEvent {

    @Override
    public String eventType() {
        return "FuncionarioDesligado";
    }

    @Override
    public String schema() {
        return "rh";
    }


    public static FuncionarioDesligado create(
            String aggregateId,
            int version,
            String motivo) {
        return new FuncionarioDesligado(
                UUIDv7.generate(),
                Instant.now(),
                aggregateId,
                version,
                motivo,
                LocalDate.now()
        );
    }
}