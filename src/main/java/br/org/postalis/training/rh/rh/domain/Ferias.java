// src/main/java/br/org/postalis/training/rh/rh/domain/Ferias.java
package br.org.postalis.training.rh.rh.domain;

import br.org.postalis.training.rh.rh.domain.events.FeriasAprovadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasConcluidaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasIniciadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasRejeitadaEvent;
import br.org.postalis.training.rh.rh.domain.events.FeriasSolicitadaEvent;
import br.org.postalis.training.rh.rh.domain.events.StatusFerias;
import br.org.postalis.training.rh.shared.domain.AggregateRoot;
import br.org.postalis.training.rh.shared.domain.BusinessRuleException;
import br.org.postalis.training.rh.shared.domain.DomainEvent;
import br.org.postalis.training.rh.shared.domain.UUIDv7;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Ferias extends AggregateRoot {

    private UUID funcionarioId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int diasSolicitados;
    private StatusFerias status;

    // Dados adicionais
    private String aprovadoPor;
    private String rejeitadoPor;
    private String motivoRejeicao;
    private LocalDate dataInicioReal;
    private LocalDate dataRetorno;

    // Construtores protegidos
    //protected Ferias() {
    //    super();
    // }

    public Ferias(UUID id) {
        super(id);
    }

    // === Factory Method ===
    public static Ferias solicitar(
            UUID funcionarioId,
            LocalDate dataInicio,
            LocalDate dataFim) {

        // Validações
        if (funcionarioId == null) {
            throw new IllegalArgumentException("Funcionário é obrigatório");
        }
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("Datas são obrigatórias");
        }
        if (dataInicio.isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Data de início não pode ser no passado");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new BusinessRuleException("Data fim deve ser após data início");
        }

        int dias = (int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        if (dias < 5) {
            throw new BusinessRuleException("Férias mínimas são 5 dias");
        }
        if (dias > 30) {
            throw new BusinessRuleException("Férias máximas são 30 dias");
        }

        Ferias ferias = new Ferias(UUIDv7.generate());

        FeriasSolicitadaEvent event = FeriasSolicitadaEvent.create(
                ferias.getId().toString(),
                funcionarioId,
                dataInicio,
                dataFim,
                dias
        );

        ferias.raise(event);

        return ferias;
    }

    // === Operações de Domínio ===

    public void aprovar(String aprovador, String observacao) {
        if (!status.podeAprovar()) {
            throw new BusinessRuleException(
                    "Férias em status " + status + " não podem ser aprovadas"
            );
        }
        if (aprovador == null || aprovador.isBlank()) {
            throw new IllegalArgumentException("Aprovador é obrigatório");
        }

        FeriasAprovadaEvent event = FeriasAprovadaEvent.create(
                this.getId().toString(),
                (int) getVersion() + 1,
                aprovador,
                observacao
        );

        raise(event);
    }

    public void rejeitar(String rejeitador, String motivo) {
        if (!status.podeRejeitar()) {
            throw new BusinessRuleException(
                    "Férias em status " + status + " não podem ser rejeitadas"
            );
        }
        if (rejeitador == null || rejeitador.isBlank()) {
            throw new IllegalArgumentException("Rejeitador é obrigatório");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo é obrigatório");
        }

        FeriasRejeitadaEvent event = FeriasRejeitadaEvent.create(
                this.getId().toString(),
                (int) getVersion() + 1,
                rejeitador,
                motivo
        );

        raise(event);
    }

    public void iniciarGozo() {
        if (!status.podeIniciarGozo()) {
            throw new BusinessRuleException(
                    "Férias em status " + status + " não podem iniciar gozo"
            );
        }

        FeriasIniciadaEvent event = FeriasIniciadaEvent.create(
                this.getId().toString(),
                (int) getVersion() + 1
        );

        raise(event);
    }

    public void concluir() {
        if (!status.podeConcluir()) {
            throw new BusinessRuleException(
                    "Férias em status " + status + " não podem ser concluídas"
            );
        }

        FeriasConcluidaEvent event = FeriasConcluidaEvent.create(
                this.getId().toString(),
                (int) getVersion() + 1
        );

        raise(event);
    }

    // === Event Handlers ===

    @Override
    protected void apply(DomainEvent event) {
        // Sealed interface garante exaustividade (sem default necessário)
        switch (event) {
            case FeriasSolicitadaEvent e -> on(e);
            case FeriasAprovadaEvent e -> on(e);
            case FeriasRejeitadaEvent e -> on(e);
            case FeriasIniciadaEvent e -> on(e);
            case FeriasConcluidaEvent e -> on(e);
            default -> throw new IllegalArgumentException(
                    "Evento desconhecido: " + event.eventType()
            );
        }
    }

    private void on(FeriasSolicitadaEvent e) {
        this.funcionarioId = e.funcionarioId();
        this.dataInicio = e.dataInicio();
        this.dataFim = e.dataFim();
        this.diasSolicitados = e.diasSolicitados();
        this.status = StatusFerias.SOLICITADA;
    }

    private void on(FeriasAprovadaEvent e) {
        this.aprovadoPor = e.aprovadoPor();
        this.status = StatusFerias.APROVADA;
    }

    private void on(FeriasRejeitadaEvent e) {
        this.rejeitadoPor = e.rejeitadoPor();
        this.motivoRejeicao = e.motivo();
        this.status = StatusFerias.REJEITADA;
    }

    private void on(FeriasIniciadaEvent e) {
        this.dataInicioReal = e.dataInicioReal();
        this.status = StatusFerias.EM_GOZO;
    }

    private void on(FeriasConcluidaEvent e) {
        this.dataRetorno = e.dataRetorno();
        this.status = StatusFerias.CONCLUIDA;
    }

    // === Getters === substituido por @Getter
    /*
    public UUID getFuncionarioId() { return funcionarioId; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public int getDiasSolicitados() { return diasSolicitados; }
    public StatusFerias getStatus() { return status; }
    public String getAprovadoPor() { return aprovadoPor; }
    public String getRejeitadoPor() { return rejeitadoPor; }
    public String getMotivoRejeicao() { return motivoRejeicao; }
    public LocalDate getDataInicioReal() { return dataInicioReal; }
    public LocalDate getDataRetorno() { return dataRetorno; }

     */
}