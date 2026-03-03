// src/main/java/br/org/postalis/training/rh/rh/application/handler/SolicitarFeriasHandler.java
package br.org.postalis.training.rh.rh.application.handler;

import br.org.postalis.training.rh.rh.application.command.SolicitarFeriasCommand;
import br.org.postalis.training.rh.rh.domain.Ferias;
import br.org.postalis.training.rh.rh.infrastructure.FeriasRepository;
import br.org.postalis.training.rh.rh.infrastructure.FuncionarioQueryRepository;
import br.org.postalis.training.rh.shared.application.CommandHandler;
import br.org.postalis.training.rh.shared.domain.BusinessRuleException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitarFeriasHandler implements CommandHandler<SolicitarFeriasCommand> {

    private final FeriasRepository repository;
    private final FuncionarioQueryRepository funcionarioQuery;

    public SolicitarFeriasHandler(
            FeriasRepository repository,
            FuncionarioQueryRepository funcionarioQuery) {
        this.repository = repository;
        this.funcionarioQuery = funcionarioQuery;
    }

    @Override
    @Transactional
    public void handle(SolicitarFeriasCommand command) {
        // Validar que funcionário existe e está ativo
        var funcionario = funcionarioQuery.findById(command.funcionarioId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionário não encontrado: " + command.funcionarioId()));

        if (!funcionario.isAtivo()) {
            throw new BusinessRuleException("Funcionário inativo não pode solicitar férias");
        }

        // Criar férias
        Ferias ferias = Ferias.solicitar(
                command.funcionarioId(),
                command.dataInicio(),
                command.dataFim()
        );

        repository.save(ferias);
    }

    // Retorna o ID das férias criadas
    @Transactional
    public UUID handleReturningId(SolicitarFeriasCommand command) {
        var funcionario = funcionarioQuery.findById(command.funcionarioId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionário não encontrado: " + command.funcionarioId()));

        if (!funcionario.isAtivo()) {
            throw new BusinessRuleException("Funcionário inativo não pode solicitar férias");
        }

        Ferias ferias = Ferias.solicitar(
                command.funcionarioId(),
                command.dataInicio(),
                command.dataFim()
        );

        repository.save(ferias);
        return ferias.getId();
    }
}