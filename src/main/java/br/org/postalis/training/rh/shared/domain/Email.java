// src/main/java/br/org/postalis/training/rh/shared/domain/Email.java
package br.org.postalis.training.rh.shared.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object para Email válido.
 */
@Embeddable
public class Email implements Serializable {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private String valor;

    protected Email() {
        // JPA only
    }

    private Email(String valor) {
        this.valor = valor;
    }

    public static Email of(String email) {
        Objects.requireNonNull(email, "Email não pode ser nulo");

        String trimmed = email.trim().toLowerCase();

        if (trimmed.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }

        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Email inválido: " + email);
        }

        return new Email(trimmed);
    }

    public String getValor() {
        return valor;
    }

    public String getDominio() {
        return valor.substring(valor.indexOf('@') + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(valor, email.valor);
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