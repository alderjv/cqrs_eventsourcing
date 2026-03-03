// src/test/java/br/org/postalis/training/rh/shared/domain/DomainEventTest.java
package br.org.postalis.training.rh.shared.domain;

import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratado;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DomainEventTest {

    @Test
    void deveCriarEventoComTodosOsCamposObrigatorios() {
        var eventId = UUID.randomUUID();
        var funcionarioId = UUID.randomUUID();
        var now = Instant.now();

        var evento = new FuncionarioContratado(
                eventId,
                now,
                funcionarioId,
                "000001",
                "João Silva",
                "52998224725",
                "joao@empresa.com",
                LocalDate.of(2024, 1, 15),
                new BigDecimal("5000.00"),
                "Analista",
                null  // metadata opcional
        );

        assertEquals(eventId, evento.eventId());
        assertEquals(now, evento.occurredOn());
        assertEquals(funcionarioId.toString(), evento.aggregateId());
        assertEquals("FuncionarioContratado", evento.eventType());
        assertEquals(1, evento.version());
        assertEquals("rh", evento.schema());
        assertNull(evento.metadata());
    }

    @Test
    void deveRejeitarEventoComSalarioInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                new FuncionarioContratado(
                        UUID.randomUUID(),
                        Instant.now(),
                        UUID.randomUUID(),
                        "000001",
                        "João Silva",
                        "52998224725",
                        "joao@empresa.com",
                        LocalDate.of(2024, 1, 15),
                        BigDecimal.ZERO,  // salário inválido
                        "Analista",
                        null
                )
        );
    }

    @Test
    void deveCriarMetadataComBuilder() {
        var correlationId = UUID.randomUUID();

        var metadata = EventMetadata.builder()
                .actorId("user-123")
                .correlationId(correlationId)
                .source("SYNC")
                .commandType("ContratarFuncionarioCommand")
                .build();

        assertEquals("user-123", metadata.getActorId());
        assertEquals(correlationId, metadata.getCorrelationId());
        assertEquals("SYNC", metadata.getSource());
        assertEquals("ContratarFuncionarioCommand", metadata.getCommandType());
        assertNotNull(metadata.getTimestamp());
    }

    @Test
    void metadataBuilderDeveTerDefaultsSensatos() {
        var metadata = EventMetadata.builder().build();

        assertNotNull(metadata.getCorrelationId());
        assertEquals("SYNC", metadata.getSource());
        assertNotNull(metadata.getTimestamp());
    }
}