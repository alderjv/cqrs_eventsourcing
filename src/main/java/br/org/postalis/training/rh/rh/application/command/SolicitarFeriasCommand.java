// src/main/java/br/org/postalis/training/rh/rh/application/command/SolicitarFeriasCommand.java
package br.org.postalis.training.rh.rh.application.command;

import br.org.postalis.training.rh.shared.application.Command;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record SolicitarFeriasCommand(
        @NotNull(message = "Funcionário é obrigatório")
        UUID funcionarioId,

        @NotNull(message = "Data de início é obrigatória")
        @Future(message = "Data de início deve ser futura")
        LocalDate dataInicio,

        @NotNull(message = "Data de fim é obrigatória")
        @Future(message = "Data de fim deve ser futura")
        LocalDate dataFim
) implements Command {
}