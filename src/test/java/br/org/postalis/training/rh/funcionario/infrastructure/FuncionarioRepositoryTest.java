// src/test/java/br/org/postalis/training/rh/funcionario/infrastructure/FuncionarioRepositoryTest.java
package br.org.postalis.training.rh.funcionario.infrastructure;

import br.org.postalis.training.rh.funcionario.domain.Funcionario;
import br.org.postalis.training.rh.rh.infrastructure.FuncionarioRepository;
import br.org.postalis.training.rh.shared.domain.Cpf;
import br.org.postalis.training.rh.shared.domain.Email;
import br.org.postalis.training.rh.shared.domain.Matricula;
import br.org.postalis.training.rh.shared.domain.UUIDv7;
import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.AggregateSnapshot;
import br.org.postalis.training.rh.shared.infrastructure.eventsourcing.SnapshotRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class FuncionarioRepositoryTest {

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private SnapshotRepository snapshotRepository;

    @Test
    void deveSalvarECarregarFuncionario() {
        // Given
        Funcionario funcionario = Funcionario.contratar(
                Cpf.of("710.840.291-20"),
                "João Silva",
                Email.of("joao@empresa.com"),
                Matricula.gerar(2),
                "Desenvolvedor",
                new BigDecimal("5000.00")
        );

        // When
        repository.save(funcionario);

        // Then
        Optional<Funcionario> encontrado = repository.findById(funcionario.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("João Silva", encontrado.get().getNome());
        assertEquals("Desenvolvedor", encontrado.get().getCargo());
        assertTrue(encontrado.get().isAtivo());

        // uncommittedEvents deve estar vazia após load
        assertEquals(0, encontrado.get().getUncommittedEvents().size());
    }

    @Test
    void deveRetornarVazioParaIdInexistente() {
        Optional<Funcionario> encontrado = repository.findById(UUIDv7.generate());

        assertTrue(encontrado.isEmpty());
    }

    @Test
    void deveCarregarViaSnapshot() {
        // Given: Criar funcionário e muitas promoções
        Funcionario func = Funcionario.contratar(
                Cpf.of("710.840.291-20"),
                "João Silva",
                Email.of("joao@empresa.com"),
                Matricula.gerar(2),
                "Desenvolvedor",
                new BigDecimal("5000.00")
        );
        repository.save(func);

        // Adicionar 99 promoções (total 100 eventos)
        for (int i = 1; i < 100; i++) {
            func = repository.findById(func.getId()).get();
            func.promover("Cargo " + i, new BigDecimal(5000 + i * 100));
            repository.save(func);
        }

        // When: Buscar
        Funcionario carregado = repository.findById(func.getId()).get();

        // Then: Deve ter carregado via snapshot
        assertEquals(100, carregado.getVersion());

        // Verificar que snapshot foi criado
        Optional<AggregateSnapshot> snapshot =
                snapshotRepository.findLatest(func.getId().toString());
        assertTrue(snapshot.isPresent());
        assertEquals(100, snapshot.get().version());
    }
}