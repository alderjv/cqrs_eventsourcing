// src/main/java/br/org/postalis/training/rh/rh/api/dto/FeriasResponse.java
package br.org.postalis.training.rh.rh.api.dto;

import br.org.postalis.training.rh.rh.domain.events.StatusFerias;
import br.org.postalis.training.rh.rh.infrastructure.FeriasJpaEntity;
import java.time.LocalDate;
import java.util.UUID;

public record FeriasResponse(
        UUID id,
        UUID funcionarioId,
        LocalDate dataInicio,
        LocalDate dataFim,
        int diasSolicitados,
        StatusFerias status,
        String aprovadoPor,
        String motivoRejeicao
) {
    public static FeriasResponse from(FeriasJpaEntity entity) {
        return new FeriasResponse(
                entity.getId(),
                entity.getFuncionarioId(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getDiasSolicitados(),
                entity.getStatus(),
                entity.getAprovadoPor(),
                entity.getMotivoRejeicao()
        );
    }
}