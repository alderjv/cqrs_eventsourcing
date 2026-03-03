// src/main/java/br/org/postalis/training/rh/shared/domain/Matricula.java
package br.org.postalis.training.rh.shared.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object para Matrícula de funcionário.
 *
 * Formato: 6 dígitos (ex: 000001, 000002)
 */
@Embeddable
public class Matricula implements Serializable {

    private String valor;

    protected Matricula() {
        // JPA only
    }

    private Matricula(String valor) {
        this.valor = valor;
    }

    /**
     * Cria matrícula a partir de string.
     */
    public static Matricula of(String matricula) {
        Objects.requireNonNull(matricula, "Matrícula não pode ser nula");

        String trimmed = matricula.trim();

        if (!trimmed.matches("\\d{6}")) {
            throw new IllegalArgumentException(
                    "Matrícula deve ter 6 dígitos: " + matricula);
        }

        return new Matricula(trimmed);
    }

    /**
     * Gera matrícula a partir de número sequencial.
     */
    public static Matricula gerar(int sequencial) {
        if (sequencial < 1 || sequencial > 999999) {
            throw new IllegalArgumentException(
                    "Sequencial deve estar entre 1 e 999999");
        }
        return new Matricula(String.format("%06d", sequencial));
    }

    public String getValor() {
        return valor;
    }

    public int getNumero() {
        return Integer.parseInt(valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matricula that = (Matricula) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}