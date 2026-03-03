// src/test/java/br/org/postalis/training/rh/shared/domain/DomainEventTest.java
package br.org.postalis.training.rh.shared.domain;

import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratado;
import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratadoV1;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DomainEventTest {

    @Test
    void deveCriarEventoComTodosOsCamposObrigatorios() {
        var eventId = UUID.randomUUID();
        var funcionarioId = UUID.randomUUID();
        var now = Instant.now();

        var evento = new FuncionarioContratadoV1(
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
        assertEquals(2, evento.version());
        assertEquals("rh", evento.schema());
        assertNull(evento.metadata());

        var eventoV1 = new FuncionarioContratadoV1(
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

        assertEquals(eventId, eventoV1.eventId());
        assertEquals(now, eventoV1.occurredOn());
        assertEquals(funcionarioId.toString(), eventoV1.aggregateId());
        assertEquals("FuncionarioContratado", eventoV1.eventType());
        assertEquals(1, eventoV1.version());
        assertEquals("rh", eventoV1.schema());
        assertNull(eventoV1.metadata());


    }

    @Test
    void deveRejeitarEventoComSalarioInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                new FuncionarioContratadoV1(
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
                        "pos-gradiação",
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