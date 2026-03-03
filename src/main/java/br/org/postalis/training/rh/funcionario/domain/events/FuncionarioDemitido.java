// src/main/java/br/org/postalis/training/rh/funcionario/domain/events/FuncionarioDemitido.java
package br.org.postalis.training.rh.funcionario.domain.events;

import br.org.postalis.training.rh.shared.domain.EventMetadata;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FuncionarioDemitido(
        UUID eventId,
        Instant occurredOn,
        UUID funcionarioId,
        LocalDate dataDemissao,
        String motivo,
        EventMetadata eventMetadata
) implements FuncionarioEvent {

    public static final int VERSION = 1;

    @Override public String aggregateId() { return funcionarioId.toString(); }
    @Override public String eventType() { return "FuncionarioDemitido"; }
    @Override public int version() { return VERSION; }
    @Override public String schema() { return "rh"; }
    @Override public EventMetadata metadata() { return eventMetadata; }
}