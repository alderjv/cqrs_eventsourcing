// src/main/java/br/org/postalis/training/rh/rh/application/command/RejeitarFeriasCommand.java
package br.org.postalis.training.rh.rh.application.command;

import br.org.postalis.training.rh.shared.application.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RejeitarFeriasCommand(
        @NotNull UUID feriasId,
        @NotBlank(message = "Rejeitador é obrigatório") String rejeitadoPor,
        @NotBlank(message = "Motivo é obrigatório") String motivo
) implements Command {
}