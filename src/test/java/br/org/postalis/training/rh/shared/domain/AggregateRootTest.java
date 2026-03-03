// src/test/java/br/org/postalis/training/rh/shared/domain/AggregateRootTest.java
package br.org.postalis.training.rh.shared.domain;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AggregateRootTest {

    // Aggregate de teste (implementação mínima)
    static class TestAggregate extends AggregateRoot {
        private String nome;  // Apenas estado mutável (ID herdado de Entity)

        protected TestAggregate(UUID id) {
            super(id);
        }

        public void criar(String nome) {
            raise(new TestCriadoEvent(
                    UUIDv7.generate(),
                    Instant.now(),
                    getId().toString(),  // Usa ID da superclasse
                    "TestCriado",
                    1,
                    "rh",
                    nome
            ));
        }

        @Override
        protected void apply(DomainEvent event) {
            if (event instanceof TestCriadoEvent e) {
                this.nome = e.nome();
                // NÃO redefine ID - já está na superclasse Entity
            }
        }

        public String getNome() { return nome; }
        // getId() herdado de Entity - não precisa redefinir
    }

    // Evento de teste
    record TestCriadoEvent(
            UUID eventId,
            Instant occurredOn,
            String aggregateId,
            String eventType,
            int version,
            String schema,
            String nome
    ) implements DomainEvent {
        @Override public EventMetadata metadata() { return null; }
    }

    @Test
    void deveGerarEventoAoCriar() {
        UUID id = UUIDv7.generate();
        TestAggregate agg = new TestAggregate(id);
        agg.criar("Teste");

        assertEquals(1, agg.getUncommittedEvents().size());
        assertEquals("Teste", agg.getNome());
        assertEquals(id, agg.getId());
    }

    @Test
    void deveReconstruirDeEventos() {
        UUID id = UUIDv7.generate();
        TestAggregate agg = criarAggregateDeEventos(id);

        assertEquals(id, agg.getId());
        assertEquals("Reconstruido", agg.getNome());
        assertEquals(0, agg.getUncommittedEvents().size());
    }

    @Test
    void deveLimparEventosAposCommit() {
        UUID id = UUIDv7.generate();
        TestAggregate agg = criarAggregateDeEventos(id);
        agg.criar("Teste");

        assertEquals(1, agg.getUncommittedEvents().size());

        agg.markEventsAsCommitted();

        assertEquals(0, agg.getUncommittedEvents().size());
    }

    // Helper: cria aggregate a partir de eventos históricos
    private TestAggregate criarAggregateDeEventos(UUID aggregateId) {
        var evento = new TestCriadoEvent(
                UUIDv7.generate(),
                Instant.now(),
                aggregateId.toString(),
                "TestCriado",
                1,
                "rh",
                "Reconstruido"
        );

        return AggregateRoot.fromEvents(
                List.of(evento),
                () -> new TestAggregate(UUID.fromString(evento.aggregateId()))
        );
    }
}