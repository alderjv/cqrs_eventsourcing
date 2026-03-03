// src/main/java/br/org/postalis/training/rh/rh/application/command/PromoverFuncionarioCommand.java
package br.org.postalis.training.rh.rh.application.command;

import br.org.postalis.training.rh.shared.application.Command;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

public record PromoverFuncionarioCommand(

        UUID funcionarioId,

        @NotBlank(message = "Novo cargo é obrigatório")
        String novoCargo,

        @NotNull(message = "Novo salário é obrigatório")
        @DecimalMin(value = "0.01", message = "Salário deve ser positivo")
        BigDecimal novoSalario
) implements Command {}