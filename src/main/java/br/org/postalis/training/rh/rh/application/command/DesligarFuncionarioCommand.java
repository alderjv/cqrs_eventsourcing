// src/main/java/br/org/postalis/training/rh/rh/application/command/DesligarFuncionarioCommand.java
package br.org.postalis.training.rh.rh.application.command;

import br.org.postalis.training.rh.shared.application.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record DesligarFuncionarioCommand(

        UUID funcionarioId,

        @NotBlank(message = "Motivo é obrigatório")
        @Size(min = 10, message = "Motivo deve ter pelo menos 10 caracteres")
        String motivo
) implements Command {
}