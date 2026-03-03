// src/main/java/br/org/postalis/training/rh/rh/domain/FuncionarioPromovido.java
package br.org.postalis.training.rh.funcionario.domain.events;


import br.org.postalis.training.rh.shared.domain.UUIDv7;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FuncionarioPromovido(
        UUID eventId,
        Instant occurredOn,
        String aggregateId,
        int version,
        String cargoAnterior,
        String cargoNovo,
        BigDecimal salarioAnterior,
        BigDecimal salarioNovo,
        LocalDate dataPromocao
) implements FuncionarioEvent {


    public static FuncionarioPromovido create(
            String aggregateId,
            int version,
            String cargoAnterior,
            String cargoNovo,
            BigDecimal salarioAnterior,
            BigDecimal salarioNovo) {
        return new FuncionarioPromovido(
                UUIDv7.generate(),
                Instant.now(),
                aggregateId,
                version,
                cargoAnterior,
                cargoNovo,
                salarioAnterior,
                salarioNovo,
                LocalDate.now()
        );
    }

    @Override
    public String eventType() {
        return "FuncionarioPromovido";
    }

    @Override
    public String schema() {
        return "rh";
    }
}