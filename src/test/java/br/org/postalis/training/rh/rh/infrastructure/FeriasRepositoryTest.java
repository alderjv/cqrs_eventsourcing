// src/test/java/br/org/postalis/training/rh/rh/infrastructure/FeriasRepositoryTest.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.rh.domain.Ferias;
import br.org.postalis.training.rh.rh.domain.events.StatusFerias;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class FeriasRepositoryTest {

    @Autowired
    private FeriasRepository repository;

    @Autowired
    private FeriasQueryRepository feriasQueryRepository;

    @Autowired
    private FuncionarioQueryRepository funcionarioQueryRepository;

    @Test
    void deveSalvarECarregarFerias() {
        // Given
        UUID funcionarioId = funcionarioQueryRepository.findByAtivoTrue().stream().findFirst().get().getId();
        LocalDate inicio = LocalDate.now().plusDays(30);
        LocalDate fim = inicio.plusDays(14);

        Ferias ferias = Ferias.solicitar(funcionarioId, inicio, fim);
        repository.save(ferias);

        // When
        Ferias carregada = repository.findById(ferias.getId()).orElseThrow();

        // Then
        assertEquals(StatusFerias.SOLICITADA, carregada.getStatus());
        assertEquals(15, carregada.getDiasSolicitados());
    }

    @Test
    void devePersistirCicloCompleto() {
        // Given
        UUID funcionarioId = funcionarioQueryRepository.findByAtivoTrue().stream().findFirst().get().getId();
        LocalDate inicio = LocalDate.now().plusDays(30);
        LocalDate fim = inicio.plusDays(9);

        Ferias ferias = Ferias.solicitar(funcionarioId, inicio, fim);
        repository.save(ferias);

        // When: aprovar
        ferias = repository.findById(ferias.getId()).orElseThrow();
        ferias.aprovar("gerente@empresa.com", "Aprovado");
        repository.save(ferias);

        // Then
        ferias = repository.findById(ferias.getId()).orElseThrow();
        assertEquals(StatusFerias.APROVADA, ferias.getStatus());

        // When: iniciar gozo
        ferias.iniciarGozo();
        repository.save(ferias);

        // Then
        ferias = repository.findById(ferias.getId()).orElseThrow();
        assertEquals(StatusFerias.EM_GOZO, ferias.getStatus());

        // When: concluir
        ferias.concluir();
        repository.save(ferias);

        // Then
        ferias = repository.findById(ferias.getId()).orElseThrow();
        assertEquals(StatusFerias.CONCLUIDA, ferias.getStatus());
        assertEquals(4, ferias.getVersion()); // 4 eventos
    }

    @Test
    void deveProjetarParaQueryRepository() {
        // Given
        UUID funcionarioId = funcionarioQueryRepository.findByAtivoTrue().stream().findFirst().get().getId();
        LocalDate inicio = LocalDate.now().plusDays(30);
        LocalDate fim = inicio.plusDays(14);

        Ferias ferias = Ferias.solicitar(funcionarioId, inicio, fim);
        ferias.aprovar("gerente", "OK");
        repository.save(ferias);

        // When
        var entity = feriasQueryRepository.findById(ferias.getId()).orElseThrow();

        // Then
        assertEquals(StatusFerias.APROVADA, entity.getStatus());
        assertEquals("gerente", entity.getAprovadoPor());
        assertEquals(15, entity.getDiasSolicitados());
    }
}