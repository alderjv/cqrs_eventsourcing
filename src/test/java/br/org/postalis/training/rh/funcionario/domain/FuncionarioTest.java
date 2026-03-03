// src/test/java/br/org/postalis/training/rh/funcionario/domain/FuncionarioTest.java
package br.org.postalis.training.rh.funcionario.domain;

import br.org.postalis.training.rh.funcionario.domain.events.FuncionarioContratado;
import br.org.postalis.training.rh.shared.domain.AggregateRoot;
import br.org.postalis.training.rh.shared.domain.Cpf;
import br.org.postalis.training.rh.shared.domain.DomainEvent;
import br.org.postalis.training.rh.shared.domain.Email;
import br.org.postalis.training.rh.shared.domain.Matricula;
import br.org.postalis.training.rh.shared.domain.UUIDv7;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuncionarioTest {

    @Test
    void deveContratarFuncionario() {
        // When
        Funcionario func = Funcionario.contratar(
                Cpf.of("529.982.247-25"),
                "João Silva",
                Email.of("joao@empresa.com"),
                Matricula.gerar(1),
                "Desenvolvedor",
                new BigDecimal("5000.00")
        );

        // Then
        assertNotNull(func.getId());
        assertEquals("João Silva", func.getNome());
        assertEquals("529.982.247-25", func.getCpf().getFormatado());
        assertEquals("Desenvolvedor", func.getCargo());
        assertEquals(new BigDecimal("5000.00"), func.getSalario());
        assertTrue(func.isAtivo());
        assertNotNull(func.getDataAdmissao());
    }

    @Test
    void deveGerarEventoAoContratar() {
        // When
        Funcionario func = Funcionario.contratar(
                Cpf.of("529.982.247-25"),
                "João Silva",
                Email.of("joao@empresa.com"),
                Matricula.gerar(1),
                "Dev",
                new BigDecimal("5000")
        );

        // Then
        List<DomainEvent> eventos = func.getUncommittedEvents();
        assertEquals(1, eventos.size());

        assertInstanceOf(FuncionarioContratado.class, eventos.get(0));
        FuncionarioContratado evento = (FuncionarioContratado) eventos.get(0);
        assertEquals("João Silva", evento.nome());
        assertEquals("FuncionarioContratado", evento.eventType());
        assertEquals("rh", evento.schema());
        assertEquals(1, evento.version());
    }

    @Test
    void deveReconstruirDeEventos() {
        // Given: um evento salvo
        UUID funcionarioId = UUIDv7.generate();
        var evento = new FuncionarioContratado(
                UUIDv7.generate(),
                Instant.now(),
                funcionarioId,
                "000002",
                "Maria Santos",
                "52998224725",
                "maria@empresa.com",
                LocalDate.now(),
                new BigDecimal("6000"),
                "Analista",
                null
        );

        // When: reconstruímos o aggregate
        // NOTA: Supplier passa o aggregateId do evento para o construtor
        Funcionario func = AggregateRoot.fromEvents(
                List.of(evento),
                () -> new Funcionario(UUID.fromString(evento.aggregateId()))
        );

        // Then
        assertEquals(funcionarioId, func.getId());
        assertEquals("Maria Santos", func.getNome());
        assertEquals("Analista", func.getCargo());
        assertTrue(func.isAtivo());

        // uncommittedEvents está vazia (eventos vieram do banco)
        assertEquals(0, func.getUncommittedEvents().size());
    }

    @Test
    void naoDeveContratarSemNome() {
        assertThrows(NullPointerException.class, () ->
                Funcionario.contratar(
                        Cpf.of("529.982.247-25"),
                        null,  // nome vazio
                        Email.of("joao@empresa.com"),
                        Matricula.gerar(1),
                        "Dev",
                        new BigDecimal("5000")
                )
        );
    }

    @Test
    void naoDeveContratarComSalarioNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
                Funcionario.contratar(
                        Cpf.of("529.982.247-25"),
                        "João",
                        Email.of("joao@empresa.com"),
                        Matricula.gerar(1),
                        "Dev",
                        new BigDecimal("-100")  // salário negativo
                )
        );
    }

    @Test
    void cicloCompletoEventSourcing() {
        // 1. CRIAR - Gera evento
        Funcionario original = Funcionario.contratar(
                Cpf.of("529.982.247-25"),
                "João Silva",
                Email.of("joao@empresa.com"),
                Matricula.gerar(1),
                "Dev",
                new BigDecimal("5000")
        );

        // 2. SIMULAR PERSISTÊNCIA - Pega os eventos
        List<DomainEvent> eventos = new ArrayList<>(original.getUncommittedEvents());
        original.markEventsAsCommitted();

        // 3. RECONSTRUIR - Cria novo Aggregate dos eventos
        // NOTA: Extraímos o aggregateId do primeiro evento para passar ao construtor
        UUID aggregateId = UUID.fromString(eventos.get(0).aggregateId());
        Funcionario reconstruido = AggregateRoot.fromEvents(
                eventos,
                () -> new Funcionario(aggregateId)
        );

        // 4. VALIDAR - Estado é idêntico
        assertEquals(original.getId(), reconstruido.getId());
        assertEquals(original.getNome(), reconstruido.getNome());
        assertEquals(original.getCpf(), reconstruido.getCpf());
        assertEquals(original.getCargo(), reconstruido.getCargo());
        assertEquals(original.getSalario(), reconstruido.getSalario());
        assertEquals(original.isAtivo(), reconstruido.isAtivo());

        // 5. VALIDAR - uncommittedEvents
        assertEquals(0, original.getUncommittedEvents().size());
        assertEquals(0, reconstruido.getUncommittedEvents().size());
    }
}