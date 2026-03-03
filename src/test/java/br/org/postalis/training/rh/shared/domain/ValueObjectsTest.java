// src/test/java/br/org/postalis/training/rh/shared/domain/ValueObjectsTest.java
package br.org.postalis.training.rh.shared.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValueObjectsTest {

    // ========== CPF ==========

    @Test
    void deveCriarCpfValido() {
        Cpf cpf = Cpf.of("529.982.247-25");

        assertEquals("52998224725", cpf.getValor());
        assertEquals("529.982.247-25", cpf.getFormatado());
    }

    @Test
    void deveCriarCpfSemFormatacao() {
        Cpf cpf = Cpf.of("52998224725");

        assertEquals("52998224725", cpf.getValor());
    }

    @ParameterizedTest
    @ValueSource(strings = {"00000000000", "11111111111", "12345678901", "123"})
    void deveRejeitarCpfInvalido(String cpf) {
        assertThrows(IllegalArgumentException.class, () -> Cpf.of(cpf));
    }

    @Test
    void cpfsComMesmoValorSaoIguais() {
        Cpf cpf1 = Cpf.of("529.982.247-25");
        Cpf cpf2 = Cpf.of("52998224725");

        assertEquals(cpf1, cpf2);
        assertEquals(cpf1.hashCode(), cpf2.hashCode());
    }

    // ========== Email ==========

    @Test
    void deveCriarEmailValido() {
        Email email = Email.of("joao.silva@empresa.com.br");

        assertEquals("joao.silva@empresa.com.br", email.getValor());
        assertEquals("empresa.com.br", email.getDominio());
    }

    @Test
    void deveConverterEmailParaMinusculo() {
        Email email = Email.of("JOAO@EMPRESA.COM");

        assertEquals("joao@empresa.com", email.getValor());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalido", "sem@dominio", "@semlocal.com"})
    void deveRejeitarEmailInvalido(String email) {
        assertThrows(IllegalArgumentException.class, () -> Email.of(email));
    }

    // ========== Matrícula ==========

    @Test
    void deveCriarMatriculaValida() {
        Matricula mat = Matricula.of("000123");

        assertEquals("000123", mat.getValor());
        assertEquals(123, mat.getNumero());
    }

    @Test
    void deveGerarMatricula() {
        Matricula mat = Matricula.gerar(1);

        assertEquals("000001", mat.getValor());
    }

    @Test
    void deveRejeitarMatriculaInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> Matricula.of("123"));      // muito curta
        assertThrows(IllegalArgumentException.class,
                () -> Matricula.of("1234567"));  // muito longa
        assertThrows(IllegalArgumentException.class,
                () -> Matricula.of("12345A"));   // com letra
    }
}