// src/main/java/br/org/postalis/training/rh/shared/domain/Cpf.java
package br.org.postalis.training.rh.shared.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object para CPF válido.
 *
 * Pode ser usado tanto em Aggregates quanto em entidades JPA.
 * Armazena apenas os 11 dígitos (sem pontos/traço).
 */
@Embeddable
public class Cpf implements Serializable {

    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");

    private String valor;

    /**
     * Construtor para JPA - NÃO usar diretamente.
     */
    protected Cpf() {
        // JPA only
    }

    private Cpf(String valor) {
        this.valor = valor;
    }

    /**
     * Factory method - cria CPF validando.
     * Aceita com ou sem formatação.
     *
     * @param cpf string com CPF (ex: "529.982.247-25" ou "52998224725")
     * @return Cpf validado
     * @throws IllegalArgumentException se CPF for inválido
     */
    public static Cpf of(String cpf) {
        Objects.requireNonNull(cpf, "CPF não pode ser nulo");

        // Remove formatação (pontos, traços, espaços)
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (!CPF_PATTERN.matcher(cpfLimpo).matches()) {
            throw new IllegalArgumentException(
                    "CPF deve conter exatamente 11 dígitos numéricos");
        }

        if (isCpfInvalido(cpfLimpo)) {
            throw new IllegalArgumentException("CPF inválido: " + cpf);
        }

        if (!validarDigitosVerificadores(cpfLimpo)) {
            throw new IllegalArgumentException(
                    "CPF com dígitos verificadores inválidos: " + cpf);
        }

        return new Cpf(cpfLimpo);
    }

    /**
     * Verifica se CPF tem todos os dígitos iguais (ex: 111.111.111-11).
     */
    private static boolean isCpfInvalido(String cpf) {
        return cpf.chars().distinct().count() == 1;
    }

    /**
     * Valida dígitos verificadores usando algoritmo oficial.
     */
    private static boolean validarDigitosVerificadores(String cpf) {
        try {
            int primeiroDigito = calcularDigitoVerificador(cpf.substring(0, 9));
            int segundoDigito = calcularDigitoVerificador(
                    cpf.substring(0, 9) + primeiroDigito);

            return cpf.equals(cpf.substring(0, 9) + primeiroDigito + segundoDigito);
        } catch (Exception e) {
            return false;
        }
    }

    private static int calcularDigitoVerificador(String base) {
        int soma = 0;
        int peso = base.length() + 1;

        for (char c : base.toCharArray()) {
            soma += Character.getNumericValue(c) * peso--;
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /**
     * Retorna apenas os 11 dígitos.
     */
    public String getValor() {
        return valor;
    }

    /**
     * Retorna formatado: 000.000.000-00
     */
    public String getFormatado() {
        if (valor == null || valor.length() != 11) {
            return valor;
        }
        return String.format("%s.%s.%s-%s",
                valor.substring(0, 3),
                valor.substring(3, 6),
                valor.substring(6, 9),
                valor.substring(9, 11)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cpf cpf = (Cpf) o;
        return Objects.equals(valor, cpf.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return getFormatado();
    }
}