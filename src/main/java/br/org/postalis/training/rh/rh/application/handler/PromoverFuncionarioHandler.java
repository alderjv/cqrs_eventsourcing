// src/main/java/br/org/postalis/training/rh/rh/application/handler/PromoverFuncionarioHandler.java
package br.org.postalis.training.rh.rh.application.handler;

import br.org.postalis.training.rh.funcionario.domain.Funcionario;
import br.org.postalis.training.rh.rh.infrastructure.FuncionarioRepository;
import br.org.postalis.training.rh.rh.application.command.PromoverFuncionarioCommand;
import br.org.postalis.training.rh.shared.application.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PromoverFuncionarioHandler
        implements CommandHandler<PromoverFuncionarioCommand> {

    private final FuncionarioRepository repository;

    public PromoverFuncionarioHandler(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void handle(PromoverFuncionarioCommand command) {
        // 1. Carregar aggregate (reconstrói de eventos)
        Funcionario funcionario = repository.findById(command.funcionarioId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionário não encontrado: " + command.funcionarioId()));

        // 2. Executar operação de domínio
        funcionario.promover(command.novoCargo(), command.novoSalario());

        // 3. Persistir (eventos + projeção)
        repository.save(funcionario);
    }
}