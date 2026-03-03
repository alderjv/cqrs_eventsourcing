// src/main/java/br/org/postalis/training/rh/rh/application/handler/DesligarFuncionarioHandler.java
package br.org.postalis.training.rh.rh.application.handler;

import br.org.postalis.training.rh.funcionario.domain.Funcionario;
import br.org.postalis.training.rh.rh.infrastructure.FuncionarioRepository;
import br.org.postalis.training.rh.rh.application.command.DesligarFuncionarioCommand;
import br.org.postalis.training.rh.shared.application.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DesligarFuncionarioHandler
        implements CommandHandler<DesligarFuncionarioCommand> {

    private final FuncionarioRepository repository;

    public DesligarFuncionarioHandler(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void handle(DesligarFuncionarioCommand command) {
        Funcionario funcionario = repository.findById(command.funcionarioId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionário não encontrado: " + command.funcionarioId()));

        funcionario.desligar(command.motivo());

        repository.save(funcionario);
    }
}