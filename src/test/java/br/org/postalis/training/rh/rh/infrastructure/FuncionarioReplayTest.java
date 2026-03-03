// src/test/java/br/org/postalis/training/rh/rh/infrastructure/FuncionarioReplayTest.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.funcionario.domain.Funcionario;
import br.org.postalis.training.rh.shared.domain.BusinessRuleException;
import br.org.postalis.training.rh.shared.domain.Cpf;
import br.org.postalis.training.rh.shared.domain.Email;
import br.org.postalis.training.rh.shared.domain.Matricula;
import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.EventStore;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class FuncionarioReplayTest {

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private EventStore eventStore;

    @Test
    void deveReconstruirFuncionarioDeEventos() {
        // Given: criar funcionário com histórico
        Funcionario original = Funcionario.contratar(
                Cpf.of("710.840.291-20"),
                "Ana Costa",
                Email.of("ana@empresa.com"),
                Matricula.gerar(100),
                "Analista Junior",
                new BigDecimal("4000.00")
        );

        original.promover("Analista Pleno", new BigDecimal("6000.00"));
        original.promover("Analista Senior", new BigDecimal("9000.00"));

        repository.save(original);

        // When: carregar do banco (reconstrói de eventos)
        Funcionario reconstruido = repository.findById(original.getId())
                .orElseThrow();

        // Then: estado deve ser igual
        assertEquals(original.getId(), reconstruido.getId());
        assertEquals("Ana Costa", reconstruido.getNome());
        assertEquals("Analista Senior", reconstruido.getCargo());
        assertEquals(new BigDecimal("9000.00"), reconstruido.getSalario());
        assertTrue(reconstruido.isAtivo());
        assertEquals(3, reconstruido.getVersion()); // 3 eventos

        // E: deve estar sem eventos pendentes
        assertEquals(0, reconstruido.getUncommittedEvents().size());
    }

    @Test
    void deveReconstruirFuncionarioDesligado() {
        // Given
        Funcionario original = Funcionario.contratar(
                Cpf.of("710.840.291-20"),
                "Pedro Santos",
                Email.of("pedro@empresa.com"),
                Matricula.gerar(101),
                "Desenvolvedor",
                new BigDecimal("5000.00")
        );

        original.desligar("Término de contrato temporário");
        repository.save(original);

        // When
        Funcionario reconstruido = repository.findById(original.getId())
                .orElseThrow();

        // Then
        assertFalse(original.isAtivo());
        assertNotNull(original.getDataDesligamento());

        // E: não deve permitir promoção
        assertThrows(BusinessRuleException.class, () ->
                reconstruido.promover("Senior", new BigDecimal("10000"))
        );
    }

    @Test
    void deveManterHistoricoCompleto() {
        // Given
        Funcionario func = Funcionario.contratar(
                Cpf.of("710.840.291-20"),
                "Maria Silva",
                Email.of("maria@empresa.com"),
                Matricula.gerar(102),
                "Estagiária",
                new BigDecimal("1500.00")
        );

        func.promover("Analista Junior", new BigDecimal("3000.00"));
        func.promover("Analista Pleno", new BigDecimal("5000.00"));
        func.promover("Analista Senior", new BigDecimal("8000.00"));
        func.desligar("Proposta irrecusável de outra empresa");

        repository.save(func);

        // When: verificar eventos no banco
        var eventos = eventStore.load(func.getId().toString());

        // Then
        assertEquals(5, eventos.size()); // 1 contratação + 3 promoções + 1 desligamento
        assertEquals("FuncionarioContratado", eventos.get(0).eventType());
        assertEquals("FuncionarioPromovido", eventos.get(1).eventType());
        assertEquals("FuncionarioPromovido", eventos.get(2).eventType());
        assertEquals("FuncionarioPromovido", eventos.get(3).eventType());
        assertEquals("FuncionarioDesligado", eventos.get(4).eventType());


    }
}