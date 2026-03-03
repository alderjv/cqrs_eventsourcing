// src/main/java/br/org/postalis/training/rh/funcionario/domain/events/FuncionarioContratado.java
package br.org.postalis.training.rh.funcionario.domain.events;

import br.org.postalis.training.rh.shared.domain.EventMetadata;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Evento de contratação de funcionário.
 *
 * Captura TODOS os dados necessários para reconstituir o estado inicial.
 * É o primeiro evento no stream do aggregate Funcionario.
 *
 * Versionamento:
 * - VERSION = 1 (versão atual do schema)
 * - Caso haja mudanças, criar FuncionarioContratadoV2 com handler próprio
 * - Este evento V1 permanece imutável para replay
 */
public record FuncionarioContratadoV1(
        UUID eventId,
        Instant occurredOn,
        UUID funcionarioId,
        String matricula,
        String nome,
        String cpf,
        String email,
        LocalDate dataAdmissao,
        BigDecimal salario,
        String cargo,
        EventMetadata eventMetadata
) implements FuncionarioEvent {

    /**
     * Versão do schema deste evento.
     */
    public static final int VERSION = 1;

    /**
     * Construtor compacto com validações.
     */
    public FuncionarioContratadoV1 {
        Objects.requireNonNull(eventId, "eventId é obrigatório");
        Objects.requireNonNull(occurredOn, "occurredOn é obrigatório");
        Objects.requireNonNull(funcionarioId, "funcionarioId é obrigatório");
        Objects.requireNonNull(matricula, "matricula é obrigatória");
        Objects.requireNonNull(nome, "nome é obrigatório");
        Objects.requireNonNull(cpf, "cpf é obrigatório");
        Objects.requireNonNull(dataAdmissao, "dataAdmissao é obrigatória");
        Objects.requireNonNull(salario, "salario é obrigatório");
        Objects.requireNonNull(cargo, "cargo é obrigatório");

        if (salario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("salario deve ser positivo");
        }
    }

    @Override
    public String aggregateId() {
        return funcionarioId.toString();
    }

    @Override
    public String eventType() {
        return "FuncionarioContratado";
    }

    @Override
    public int version() {
        return VERSION;
    }

    @Override
    public String schema() {
        return "rh";
    }

    @Override
    public EventMetadata metadata() {
        return eventMetadata;
    }
}