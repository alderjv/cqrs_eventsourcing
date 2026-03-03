// src/main/java/br/org/postalis/training/rh/rh/application/command/ContratarFuncionarioCommand.java
package br.org.postalis.training.rh.rh.application.command;

import br.org.postalis.training.rh.shared.application.Command;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ContratarFuncionarioCommand(
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "CPF deve ter 11 dígitos")
        String cpf,

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100)
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Cargo é obrigatório")
        String cargo,

        @NotNull(message = "Salário é obrigatório")
        @DecimalMin(value = "0.01", message = "Salário deve ser positivo")
        BigDecimal salario
) implements Command {
}