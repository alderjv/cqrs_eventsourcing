// src/main/java/br/org/postalis/training/rh/rh/application/command/AprovarFeriasCommand.java
package br.org.postalis.training.rh.rh.application.command;

import br.org.postalis.training.rh.shared.application.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AprovarFeriasCommand(
        @NotNull UUID feriasId,
        @NotBlank(message = "Aprovador é obrigatório") String aprovadoPor,
        String observacao
) implements Command {
}