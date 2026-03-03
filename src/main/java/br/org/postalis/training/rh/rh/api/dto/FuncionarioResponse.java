// src/main/java/br/org/postalis/training/rh/rh/api/dto/FuncionarioResponse.java
package br.org.postalis.training.rh.rh.api.dto;

import br.org.postalis.training.rh.rh.infrastructure.FuncionarioJpaEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FuncionarioResponse(
        UUID id,
        String cpf,
        String nome,
        String email,
        String matricula,
        String cargo,
        BigDecimal salario,
        LocalDate dataContratacao,
        boolean ativo
) {
    public static FuncionarioResponse from(FuncionarioJpaEntity entity) {
        return new FuncionarioResponse(
                entity.getId(),
                entity.getCpf(),
                entity.getNome(),
                entity.getEmail(),
                entity.getMatricula(),
                entity.getCargo(),
                entity.getSalario(),
                entity.getDataAdimissao(),
                entity.isAtivo()
        );
    }
}