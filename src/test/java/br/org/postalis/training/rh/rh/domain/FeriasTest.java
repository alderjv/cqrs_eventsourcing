// src/test/java/br/org/postalis/training/rh/rh/domain/FeriasTest.java
package br.org.postalis.training.rh.rh.domain;

import br.org.postalis.training.rh.rh.domain.events.StatusFerias;
import br.org.postalis.training.rh.shared.domain.BusinessRuleException;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeriasTest {

    private final UUID funcionarioId = UUID.randomUUID();
    private final LocalDate inicio = LocalDate.now().plusDays(30);
    private final LocalDate fim = inicio.plusDays(14);

    @Test
    void deveSolicitarFerias() {
        Ferias ferias = Ferias.solicitar(funcionarioId, inicio, fim);

        assertNotNull(ferias.getId());
        assertEquals(StatusFerias.SOLICITADA, ferias.getStatus());
        assertEquals(15, ferias.getDiasSolicitados());
        assertEquals(1, ferias.getUncommittedEvents().size());
    }

    @Test
    void deveAprovarFerias() {
        Ferias ferias = Ferias.solicitar(funcionarioId, inicio, fim);
        ferias.aprovar("gerente@empresa.com", "Aprovado sem restrições");

        assertEquals(StatusFerias.APROVADA, ferias.getStatus());
        assertEquals("gerente@empresa.com", ferias.getAprovadoPor());
    }

    @Test
    void deveRejeitarFerias() {
        Ferias ferias = Ferias.solicitar(funcionarioId, inicio, fim);
        ferias.rejeitar("gerente@empresa.com", "Conflito com projeto crítico");

        assertEquals(StatusFerias.REJEITADA, ferias.getStatus());
        assertEquals("Conflito com projeto crítico", ferias.getMotivoRejeicao());
    }

    @Test
    void deveCompletarCicloCompleto() {
        Ferias ferias = Ferias.solicitar(funcionarioId, inicio, fim);
        ferias.aprovar("gerente", "OK");
        ferias.iniciarGozo();
        ferias.concluir();

        assertEquals(StatusFerias.CONCLUIDA, ferias.getStatus());
        assertEquals(4, ferias.getUncommittedEvents().size()); // 4 transições
    }

    @Test
    void naoDeveAprovarFeriasJaAprovadas() {
        Ferias ferias = Ferias.solicitar(funcionarioId, inicio, fim);
        ferias.aprovar("gerente", "OK");

        assertThrows(BusinessRuleException.class, () ->
                ferias.aprovar("outro", "Tentativa")
        );
    }

    @Test
    void naoDeveIniciarGozoSemAprovacao() {
        Ferias ferias = Ferias.solicitar(funcionarioId, inicio, fim);

        assertThrows(BusinessRuleException.class, () ->
                ferias.iniciarGozo()
        );
    }

    @Test
    void naoDeveSolicitarFeriasMenoresQue5Dias() {
        LocalDate fimCurto = inicio.plusDays(3);

        assertThrows(BusinessRuleException.class, () ->
                Ferias.solicitar(funcionarioId, inicio, fimCurto)
        );
    }

    @Test
    void naoDeveSolicitarFeriasMaioresQue30Dias() {
        LocalDate fimLongo = inicio.plusDays(45);

        assertThrows(BusinessRuleException.class, () ->
                Ferias.solicitar(funcionarioId, inicio, fimLongo)
        );
    }
}