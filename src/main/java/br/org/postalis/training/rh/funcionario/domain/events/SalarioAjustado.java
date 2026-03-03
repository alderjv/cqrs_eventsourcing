// src/main/java/br/org/postalis/training/rh/funcionario/domain/events/SalarioAjustado.java
package br.org.postalis.training.rh.funcionario.domain.events;

import br.org.postalis.training.rh.shared.domain.EventMetadata;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SalarioAjustado(
        UUID eventId,
        Instant occurredOn,
        UUID funcionarioId,
        BigDecimal salarioAnterior,
        BigDecimal salarioNovo,
        String motivo,
        EventMetadata eventMetadata
) implements FuncionarioEvent {

    public static final int VERSION = 1;

    @Override public String aggregateId() { return funcionarioId.toString(); }
    @Override public String eventType() { return "SalarioAjustado"; }
    @Override public int version() { return VERSION; }
    @Override public String schema() { return "rh"; }
    @Override public EventMetadata metadata() { return eventMetadata; }
}