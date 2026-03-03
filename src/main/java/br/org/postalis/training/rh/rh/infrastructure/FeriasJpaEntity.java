// src/main/java/br/org/postalis/training/rh/rh/infrastructure/FeriasJpaEntity.java
package br.org.postalis.training.rh.rh.infrastructure;

import br.org.postalis.training.rh.rh.domain.events.StatusFerias;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "ferias", schema = "rh")
@Getter
public class FeriasJpaEntity {

    @Id
    private UUID id;

    @Column(name = "funcionario_id")
    private UUID funcionarioId;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "dias_solicitados")
    private int diasSolicitados;

    @Enumerated(EnumType.STRING)
    private StatusFerias status;

    @Column(name = "aprovado_por")
    private String aprovadoPor;

    @Column(name = "rejeitado_por")
    private String rejeitadoPor;

    @Column(name = "motivo_rejeicao")
    private String motivoRejeicao;

    @Column(name = "data_inicio_real")
    private LocalDate dataInicioReal;

    @Column(name = "data_retorno")
    private LocalDate dataRetorno;


}