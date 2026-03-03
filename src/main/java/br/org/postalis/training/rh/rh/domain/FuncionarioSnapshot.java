// src/main/java/br/org/postalis/training/rh/rh/domain/FuncionarioSnapshot.java
package br.org.postalis.training.rh.rh.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FuncionarioSnapshot(
        UUID id,
        String cpf,
        String nome,
        String email,
        String matricula,
        String cargo,
        BigDecimal salario,
        LocalDate dataAdmissao,
        LocalDate dataDesligamento,
        boolean ativo
) {
}