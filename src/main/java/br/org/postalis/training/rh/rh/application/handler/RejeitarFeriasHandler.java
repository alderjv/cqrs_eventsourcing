// src/main/java/br/org/postalis/training/rh/rh/application/handler/RejeitarFeriasHandler.java
package br.org.postalis.training.rh.rh.application.handler;

import br.org.postalis.training.rh.rh.application.command.RejeitarFeriasCommand;
import br.org.postalis.training.rh.rh.domain.Ferias;
import br.org.postalis.training.rh.rh.infrastructure.FeriasRepository;
import br.org.postalis.training.rh.shared.application.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RejeitarFeriasHandler implements CommandHandler<RejeitarFeriasCommand> {

    private final FeriasRepository repository;

    public RejeitarFeriasHandler(FeriasRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void handle(RejeitarFeriasCommand command) {
        Ferias ferias = repository.findById(command.feriasId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Férias não encontradas: " + command.feriasId()));

        ferias.rejeitar(command.rejeitadoPor(), command.motivo());

        repository.save(ferias);
    }
}