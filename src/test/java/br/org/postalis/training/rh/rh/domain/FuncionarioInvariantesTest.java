// src/test/java/br/org/postalis/training/rh/rh/domain/FuncionarioInvariantesTest.java
package br.org.postalis.training.rh.rh.domain;

import br.org.postalis.training.rh.funcionario.domain.Funcionario;
import br.org.postalis.training.rh.shared.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioInvariantesTest {

    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        funcionario = Funcionario.contratar(
                Cpf.of("529.982.247-25"),
                "João Silva",
                Email.of("joao@empresa.com"),
                Matricula.gerar(1),
                "Desenvolvedor",
                new BigDecimal("5000.00"),
                "Pos-graduação"
        );
    }

    // === Testes de Contratação ===

    @Test
    void naoDeveContratarComCpfNulo() {
        assertThrows(NullPointerException.class, () ->
                Funcionario.contratar(
                        null,  // CPF nulo
                        "Nome",
                        Email.of("email@test.com"),
                        Matricula.gerar(1),
                        "Cargo",
                        new BigDecimal("1000"),
                        "Pos-graduação"
                )
        );
    }

    @Test
    void naoDeveContratarComSalarioZero() {
        assertThrows(IllegalArgumentException.class, () ->
                Funcionario.contratar(
                        Cpf.of("529.982.247-25"),
                        "Nome",
                        Email.of("email@test.com"),
                        Matricula.gerar(1),
                        "Cargo",
                        BigDecimal.ZERO,  // Salário zero
                        "Pos-graduação"
                )
        );
    }

    @Test
    void naoDeveContratarComSalarioNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
                Funcionario.contratar(
                        Cpf.of("529.982.247-25"),
                        "Nome",
                        Email.of("email@test.com"),
                        Matricula.gerar(1),
                        "Cargo",
                        new BigDecimal("-100"),  // Salário negativo
                        "Pos-graduação"
                )
        );
    }

    // === Testes de Promoção ===

    @Test
    void devePromoverComSalarioMaior() {
        funcionario.promover("Senior", new BigDecimal("8000.00"));

        assertEquals("Senior", funcionario.getCargo());
        assertEquals(new BigDecimal("8000.00"), funcionario.getSalario());
    }

    @Test
    void naoDevePromoverComSalarioMenor() {
        var ex = assertThrows(BusinessRuleException.class, () ->
                funcionario.promover("Junior", new BigDecimal("3000.00"))
        );

        assertTrue(ex.getMessage().contains("maior que o atual"));
    }

    @Test
    void naoDevePromoverComSalarioIgual() {
        var ex = assertThrows(BusinessRuleException.class, () ->
                funcionario.promover("Mesmo Cargo", new BigDecimal("5000.00"))
        );

        assertTrue(ex.getMessage().contains("maior que o atual"));
    }

    @Test
    void naoDevePromoverFuncionarioInativo() {
        funcionario.desligar("Pedido de demissão voluntária");

        var ex = assertThrows(BusinessRuleException.class, () ->
                funcionario.promover("Diretor", new BigDecimal("50000.00"))
        );

        assertTrue(ex.getMessage().contains("inativo"));
    }

    // === Testes de Desligamento ===

    @Test
    void deveDesligarComMotivo() {
        funcionario.desligar("Redução de quadro por reestruturação");

        assertFalse(funcionario.isAtivo());
        assertNotNull(funcionario.getDataDesligamento());
    }

    @Test
    void naoDeveDesligarSemMotivo() {
        assertThrows(IllegalArgumentException.class, () ->
                funcionario.desligar("")
        );
    }

    @Test
    void naoDeveDesligarComMotivoMuitoCurto() {
        var ex = assertThrows(IllegalArgumentException.class, () ->
                funcionario.desligar("curto")
        );

        assertTrue(ex.getMessage().contains("10 caracteres"));
    }

    @Test
    void naoDeveDesligarDuasVezes() {
        funcionario.desligar("Primeiro desligamento válido");

        var ex = assertThrows(BusinessRuleException.class, () ->
                funcionario.desligar("Segundo desligamento")
        );

        assertTrue(ex.getMessage().contains("já está desligado"));
    }

    // === Testes de Imutabilidade ===

    @Test
    void estadoNaoMudaSemEvento() {
        String cargoOriginal = funcionario.getCargo();
        BigDecimal salarioOriginal = funcionario.getSalario();

        // Sem chamar nenhum método de domínio, o estado não muda
        assertEquals(cargoOriginal, funcionario.getCargo());
        assertEquals(salarioOriginal, funcionario.getSalario());
    }

    @Test
    void eventosGeradosCorretamente() {
        // Contratação gera 1 evento
        assertEquals(1, funcionario.getUncommittedEvents().size());

        funcionario.promover("Senior", new BigDecimal("8000"));
        // Agora temos 2 eventos
        assertEquals(2, funcionario.getUncommittedEvents().size());

        funcionario.desligar("Aposentadoria por tempo de serviço");
        // Agora temos 3 eventos
        assertEquals(3, funcionario.getUncommittedEvents().size());
    }
}