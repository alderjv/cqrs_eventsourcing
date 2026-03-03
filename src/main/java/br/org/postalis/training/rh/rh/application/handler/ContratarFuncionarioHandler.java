// src/main/java/br/org/postalis/training/rh/rh/application/handler/ContratarFuncionarioHandler.java
package br.org.postalis.training.rh.rh.application.handler;

import br.org.postalis.training.rh.funcionario.domain.Funcionario;
import br.org.postalis.training.rh.rh.infrastructure.FuncionarioRepository;
import br.org.postalis.training.rh.rh.application.command.ContratarFuncionarioCommand;
import br.org.postalis.training.rh.shared.application.CommandHandler;
import br.org.postalis.training.rh.shared.domain.BusinessRuleException;
import br.org.postalis.training.rh.shared.domain.Cpf;
import br.org.postalis.training.rh.shared.domain.Email;
import br.org.postalis.training.rh.shared.domain.Matricula;
import br.org.postalis.training.rh.shared.infrastructure.messaging.DomainEventPublisher;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ContratarFuncionarioHandler
        implements CommandHandler<ContratarFuncionarioCommand> {

    private final FuncionarioRepository repository;
    private final DomainEventPublisher eventPublisher;

    // Gerador simples de matrícula (em produção seria mais robusto)
    private final AtomicInteger sequencialMatricula = new AtomicInteger((int) (Math.random() * 100_000));

    @Override
    @Transactional
    public void handle(ContratarFuncionarioCommand command) {
        // 1. Validar regras de negócio
        validarCpfUnico(command.cpf());

        // 2. Gerar matrícula
        Matricula matricula = Matricula.gerar(sequencialMatricula.getAndIncrement());

        // 3. Criar Aggregate
        Funcionario funcionario = Funcionario.contratar(
                Cpf.of(command.cpf()),
                command.nome(),
                Email.of(command.email()),
                matricula,
                command.cargo(),
                command.salario()
        );

        var events = List.copyOf(funcionario.getUncommittedEvents());

        // 4. Persistir (Event Store + Projeção)
        repository.save(funcionario);

        // 5. Publicar eventos para listeners assíncronos
        eventPublisher.publishAll(events);
    }

    private void validarCpfUnico(String cpf) {
        // Remover formatação para comparar
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (repository.existsByCpf(cpfLimpo)) {
            throw new BusinessRuleException("CPF já cadastrado: " + cpf);
        }
    }
}